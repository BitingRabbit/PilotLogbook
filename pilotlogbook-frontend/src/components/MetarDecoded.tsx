import type { MetarDto } from '../types/weather'
import { DataField } from './ui/DataField'

interface MetarDecodedProps {
  metar: MetarDto
}

export default function MetarDecoded({ metar }: MetarDecodedProps) {
  const d = metar.decodedMetar
  if (!d) return null

  const wind = d.wind?.variable
    ? `VRB ${d.wind.speedKt} kt`
    : `${d.wind?.directionDeg ?? '—'}° / ${d.wind?.speedKt ?? '—'} kt${d.wind?.gustKt ? ` G${d.wind.gustKt}` : ''}`

  const visibility = d.visibility?.cavok ? 'CAVOK' : (d.visibility?.value ?? '—')

  const clouds = d.clouds?.length > 0
    ? d.clouds.map(c => `${c.cover} ${c.baseFt}ft`).join(', ')
    : 'Clear'

  return (
    <div className="grid grid-cols-2 sm:grid-cols-3 gap-x-4 gap-y-3">
      <DataField label="Temperature" mono>{d.temperature?.tempC ?? '—'}°C</DataField>
      <DataField label="Dewpoint" mono>{d.temperature?.dewpointC ?? '—'}°C</DataField>
      <DataField label="Wind" mono>{wind}</DataField>
      <DataField label="Visibility" mono>{visibility}</DataField>
      <DataField label="QNH" mono>{d.pressure?.qnhHpa ?? '—'} hPa</DataField>
      <DataField label="Clouds" mono>{clouds}</DataField>
      {d.flightCategory && <DataField label="Category" mono>{d.flightCategory}</DataField>}
    </div>
  )
}
