import { useState } from 'react'
import { Search } from 'lucide-react'
import { getMetar } from '../api/weather'
import type { MetarDto } from '../types/weather'
import { Card } from './ui/Card'
import { Input } from './ui/Input'
import { Field } from './ui/Field'
import { Button } from './ui/Button'
import { Alert } from './ui/Alert'
import { cx } from './ui/cx'
import MetarDecoded from './MetarDecoded'
import RawMetar from './RawMetar'

type Mode = 'raw' | 'decoded'

export default function WeatherBlock() {
  const [icao, setIcao] = useState('')
  const [time, setTime] = useState('')
  const [mode, setMode] = useState<Mode>('decoded')
  const [metar, setMetar] = useState<MetarDto | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!icao.trim()) return
    setLoading(true)
    setError(null)
    try {
      const result = await getMetar(icao.trim().toUpperCase(), time || undefined)
      setMetar(result)
    } catch {
      setMetar(null)
      setError('Failed to fetch METAR. Check the ICAO code and try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <Card>
      <h2 className="text-xs uppercase tracking-wider text-zinc-500 font-medium mb-3">METAR</h2>

      <form onSubmit={submit} className="grid grid-cols-[120px_1fr_auto_auto] gap-3 items-end mb-4">
        <Field label="ICAO" htmlFor="wx-icao" required>
          <Input
            id="wx-icao"
            value={icao}
            onChange={e => setIcao(e.target.value.toUpperCase())}
            placeholder="EDDS"
            maxLength={4}
            className="font-mono uppercase"
          />
        </Field>

        <Field label="Time (optional)" htmlFor="wx-time">
          <Input
            id="wx-time"
            type="datetime-local"
            value={time}
            onChange={e => setTime(e.target.value)}
            className="[color-scheme:dark]"
          />
        </Field>

        <ModeToggle mode={mode} setMode={setMode} />

        <Button type="submit" disabled={loading || !icao.trim()}>
          <Search size={14} />
          {loading ? 'Loading' : 'Fetch'}
        </Button>
      </form>

      {error && <div className="mb-3"><Alert tone="error">{error}</Alert></div>}

      {metar && (mode === 'raw' ? <RawMetar raw={metar.rawMetar} /> : <MetarDecoded metar={metar} />)}
    </Card>
  )
}

function ModeToggle({ mode, setMode }: { mode: Mode; setMode: (m: Mode) => void }) {
  return (
    <div className="flex border border-zinc-700 rounded-md overflow-hidden">
      {(['decoded', 'raw'] as const).map(m => (
        <button
          key={m}
          type="button"
          onClick={() => setMode(m)}
          className={cx(
            'px-3 py-2 text-xs uppercase tracking-wider cursor-pointer transition-colors',
            mode === m ? 'bg-zinc-800 text-zinc-100' : 'text-zinc-500 hover:text-zinc-300',
          )}
        >
          {m}
        </button>
      ))}
    </div>
  )
}
