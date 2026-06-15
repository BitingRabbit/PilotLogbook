import type { FlightResponse } from '../../types/flight'
import { Section } from '../ui/Card'
import AirportCard from '../AirportCard'

export default function AirportsSection({ flight }: { flight: FlightResponse }) {
  return (
    <Section title="Airports">
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
        <AirportCard airport={flight.originAirport} label="Origin" />
        <AirportCard airport={flight.destinationAirport} label="Destination" />
      </div>
    </Section>
  )
}
