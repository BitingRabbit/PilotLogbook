import type { FlightResponse } from '../types/flight'
import { formatDate, formatDuration } from '../utils/format'
import { cx } from './ui/cx'

interface FlightListItemProps {
  flight: FlightResponse
  onClick: () => void
  isSelected?: boolean
}

export default function FlightListItem({ flight, onClick, isSelected }: FlightListItemProps) {
  return (
    <button
      onClick={onClick}
      className={cx(
        'w-full text-left px-3 py-2.5 border-l-2 transition-colors cursor-pointer',
        'hover:bg-zinc-900',
        isSelected
          ? 'border-amber-500 bg-zinc-900'
          : 'border-transparent',
      )}
    >
      <div className="flex justify-between items-baseline">
        <span className="font-mono text-sm text-zinc-100">
          {flight.originAirport.icao} → {flight.destinationAirport.icao}
        </span>
        <span className="font-mono text-xs text-zinc-500">
          {formatDuration(flight.durationInMinutes)}
        </span>
      </div>
      <div className="flex justify-between items-baseline mt-0.5">
        <span className="font-mono text-xs text-zinc-500">{flight.aircraftRegistration}</span>
        <span className="text-xs text-zinc-500">{formatDate(flight.departureTime)}</span>
      </div>
    </button>
  )
}
