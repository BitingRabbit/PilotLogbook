package de.dhbwravensburg.webeng.pilotlogbook.service;

import de.dhbwravensburg.webeng.pilotlogbook.dto.request.CreateAircraftRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.request.UpdateAircraftRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.AircraftResponse;
import de.dhbwravensburg.webeng.pilotlogbook.exception.ConflictException;
import de.dhbwravensburg.webeng.pilotlogbook.model.Aircraft;
import de.dhbwravensburg.webeng.pilotlogbook.model.Pilot;
import de.dhbwravensburg.webeng.pilotlogbook.repository.AircraftRepository;
import de.dhbwravensburg.webeng.pilotlogbook.repository.FlightRepository;
import de.dhbwravensburg.webeng.pilotlogbook.util.CurrentAircraftProvider;
import de.dhbwravensburg.webeng.pilotlogbook.util.CurrentPilotProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AircraftService {

    private final AircraftRepository aircraftRepository;
    private final FlightRepository flightRepository;
    private final CurrentPilotProvider currentPilotProvider;
    private final CurrentAircraftProvider currentAircraftProvider;

    @Transactional
    public AircraftResponse createAircraft(CreateAircraftRequest request) {
        Pilot pilot = currentPilotProvider.get();

        if (aircraftRepository.existsByRegistrationAndPilotId(request.getRegistration(), pilot.getId())) {
            throw new ConflictException("Aircraft with this registration already exists.");
        }

        Aircraft aircraft = new Aircraft(
                pilot,
                request.getRegistration().toUpperCase(),
                request.getType().toUpperCase(),
                request.getModel(),
                request.getEngineType()
        );

        return AircraftResponse.from(aircraftRepository.save(aircraft));
    }

    @Transactional(readOnly = true)
    public List<AircraftResponse> getAllAircraft() {
        Pilot pilot = currentPilotProvider.get();

        return aircraftRepository.findByPilotId(pilot.getId())
                .stream()
                .map(AircraftResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AircraftResponse getAircraftById(Long aircraftId) {
        Pilot pilot = currentPilotProvider.get();
        Aircraft aircraft = currentAircraftProvider.get(aircraftId, pilot.getId());
        return AircraftResponse.from(aircraft);
    }

    @Transactional
    public AircraftResponse updateAircraft(Long aircraftId, UpdateAircraftRequest request) {
        Pilot pilot = currentPilotProvider.get();
        Aircraft aircraft = currentAircraftProvider.get(aircraftId, pilot.getId());

        if (request.getRegistration() != null) {
            String newReg = request.getRegistration().toUpperCase();
            if (!newReg.equals(aircraft.getRegistration()) &&
                    aircraftRepository.existsByRegistrationAndPilotId(newReg, pilot.getId())) {
                throw new ConflictException("Aircraft with this registration already exists.");
            }
            aircraft.setRegistration(newReg);
        }

        if (request.getType() != null) {
            aircraft.setType(request.getType().toUpperCase());
        }

        if (request.getModel() != null) {
            aircraft.setModel(request.getModel());
        }

        if (request.getEngineType() != null) {
            aircraft.setEngineType(request.getEngineType());
        }

        return AircraftResponse.from(aircraftRepository.save(aircraft));
    }

    @Transactional
    public void deleteAircraft(Long aircraftId) {
        Pilot pilot = currentPilotProvider.get();
        Aircraft aircraft = currentAircraftProvider.get(aircraftId, pilot.getId());

        boolean hasFlights = flightRepository.existsByAircraftId(aircraftId);
        if (hasFlights) {
            throw new IllegalStateException(
                    "Cannot delete aircraft with existing flights. " +
                            "Remove all associated flights first");
        }

        aircraftRepository.delete(aircraft);
    }

}