import type { FlightResponse } from '../../types/flight'
import { Card, Section } from '../ui/Card'
import { DataField } from '../ui/DataField'

export default function AircraftDetails({ flight }: { flight: FlightResponse }) {
  return (
    <Section title="Aircraft">
      <Card>
        <div className="grid grid-cols-2 sm:grid-cols-3 gap-x-4 gap-y-3">
          <DataField label="Registration" mono>{flight.aircraftRegistration}</DataField>
          <DataField label="Type" mono>{flight.aircraftType}</DataField>
          {flight.aircraftModel && <DataField label="Model">{flight.aircraftModel}</DataField>}
        </div>
      </Card>
    </Section>
  )
}
