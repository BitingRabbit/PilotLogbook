import { Input } from '../ui/Input'
import MonthPicker from './MonthPicker'

interface FilterBarProps {
  dep: string
  dest: string
  month: number | undefined
  onDepChange: (v: string) => void
  onDestChange: (v: string) => void
  onMonthChange: (m: number | undefined) => void
}

export default function FilterBar({ dep, dest, month, onDepChange, onDestChange, onMonthChange }: FilterBarProps) {
  return (
    <div className="grid grid-cols-2 gap-2">
      <Input
        type="text"
        placeholder="DEP"
        value={dep}
        onChange={e => onDepChange(e.target.value)}
        maxLength={4}
        aria-label="Filter by origin ICAO"
        className="font-mono uppercase text-xs"
      />
      <Input
        type="text"
        placeholder="DEST"
        value={dest}
        onChange={e => onDestChange(e.target.value)}
        maxLength={4}
        aria-label="Filter by destination ICAO"
        className="font-mono uppercase text-xs"
      />
      <div className="col-span-2">
        <MonthPicker value={month} onChange={onMonthChange} />
      </div>
    </div>
  )
}
