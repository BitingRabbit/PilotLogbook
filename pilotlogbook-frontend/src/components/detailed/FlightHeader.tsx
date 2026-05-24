import { Trash2, Pencil } from 'lucide-react'
import type { FlightResponse } from '../../types/flight'
import { Button } from '../ui/Button'
import { Badge } from '../ui/Badge'
import { formatDate } from '../../utils/format'

interface FlightHeaderProps {
  flight: FlightResponse
  deleting: boolean
  onDelete: () => void
  onEdit: () => void
}

export default function FlightHeader({ flight, deleting, onDelete, onEdit }: FlightHeaderProps) {
  return (
    <div className="flex items-start justify-between flex-wrap gap-3">
      <div>
        <h2 className="font-mono text-2xl text-zinc-100 mb-1">
          {flight.originAirport.icao} <span className="text-zinc-600">→</span> {flight.destinationAirport.icao}
        </h2>
        <div className="flex items-center gap-2">
          <Badge tone="amber" mono>{flight.aircraftRegistration}</Badge>
          <Badge tone="neutral">{flight.flightType}</Badge>
          <span className="text-xs text-zinc-500">{formatDate(flight.departureTime)}</span>
        </div>
      </div>
      <Button variant="outline" size="sm" onClick={onEdit}>
        <Pencil size={12} />
          {'Edit'}
      </Button>
      <Button variant="danger" size="sm" onClick={onDelete} disabled={deleting}>
        <Trash2 size={12} />
        {deleting ? 'Deleting…' : 'Delete'}
      </Button>
    </div>
  )
}
