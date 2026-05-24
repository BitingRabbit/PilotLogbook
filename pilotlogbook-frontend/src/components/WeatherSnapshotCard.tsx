import { useState } from 'react'
import { RefreshCw } from 'lucide-react'
import { refreshWeatherSnapshots } from '../api/weather'
import { extractErrorMessage } from '../utils/apiError'
import type { WeatherSnapshotResponse } from '../types/weather'
import { Card } from './ui/Card'
import { Badge } from './ui/Badge'
import MetarDecoded from './MetarDecoded'
import RawMetar from './RawMetar'

interface WeatherSnapshotCardProps {
  snapshot: WeatherSnapshotResponse
  flightId: number
  onRefresh: () => void
}

const STATUS_TONE = {
  AVAILABLE: 'green',
  PENDING: 'yellow',
  UNAVAILABLE: 'red',
} as const

const PHASE_LABEL = {
  DEPARTURE: 'Departure',
  ARRIVAL: 'Arrival',
} as const

export default function WeatherSnapshotCard({ snapshot, flightId, onRefresh }: WeatherSnapshotCardProps) {
  const [refreshing, setRefreshing] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleRefresh = async () => {
    setRefreshing(true)
    setError(null)
    try {
      await refreshWeatherSnapshots(flightId)
      onRefresh()
    } catch (e) {
      setError(extractErrorMessage(e, 'Refresh failed'))
    } finally {
      setRefreshing(false)
    }
  }

  return (
    <Card>
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center gap-2">
          <span className="text-sm text-zinc-200">{PHASE_LABEL[snapshot.phase]}</span>
          <span className="font-mono text-xs text-zinc-500">{snapshot.icao}</span>
          <Badge tone={STATUS_TONE[snapshot.status]}>{snapshot.status}</Badge>
        </div>
        {snapshot.status !== 'AVAILABLE' && (
          <button
            onClick={handleRefresh}
            disabled={refreshing}
            aria-label="Refresh weather snapshot"
            className="p-1 rounded text-zinc-500 hover:text-zinc-200 hover:bg-zinc-800 disabled:opacity-40 cursor-pointer transition-colors"
          >
            <RefreshCw size={13} className={refreshing ? 'animate-spin' : ''} />
          </button>
        )}
      </div>

      {snapshot.status === 'PENDING' && (
        <p className="text-xs text-zinc-500">Fetching weather data…</p>
      )}
      {snapshot.status === 'UNAVAILABLE' && (
        <p className="text-xs text-zinc-500">
          No METAR available for this time window. NOAA keeps about 7 days of historical data.
        </p>
      )}

      {error && <p className="mt-2 text-xs text-red-400">{error}</p>}

      {snapshot.status === 'AVAILABLE' && snapshot.metar && (
        <div className="space-y-3">
          <RawMetar raw={snapshot.metar.rawMetar} />
          <MetarDecoded metar={snapshot.metar} />
        </div>
      )}
    </Card>
  )
}
