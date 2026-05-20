import { useEffect, type ReactNode } from 'react'
import { X } from 'lucide-react'
import { cx } from './cx'

interface ModalProps {
  title: string
  onClose: () => void
  children: ReactNode
  size?: 'sm' | 'md' | 'lg'
}

const sizes = {
  sm: 'max-w-sm',
  md: 'max-w-md',
  lg: 'max-w-2xl',
}

export function Modal({ title, onClose, children, size = 'md' }: ModalProps) {
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => { if (e.key === 'Escape') onClose() }
    document.addEventListener('keydown', onKey)
    return () => document.removeEventListener('keydown', onKey)
  }, [onClose])

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70"
      onClick={e => { if (e.target === e.currentTarget) onClose() }}
    >
      <div
        className={cx(
          'w-full bg-zinc-900 border border-zinc-800 rounded-md shadow-xl',
          'max-h-[90dvh] overflow-y-auto',
          sizes[size],
        )}
      >
        <header className="flex items-center justify-between px-4 py-3 border-b border-zinc-800 sticky top-0 bg-zinc-900 z-10">
          <h2 className="text-sm font-medium text-zinc-100">{title}</h2>
          <button
            onClick={onClose}
            aria-label="Close"
            className="p-1 rounded text-zinc-500 hover:text-zinc-200 hover:bg-zinc-800 cursor-pointer transition-colors"
          >
            <X size={16} />
          </button>
        </header>
        {children}
      </div>
    </div>
  )
}
