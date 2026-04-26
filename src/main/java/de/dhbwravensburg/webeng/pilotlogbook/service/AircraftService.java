package de.dhbwravensburg.webeng.pilotlogbook.service;

import de.dhbwravensburg.webeng.pilotlogbook.dto.request.CreateAircraftRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.request.UpdateAircraftRequest;
import de.dhbwravensburg.webeng.pilotlogbook.dto.response.AircraftResponse;
import de.dhbwravensburg.webeng.pilotlogbook.exception.ConflictException;
import de.dhbwravensburg.webeng.pilotlogbook.exception.ResourceNotFoundException;
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

/**
 * Business logic for aircraft CRUD operations.
 * All operations are implicitly scoped to the authenticated pilot via {@link CurrentPilotProvider}.
 */
@Service
@RequiredArgsConstructor
public class AircraftService {

    private final AircraftRepository aircraftRepository;
    private final FlightRepository flightRepository;
    private final CurrentPilotProvider currentPilotProvider;
    private final CurrentAircraftProvider currentAircraftProvider;

    /**
     * Creates a new aircraft for the current pilot.
     * Registration is unique per pilot and stored in uppercase.
     *
     * @param request aircraft details
     * @return the persisted aircraft
     * @throws ConflictException if the pilot already owns an aircraft with the same registration
     */
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

    /**
     * Returns all aircraft owned by the current pilot.
     *
     * @return list of aircraft responses
     */
    @Transactional(readOnly = true)
    public List<AircraftResponse> getAllAircraft() {
        Pilot pilot = currentPilotProvider.get();

        return aircraftRepository.findByPilotId(pilot.getId())
                .stream()
                .map(AircraftResponse::from)
                .toList();
    }

    /**
     * Returns a single aircraft by ID, verifying ownership by the current pilot.
     *
     * @param aircraftId aircraft ID
     * @return the aircraft response
     * @throws ResourceNotFoundException if the aircraft does not exist or is not owned by the pilot
     */
    @Transactional(readOnly = true)
    public AircraftResponse getAircraftById(Long aircraftId) {
        Pilot pilot = currentPilotProvider.get();
        Aircraft aircraft = currentAircraftProvider.get(aircraftId, pilot.getId());
        return AircraftResponse.from(aircraft);
    }

    /**
     * Partially updates an aircraft. Only non-null fields in the request are applied.
     * Registration uniqueness is re-checked if the registration is being changed.
     *
     * @param aircraftId aircraft ID
     * @param request    fields to update
     * @return the updated aircraft response
     * @throws ConflictException if the new registration conflicts with an existing one for this pilot
     */
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

    /**
     * Deletes an aircraft owned by the current pilot.
     *
     * @param aircraftId aircraft ID
     * @throws ConflictException if the aircraft has associated flights
     */
    @Transactional
    public void deleteAircraft(Long aircraftId) {
        Pilot pilot = currentPilotProvider.get();
        Aircraft aircraft = currentAircraftProvider.get(aircraftId, pilot.getId());

        if (flightRepository.existsByAircraftId(aircraftId)) {
            throw new ConflictException(
                    "Cannot delete aircraft with existing flights. Remove all associated flights first.");
        }

        aircraftRepository.delete(aircraft);
    }

}