import type { AirportResponse } from '../types/airport'
import { Card } from './ui/Card'
import { Badge } from './ui/Badge'

interface AirportCardProps {
  airport: AirportResponse
  label: 'Origin' | 'Destination'
}

export default function AirportCard({ airport, label }: AirportCardProps) {
  return (
    <Card>
      <div className="flex items-baseline gap-2 mb-2">
        <Badge tone={label === 'Origin' ? 'amber' : 'neutral'}>{label}</Badge>
        <span className="font-mono text-zinc-100 font-medium">{airport.icao}</span>
        {airport.iata && <span className="font-mono text-xs text-zinc-500">/ {airport.iata}</span>}
      </div>

      <p className="text-sm text-zinc-200">{airport.name}</p>
      <p className="text-xs text-zinc-500 mb-3">{airport.city}, {airport.country}</p>

      <div className="grid grid-cols-2 gap-2 text-xs">
        <div>
          <p className="text-[10px] uppercase tracking-wider text-zinc-500">Elevation</p>
          <p className="font-mono text-zinc-300">{airport.elevationInFt} ft</p>
        </div>
        <div>
          <p className="text-[10px] uppercase tracking-wider text-zinc-500">Timezone</p>
          <p className="text-zinc-300 truncate">{airport.timezone}</p>
        </div>
      </div>

      {airport.runways.length > 0 && (
        <div className="mt-3 pt-3 border-t border-zinc-800">
          <p className="text-[10px] uppercase tracking-wider text-zinc-500 mb-1.5">Runways</p>
          <ul className="space-y-0.5 font-mono text-xs">
            {airport.runways.map((rwy, i) => (
              <li key={i} className="flex gap-3 text-zinc-400">
                <span>{rwy.lengthInFt} × {rwy.widthInFt} ft</span>
                {rwy.hasLights && <span className="text-amber-400">lit</span>}
              </li>
            ))}
          </ul>
        </div>
      )}
    </Card>
  )
}
