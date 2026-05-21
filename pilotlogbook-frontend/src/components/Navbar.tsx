import { Link, NavLink, useNavigate } from 'react-router-dom'
import { Plane, LogOut, Plus } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { cx } from './ui/cx'

interface NavbarProps {
  onNewFlight: () => void
}

const navLink = ({ isActive }: { isActive: boolean }) =>
  cx(
    'px-3 py-1.5 text-sm transition-colors cursor-pointer rounded',
    isActive
      ? 'text-amber-400'
      : 'text-zinc-400 hover:text-zinc-100',
  )

export default function Navbar({ onNewFlight }: NavbarProps) {
  const { logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  return (
    <>
      <nav className="border-b border-zinc-800 bg-zinc-950 sticky top-0 z-40">
        <div className="max-w-screen-2xl mx-auto px-4 h-14 flex items-center justify-between gap-4">
          <Link to="/dashboard" aria-label="Home" className="flex items-center gap-2 text-zinc-100 hover:text-amber-400 transition-colors">
            <Plane size={16} className="text-amber-500" />
            <span className="font-mono text-sm tracking-tight hidden sm:inline">pilotlogbook</span>
          </Link>

          <div className="flex items-center gap-1">
            <NavLink to="/flights" className={navLink}>Logbook</NavLink>
          </div>

          <div className="flex items-center gap-1">
            <button
              onClick={handleLogout}
              aria-label="Logout"
              className="p-2 rounded text-zinc-500 hover:text-red-400 hover:bg-zinc-800 cursor-pointer transition-colors"
            >
              <LogOut size={16} />
            </button>
            <button
              onClick={onNewFlight}
              className="ml-2 inline-flex items-center gap-1.5 px-3 py-1.5 rounded-md bg-amber-500 text-zinc-950 text-sm font-medium hover:bg-amber-400 cursor-pointer transition-colors"
            >
              <Plus size={14} />
              <span className="hidden sm:inline">New flight</span>
            </button>
          </div>
        </div>
      </nav>
    </>
  )
}
