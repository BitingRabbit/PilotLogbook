import type { FlightResponse } from '../../types/flight'
import { Card, Section } from '../ui/Card'
import { DataField } from '../ui/DataField'
import { formatDateTime, formatDuration } from '../../utils/format'
import { PILOT_FUNCTION_LABELS } from '../../constants/flight'

export default function FlightDetails({ flight }: { flight: FlightResponse }) {
  return (
    <Section title="Flight">
      <Card>
        <div className="grid grid-cols-2 sm:grid-cols-3 gap-x-4 gap-y-3">
          <DataField label="Departure" mono>{formatDateTime(flight.departureTime)}</DataField>
          <DataField label="Arrival" mono>{formatDateTime(flight.arrivalTime)}</DataField>
          <DataField label="Duration" mono>{formatDuration(flight.durationInMinutes)}</DataField>
          <DataField label="Pilot function">{PILOT_FUNCTION_LABELS[flight.pilotFunction] ?? flight.pilotFunction}</DataField>
          <DataField label="Flight type" mono>{flight.flightType}</DataField>
          <DataField label="Landings" mono>{flight.landings}</DataField>
          {flight.passengers != null && <DataField label="Passengers" mono>{flight.passengers}</DataField>}
          {flight.cost != null && <DataField label="Cost" mono>€{flight.cost.toFixed(2)}</DataField>}
        </div>
        {flight.remarks && (
          <div className="mt-4 pt-3 border-t border-zinc-800">
            <p className="text-[10px] uppercase tracking-wider text-zinc-500 mb-1">Remarks</p>
            <p className="text-sm text-zinc-300">{flight.remarks}</p>
          </div>
        )}
      </Card>
    </Section>
  )
}
