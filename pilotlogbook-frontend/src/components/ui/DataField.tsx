import type { ReactNode } from 'react'
import { cx } from './cx'

interface DataFieldProps {
  label: string
  children: ReactNode
  mono?: boolean
}

export function DataField({ label, children, mono }: DataFieldProps) {
  return (
    <div>
      <p className="text-[10px] uppercase tracking-wider text-zinc-500 mb-0.5">{label}</p>
      <p className={cx('text-sm text-zinc-100', mono && 'font-mono')}>{children}</p>
    </div>
  )
}
