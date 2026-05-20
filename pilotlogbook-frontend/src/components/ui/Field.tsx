import type { ReactNode } from 'react'

interface FieldProps {
  label: string
  htmlFor?: string
  required?: boolean
  error?: string
  hint?: string
  children: ReactNode
}

export function Field({ label, htmlFor, required, error, hint, children }: FieldProps) {
  return (
    <div className="flex flex-col gap-1">
      <label
        htmlFor={htmlFor}
        className="text-[11px] uppercase tracking-wider text-zinc-500 font-medium"
      >
        {label}{required && <span className="text-amber-500 ml-0.5">*</span>}
      </label>
      {children}
      {error && <p className="text-xs text-red-400">{error}</p>}
      {!error && hint && <p className="text-xs text-zinc-500">{hint}</p>}
    </div>
  )
}
