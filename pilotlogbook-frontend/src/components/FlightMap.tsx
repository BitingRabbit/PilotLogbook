import { useEffect, useMemo } from 'react'
import { MapContainer, TileLayer, Polyline, CircleMarker, Tooltip, useMap } from 'react-leaflet'
import L from 'leaflet'
import type { FlightResponse } from '../types/flight'

const ROUTE_COLOR = '#f59e0b'      // amber-500
const ORIGIN_COLOR = '#fbbf24'     // amber-400
const DEST_COLOR = '#a78bfa'       // violet-400

function MapBoundsController({ flights }: { flights: FlightResponse[] }) {
  const map = useMap()
  useEffect(() => {
    if (flights.length === 0) return
    const coords: [number, number][] = flights.flatMap(f => [
      [f.originAirport.latitude, f.originAirport.longitude],
      [f.destinationAirport.latitude, f.destinationAirport.longitude],
    ])
    if (coords.length > 0) {
      map.fitBounds(L.latLngBounds(coords), { padding: [40, 40], maxZoom: 12 })
    }
  }, [flights, map])
  return null
}

interface FlightMapProps {
  flights: FlightResponse[]
  height?: string
}

export default function FlightMap({ flights, height = 'h-[400px]' }: FlightMapProps) {
  const validFlights = useMemo(
    () => flights.filter(f => f.originAirport?.latitude != null && f.destinationAirport?.latitude != null),
    [flights],
  )

  const airports = useMemo(() => {
    const seen = new Map<string, { icao: string; name: string; lat: number; lng: number; isOrigin: boolean }>()
    for (const f of validFlights) {
      const o = f.originAirport
      const d = f.destinationAirport
      if (!seen.has(o.icao)) seen.set(o.icao, { icao: o.icao, name: o.name, lat: o.latitude, lng: o.longitude, isOrigin: true })
      if (!seen.has(d.icao)) seen.set(d.icao, { icao: d.icao, name: d.name, lat: d.latitude, lng: d.longitude, isOrigin: false })
    }
    return [...seen.values()]
  }, [validFlights])

  return (
    <div className={`${height} relative z-0 isolate rounded-md overflow-hidden border border-zinc-800`}>
      <MapContainer center={[50, 10]} zoom={5} style={{ height: '100%', width: '100%' }} zoomControl>
        <TileLayer
          url="https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png"
          attribution='&copy; OSM &copy; CARTO'
          maxZoom={20}
        />
        {validFlights.length > 0 && <MapBoundsController flights={validFlights} />}

        {validFlights.map(f => (
          <Polyline
            key={f.id}
            positions={[
              [f.originAirport.latitude, f.originAirport.longitude],
              [f.destinationAirport.latitude, f.destinationAirport.longitude],
            ]}
            pathOptions={{ color: ROUTE_COLOR, weight: 1.5, opacity: 0.65, dashArray: '4 4' }}
          />
        ))}

        {airports.map(a => (
          <CircleMarker
            key={a.icao}
            center={[a.lat, a.lng]}
            radius={5}
            pathOptions={{
              color: a.isOrigin ? ORIGIN_COLOR : DEST_COLOR,
              fillColor: a.isOrigin ? ORIGIN_COLOR : DEST_COLOR,
              fillOpacity: 0.9,
              weight: 1,
            }}
          >
            <Tooltip>{a.icao} - {a.name}</Tooltip>
          </CircleMarker>
        ))}
      </MapContainer>
    </div>
  )
}

export function MapLegend() {
  return (
    <div className="flex items-center gap-4 px-1 text-[11px] text-zinc-500">
      <span className="flex items-center gap-1.5">
        <span className="w-5 border-t border-dashed border-amber-500" />
        Route
      </span>
      <span className="flex items-center gap-1.5">
        <span className="w-2 h-2 rounded-full bg-amber-400" />
        Origin
      </span>
      <span className="flex items-center gap-1.5">
        <span className="w-2 h-2 rounded-full bg-violet-400" />
        Destination
      </span>
    </div>
  )
}
