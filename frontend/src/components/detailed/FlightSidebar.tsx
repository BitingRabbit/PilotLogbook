import type { FlightResponse } from '../../types/flight'
import FlightListItem from '../FlightListItem'
import {cx} from "../ui/cx.ts";
import {Menu} from "lucide-react";

interface FlightSidebarProps {
  flights: FlightResponse[]
  loading: boolean
  selectedId: number | undefined
  onSelect: (f: FlightResponse) => void
  open: boolean
  onClose: () => void
  onOpen: () => void
}

export default function FlightSidebar({ flights, loading, selectedId, onSelect, open, onClose, onOpen }: FlightSidebarProps) {
  return (
    <>
    <button type="button"
      onClick={() => onOpen()}
      className="lg:hidden self-start m-3 p-2 rounded text-zinc-400 hover:text-zinc-100 hover:bg-zinc-800 cursor-pointer"
      aria-label="Open flight list"
      >
      <Menu size={18} />
    </button>

      {open && (
      <div
        className="lg:hidden fixed inset-0 z-30 bg-black/60"
        onClick={onClose}
        aria-hidden
        />
    )}
    <aside className={cx(
        // Mobile
        'fixed inset-y-0 left-0 z-40 w-[280px] bg-zinc-950 border-r border-zinc-800 overflow-y-auto',
        'transition-transform duration-200',
        open ? 'translate-x-0' : '-translate-x-full',
        // Desktop: static, immer sichtbar
        'lg:static lg:translate-x-0 lg:w-[320px] lg:shrink-0 lg:z-auto'
      )}
    >
      <header className="px-4 py-3 border-b border-zinc-800 flex items-center justify-between">
        <h2 className="text-xs uppercase tracking-wider text-zinc-500 font-medium">All flights</h2>
        <span className="font-mono text-xs text-zinc-600">{flights.length}</span>
      </header>

      {loading ? (
        <ul className="p-3 space-y-1">
          {Array.from({ length: 6 }).map((_, i) => (
            <li key={i} className="h-14 bg-zinc-900 animate-pulse rounded" />
          ))}
        </ul>
      ) : flights.length === 0 ? (
        <p className="text-center text-sm text-zinc-600 py-10 px-4">No flights logged yet.</p>
      ) : (
        <ul>
          {flights.map(f => (
            <li key={f.id} className="border-b border-zinc-900 last:border-b-0">
              <FlightListItem flight={f} onClick={() => { onSelect(f); onClose() }} isSelected={selectedId === f.id} />
            </li>
          ))}
        </ul>
      )}
    </aside>
    </>
  )
}
