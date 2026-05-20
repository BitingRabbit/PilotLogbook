import type { ButtonHTMLAttributes, ReactNode } from 'react'
import { cx } from './cx'

type Variant = 'primary' | 'ghost' | 'danger' | 'outline'
type Size = 'sm' | 'md'

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant
  size?: Size
  children: ReactNode
}

const base =
  'inline-flex items-center justify-center gap-2 font-medium rounded-md ' +
  'transition-colors cursor-pointer disabled:opacity-40 disabled:cursor-not-allowed ' +
  'focus:outline-none focus-visible:ring-2 focus-visible:ring-amber-500/50'

const variants: Record<Variant, string> = {
  primary: 'bg-amber-500 text-zinc-950 hover:bg-amber-400',
  ghost:   'text-zinc-400 hover:text-zinc-100 hover:bg-zinc-800',
  danger:  'text-red-400 hover:text-red-300 hover:bg-red-500/10 border border-red-500/30',
  outline: 'border border-zinc-700 text-zinc-200 hover:bg-zinc-800 hover:border-zinc-600',
}

const sizes: Record<Size, string> = {
  sm: 'px-2.5 py-1.5 text-xs',
  md: 'px-4 py-2 text-sm',
}

export function Button({ variant = 'primary', size = 'md', className, children, ...rest }: ButtonProps) {
  return (
    <button className={cx(base, variants[variant], sizes[size], className)} {...rest}>
      {children}
    </button>
  )
}
