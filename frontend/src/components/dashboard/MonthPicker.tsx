import { useRef, useState } from 'react'
import { ChevronDown } from 'lucide-react'
import { MONTHS } from '../../constants/flight'
import { useClickOutside } from '../../hooks/useClickOutside'
import { cx } from '../ui/cx'

interface MonthPickerProps {
  value: number | undefined
  onChange: (m: number | undefined) => void
}

export default function MonthPicker({ value, onChange }: MonthPickerProps) {
  const [open, setOpen] = useState(false)
  const ref = useRef<HTMLDivElement>(null)
  useClickOutside(ref, () => setOpen(false), open)

  return (
    <div ref={ref} className="relative">
      <button
        type="button"
        onClick={() => setOpen(o => !o)}
        className="w-full flex items-center justify-between px-3 py-2 rounded-md bg-zinc-900 border border-zinc-700 text-xs cursor-pointer hover:border-zinc-600 transition-colors"
      >
        <span className={value ? 'text-zinc-100' : 'text-zinc-500'}>
          {value ? MONTHS[value - 1] : 'All months'}
        </span>
        <ChevronDown size={12} className={cx('text-zinc-500 transition-transform', open && 'rotate-180')} />
      </button>

      {open && (
        <div className="absolute z-30 top-full mt-1 w-full bg-zinc-900 border border-zinc-700 rounded-md shadow-lg overflow-hidden">
          <button
            type="button"
            onClick={() => { onChange(undefined); setOpen(false) }}
            className="w-full px-3 py-1.5 text-left text-xs text-zinc-500 hover:bg-zinc-800 hover:text-zinc-200 cursor-pointer"
          >
            All months
          </button>
          <div className="grid grid-cols-3 gap-px bg-zinc-800">
            {MONTHS.map((m, i) => (
              <button
                key={m}
                type="button"
                onClick={() => { onChange(i + 1); setOpen(false) }}
                className={cx(
                  'px-2 py-1.5 text-xs cursor-pointer transition-colors',
                  value === i + 1
                    ? 'bg-amber-500/10 text-amber-300'
                    : 'bg-zinc-900 text-zinc-300 hover:bg-zinc-800',
                )}
              >
                {m}
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
