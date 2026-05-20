import { forwardRef, type InputHTMLAttributes, type TextareaHTMLAttributes, type SelectHTMLAttributes } from 'react'
import { cx } from './cx'

const fieldClass =
  'w-full px-3 py-2 rounded-md bg-zinc-900 border border-zinc-700 text-zinc-100 ' +
  'placeholder-zinc-600 text-sm transition-colors ' +
  'focus:outline-none focus:border-amber-500 focus:bg-zinc-900'

export const Input = forwardRef<HTMLInputElement, InputHTMLAttributes<HTMLInputElement>>(
  function Input({ className, ...rest }, ref) {
    return <input ref={ref} className={cx(fieldClass, className)} {...rest} />
  },
)

export const Textarea = forwardRef<HTMLTextAreaElement, TextareaHTMLAttributes<HTMLTextAreaElement>>(
  function Textarea({ className, ...rest }, ref) {
    return <textarea ref={ref} className={cx(fieldClass, 'resize-none', className)} {...rest} />
  },
)

export const Select = forwardRef<HTMLSelectElement, SelectHTMLAttributes<HTMLSelectElement>>(
  function Select({ className, children, ...rest }, ref) {
    return (
      <select ref={ref} className={cx(fieldClass, 'cursor-pointer', className)} {...rest}>
        {children}
      </select>
    )
  },
)
