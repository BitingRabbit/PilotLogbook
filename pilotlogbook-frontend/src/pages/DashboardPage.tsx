import { useAuth } from '../context/AuthContext'
import { useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'

const fadeUp = {
  hidden: { opacity: 0, y: 20 },
  show: { opacity: 1, y: 0, transition: { duration: 0.6, ease: 'easeOut' } },
}

const stats = [
  { label: 'Total Flights', value: '—', icon: '🛫' },
  { label: 'Flight Hours', value: '—', icon: '⏱️' },
  { label: 'Aircraft', value: '—', icon: '✈️' },
  { label: 'Airports', value: '—', icon: '🗺️' },
]

export default function DashboardPage() {
  const { logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-950 via-blue-950 to-indigo-950 px-4 py-8">
      {/* Background */}
      <motion.div
        className="fixed top-20 right-20 w-72 h-72 rounded-full bg-blue-500/10 blur-3xl pointer-events-none"
        animate={{ scale: [1, 1.2, 1] }}
        transition={{ duration: 8, repeat: Infinity }}
      />

      <motion.div
        initial="hidden"
        animate="show"
        variants={{ show: { transition: { staggerChildren: 0.1 } } }}
        className="relative z-10 max-w-4xl mx-auto"
      >
        {/* Header */}
        <motion.div variants={fadeUp} className="flex items-center justify-between mb-10">
          <div>
            <h1 className="text-3xl font-bold text-white mb-1">Welcome, Pilot! ✈️</h1>
            <p className="text-blue-200/50 text-sm">Your logbook at a glance</p>
          </div>
          <button
            onClick={handleLogout}
            className="px-5 py-2 text-sm font-medium rounded-xl text-red-300/70 border border-red-500/20
                       bg-red-500/5 hover:bg-red-500/15 hover:text-red-300
                       transition-all duration-300"
          >
            Logout
          </button>
        </motion.div>

        {/* Stats Grid */}
        <motion.div variants={fadeUp} className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-10">
          {stats.map((stat, i) => (
            <motion.div
              key={stat.label}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.3 + i * 0.1 }}
              className="p-5 rounded-2xl border border-white/10 bg-white/5 backdrop-blur-xl
                         hover:bg-white/8 transition-all duration-300 group"
            >
              <span className="text-2xl mb-2 block group-hover:scale-110 transition-transform duration-300">{stat.icon}</span>
              <p className="text-2xl font-bold text-white mb-1">{stat.value}</p>
              <p className="text-blue-200/40 text-xs">{stat.label}</p>
            </motion.div>
          ))}
        </motion.div>

        {/* Recent Flights Placeholder */}
        <motion.div
          variants={fadeUp}
          className="p-8 rounded-3xl border border-white/10 bg-white/5 backdrop-blur-xl text-center"
        >
          <motion.div
            animate={{ y: [0, -8, 0] }}
            transition={{ duration: 3, repeat: Infinity, ease: 'easeInOut' }}
            className="text-5xl mb-4"
          >
            📋
          </motion.div>
          <h2 className="text-xl font-semibold text-white mb-2">No flights logged yet</h2>
          <p className="text-blue-200/40 text-sm mb-6">Start logging your flights to see them here.</p>
          <button
            className="px-6 py-3 rounded-xl font-semibold text-white
                       bg-gradient-to-r from-blue-600 to-cyan-500
                       shadow-lg shadow-blue-500/25
                       hover:shadow-xl hover:shadow-blue-500/40 hover:scale-[1.02]
                       transition-all duration-300 opacity-50 cursor-not-allowed"
            disabled
          >
            Log a Flight (coming soon)
          </button>
        </motion.div>
      </motion.div>
    </div>
  )
}

