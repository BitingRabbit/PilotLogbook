import type { ReactNode } from 'react'
import { Plane } from 'lucide-react'

interface AuthCardProps {
  title: string
  subtitle?: ReactNode
  children: ReactNode
  footer?: ReactNode
}

export default function AuthCard({ title, subtitle, children, footer }: AuthCardProps) {
  return (
    <div className="w-full">
      <div className="flex items-center gap-2 mb-8">
        <Plane size={18} className="text-amber-500" />
        <span className="font-mono text-sm text-zinc-400">pilotlogbook</span>
      </div>

      <h1 className="text-xl text-zinc-100 mb-1">{title}</h1>
      {subtitle && <p className="text-sm text-zinc-500 mb-6">{subtitle}</p>}

      {children}

      {footer && <div className="mt-6 text-sm">{footer}</div>}
    </div>
  )
}
