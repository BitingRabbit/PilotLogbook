import { cx } from '../ui/cx'

interface SegmentedControlProps<T extends string> {
  options: readonly T[]
  value: T
  onChange: (v: T) => void
}

export default function SegmentedControl<T extends string>({ options, value, onChange }: SegmentedControlProps<T>) {
  return (
    <div className="flex rounded-md border border-zinc-700 overflow-hidden">
      {options.map(opt => (
        <button
          key={opt}
          type="button"
          onClick={() => onChange(opt)}
          className={cx(
            'flex-1 px-3 py-2 text-xs uppercase tracking-wider cursor-pointer transition-colors',
            value === opt ? 'bg-amber-500/10 text-amber-300' : 'text-zinc-500 hover:text-zinc-300',
          )}
        >
          {opt}
        </button>
      ))}
    </div>
  )
}
