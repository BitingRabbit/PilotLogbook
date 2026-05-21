import { Plus } from 'lucide-react'
import type { FlightResponse } from '../../types/flight'
import FlightListItem from '../FlightListItem'
import { Card } from '../ui/Card'
import FilterBar from './FilterBar'

interface RecentFlightsPanelProps {
  flights: FlightResponse[]
  loading: boolean
  filterDep: string
  filterDest: string
  filterMonth: number | undefined
  onDepChange: (v: string) => void
  onDestChange: (v: string) => void
  onMonthChange: (m: number | undefined) => void
  onSelectFlight: (f: FlightResponse) => void
  onNewFlight: () => void
}

export default function RecentFlightsPanel(props: RecentFlightsPanelProps) {
  const { flights, loading, onSelectFlight, onNewFlight } = props

  return (
    <Card padded={false} className="flex flex-col">
      <header className="flex items-center justify-between px-4 pt-3 pb-2">
        <h2 className="text-xs uppercase tracking-wider text-zinc-500 font-medium">Recent flights</h2>
        <span className="font-mono text-xs text-zinc-600">{flights.length}</span>
      </header>

      <div className="px-4 pb-3 border-b border-zinc-800">
        <FilterBar
          dep={props.filterDep}
          dest={props.filterDest}
          month={props.filterMonth}
          onDepChange={props.onDepChange}
          onDestChange={props.onDestChange}
          onMonthChange={props.onMonthChange}
        />
      </div>

      <div className="overflow-y-auto max-h-[600px] py-1">
        {loading ? (
          <ul className="space-y-1 px-3 py-2">
            {Array.from({ length: 4 }).map((_, i) => (
              <li key={i} className="h-12 bg-zinc-900 animate-pulse rounded" />
            ))}
          </ul>
        ) : flights.length === 0 ? (
          <p className="text-center text-sm text-zinc-600 py-10">No flights found.</p>
        ) : (
          flights.slice(0, 10).map(f => (
            <FlightListItem key={f.id} flight={f} onClick={() => onSelectFlight(f)} />
          ))
        )}
      </div>

      <footer className="px-4 py-3 border-t border-zinc-800">
        <button
          onClick={onNewFlight}
          className="w-full flex items-center justify-center gap-1.5 py-1.5 rounded-md text-xs text-zinc-500 border border-dashed border-zinc-700 hover:text-amber-400 hover:border-amber-500/40 cursor-pointer transition-colors"
        >
          <Plus size={12} />
          Log a flight
        </button>
      </footer>
    </Card>
  )
}
