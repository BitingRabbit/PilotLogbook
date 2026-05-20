import type { ReactNode } from 'react'
import { cx } from './cx'

type Tone = 'neutral' | 'amber' | 'green' | 'yellow' | 'red' | 'blue'

const tones: Record<Tone, string> = {
  neutral: 'bg-zinc-800 text-zinc-300 border-zinc-700',
  amber:   'bg-amber-500/10 text-amber-300 border-amber-500/30',
  green:   'bg-emerald-500/10 text-emerald-300 border-emerald-500/30',
  yellow:  'bg-yellow-500/10 text-yellow-300 border-yellow-500/30',
  red:     'bg-red-500/10 text-red-300 border-red-500/30',
  blue:    'bg-sky-500/10 text-sky-300 border-sky-500/30',
}

interface BadgeProps {
  tone?: Tone
  mono?: boolean
  children: ReactNode
}

export function Badge({ tone = 'neutral', mono, children }: BadgeProps) {
  return (
    <span
      className={cx(
        'inline-flex items-center px-1.5 py-0.5 text-[10px] uppercase tracking-wider',
        'border rounded-sm font-medium',
        mono && 'font-mono normal-case tracking-normal',
        tones[tone],
      )}
    >
      {children}
    </span>
  )
}
