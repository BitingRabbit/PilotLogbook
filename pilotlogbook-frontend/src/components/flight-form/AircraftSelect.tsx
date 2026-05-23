import { useRef, useState } from 'react'
import { ChevronDown, Trash2 } from 'lucide-react'
import type { AircraftResponse } from '../../types/aircraft'
import { useClickOutside } from '../../hooks/useClickOutside'
import { cx } from '../ui/cx'

interface AircraftSelectProps {
  aircraft: AircraftResponse[]
  value: string
  onChange: (v: string) => void
  onDelete: (id: number) => void
  loading?: boolean
  deletingId?: number | null
}

function describe(a: AircraftResponse) {
  return `${a.registration} — ${a.type}${a.model ? ` ${a.model}` : ''}`
}

export default function AircraftSelect({ aircraft, value, onChange, onDelete, loading = false, deletingId = null }: AircraftSelectProps) {
  const [open, setOpen] = useState(false)
  const ref = useRef<HTMLDivElement>(null)
  useClickOutside(ref, () => setOpen(false), open)

  const selected = aircraft.find(a => String(a.id) === value)
  const fieldBase = 'w-full px-3 py-2 rounded-md bg-zinc-900 border border-zinc-700 text-sm transition-colors focus:outline-none focus:border-amber-500'
  const placeholderText = loading ? 'Loading aircraft…' : 'Select aircraft…'

  return (
    <div ref={ref} className="relative">
      <button
        type="button"
        onClick={() => setOpen(o => !o)}
        className={cx(fieldBase, 'flex items-center justify-between text-left cursor-pointer', selected ? 'text-zinc-100 font-mono' : 'text-zinc-500')}
      >
        <span>{selected ? describe(selected) : placeholderText}</span>
        <ChevronDown size={14} className={cx('text-zinc-500 transition-transform', open && 'rotate-180')} />
      </button>

      {open && (
        <ul className="absolute z-20 mt-1 w-full max-h-60 overflow-y-auto bg-zinc-900 border border-zinc-700 rounded-md shadow-lg">
          {loading && (
            <li className="px-3 py-2 text-xs text-zinc-500 italic">Loading aircraft…</li>
          )}
          {!loading && aircraft.length === 0 && (
            <li className="px-3 py-2 text-xs text-zinc-500 italic">No aircraft yet — add one above.</li>
          )}
          {aircraft.map(a => (
            <li
              key={a.id}
              className={cx(
                'flex items-center justify-between group transition-colors',
                String(a.id) === value ? 'bg-zinc-800 text-zinc-100' : 'text-zinc-300 hover:bg-zinc-800',
              )}
            >
              <button
                type="button"
                onClick={() => { onChange(String(a.id)); setOpen(false) }}
                className="flex-1 text-left px-3 py-2 font-mono text-sm cursor-pointer"
              >
                {describe(a)}
              </button>
              <button
                type="button"
                onClick={() => onDelete(a.id)}
                disabled={deletingId === a.id}
                aria-label={`Delete ${a.registration}`}
                className="mr-2 p-1 text-zinc-600 hover:text-red-400 hover:bg-zinc-900 rounded cursor-pointer opacity-0 group-hover:opacity-100 transition-opacity disabled:opacity-40 disabled:cursor-not-allowed"
              >
                <Trash2 size={12} className={deletingId === a.id ? 'animate-pulse' : ''} />
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}
