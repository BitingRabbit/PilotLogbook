import type { ReactNode, HTMLAttributes } from 'react'
import { cx } from './cx'

interface CardProps extends HTMLAttributes<HTMLDivElement> {
  children: ReactNode
  padded?: boolean
}

export function Card({ children, padded = true, className, ...rest }: CardProps) {
  return (
    <div
      className={cx(
        'bg-zinc-900/60 border border-zinc-800 rounded-md',
        padded && 'p-4',
        className,
      )}
      {...rest}
    >
      {children}
    </div>
  )
}

interface SectionProps {
  title: string
  action?: ReactNode
  children: ReactNode
}

export function Section({ title, action, children }: SectionProps) {
  return (
    <section>
      <div className="flex items-center justify-between mb-2">
        <h3 className="text-xs uppercase tracking-wider text-zinc-500 font-medium">{title}</h3>
        {action}
      </div>
      {children}
    </section>
  )
}
