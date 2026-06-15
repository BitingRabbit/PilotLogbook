import type { ReactNode } from 'react'
import { cx } from './cx'

interface AlertProps {
  tone?: 'error' | 'info' | 'success'
  children: ReactNode
}

const tones = {
  error:   'border-red-500/40 bg-red-500/10 text-red-300',
  info:    'border-zinc-700 bg-zinc-800/50 text-zinc-300',
  success: 'border-emerald-500/40 bg-emerald-500/10 text-emerald-300',
}

export function Alert({ tone = 'info', children }: AlertProps) {
  return (
    <div role="alert" className={cx('px-3 py-2 rounded-md border text-sm', tones[tone])}>
      {children}
    </div>
  )
}
