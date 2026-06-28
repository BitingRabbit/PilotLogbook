import type { FlightResponse } from '../../types/flight'
import type { WeatherSnapshotResponse } from '../../types/weather'
import { Section } from '../ui/Card'
import WeatherSnapshotCard from '../WeatherSnapshotCard'

interface WeatherSectionProps {
  flight: FlightResponse
  onRefresh: () => void
}

// fill in missing phases so both cards always render
function resolveSnapshots(flight: FlightResponse): WeatherSnapshotResponse[] {
  const byPhase = new Map(flight.weatherSnapshots.map(s => [s.phase, s]))
  return [
    byPhase.get('DEPARTURE') ?? {
      phase: 'DEPARTURE',
      status: 'UNAVAILABLE',
      icao: flight.originAirport.icao,
      metar: null,
    },
    byPhase.get('ARRIVAL') ?? {
      phase: 'ARRIVAL',
      status: 'UNAVAILABLE',
      icao: flight.destinationAirport.icao,
      metar: null,
    },
  ]
}

export default function WeatherSection({ flight, onRefresh }: WeatherSectionProps) {
  const snapshots = resolveSnapshots(flight)
  return (
    <Section title="Weather snapshots">
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
        {snapshots.map(snap => (
          <WeatherSnapshotCard key={`${flight.id}-${snap.phase}`} snapshot={snap} flightId={flight.id} onRefresh={onRefresh} />
        ))}
      </div>
    </Section>
  )
}
