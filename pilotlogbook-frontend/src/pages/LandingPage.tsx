import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'

const floatingPlane = {
  animate: {
    y: [0, -18, 0],
    rotate: [0, 2, -2, 0],
    transition: { duration: 6, repeat: Infinity, ease: 'easeInOut' },
  },
}

const stagger = {
  hidden: {},
  show: { transition: { staggerChildren: 0.15 } },
}

const fadeUp = {
  hidden: { opacity: 0, y: 30 },
  show: { opacity: 1, y: 0, transition: { duration: 0.7, ease: 'easeOut' } },
}

export default function LandingPage() {
  return (
    <div className="min-h-screen relative overflow-hidden bg-gradient-to-br from-slate-950 via-blue-950 to-indigo-950">
      {/* Animated background orbs */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <motion.div
          className="absolute -top-40 -right-40 w-96 h-96 rounded-full bg-blue-500/10 blur-3xl"
          animate={{ scale: [1, 1.2, 1], x: [0, 30, 0], y: [0, -20, 0] }}
          transition={{ duration: 8, repeat: Infinity, ease: 'easeInOut' }}
        />
        <motion.div
          className="absolute -bottom-32 -left-32 w-80 h-80 rounded-full bg-indigo-500/10 blur-3xl"
          animate={{ scale: [1.2, 1, 1.2], x: [0, -20, 0] }}
          transition={{ duration: 10, repeat: Infinity, ease: 'easeInOut' }}
        />
        <motion.div
          className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] rounded-full bg-cyan-500/5 blur-3xl"
          animate={{ scale: [1, 1.1, 1] }}
          transition={{ duration: 12, repeat: Infinity, ease: 'easeInOut' }}
        />
      </div>

      {/* Stars / particles */}
      {[...Array(20)].map((_, i) => (
        <motion.div
          key={i}
          className="absolute w-1 h-1 bg-white/20 rounded-full"
          style={{ left: `${Math.random() * 100}%`, top: `${Math.random() * 100}%` }}
          animate={{ opacity: [0.1, 0.6, 0.1] }}
          transition={{ duration: 2 + Math.random() * 3, repeat: Infinity, delay: Math.random() * 2 }}
        />
      ))}

      {/* Content */}
      <div className="relative z-10 min-h-screen flex flex-col items-center justify-center px-6">
        <motion.div
          variants={stagger}
          initial="hidden"
          animate="show"
          className="text-center max-w-2xl"
        >
          {/* Floating plane icon */}
          <motion.div
            variants={floatingPlane}
            animate="animate"
            className="text-7xl mb-8 inline-block"
          >
            ✈️
          </motion.div>

          <motion.h1
            variants={fadeUp}
            className="text-6xl md:text-7xl font-bold bg-gradient-to-r from-white via-blue-100 to-cyan-200 bg-clip-text text-transparent leading-tight mb-6"
          >
            Pilot Logbook
          </motion.h1>

          <motion.p
            variants={fadeUp}
            className="text-lg md:text-xl text-blue-200/70 mb-4 leading-relaxed"
          >
            Track your flights. Log your hours. Stay current.
          </motion.p>

          <motion.p
            variants={fadeUp}
            className="text-sm text-blue-300/40 mb-10"
          >
            The modern digital logbook for private and commercial pilots.
          </motion.p>

          <motion.div variants={fadeUp}>
            <Link
              to="/email"
              className="group relative inline-flex items-center gap-3 px-10 py-4 text-lg font-semibold text-white rounded-2xl
                         bg-gradient-to-r from-blue-600 to-cyan-500
                         shadow-lg shadow-blue-500/25
                         hover:shadow-xl hover:shadow-blue-500/40
                         hover:scale-105 active:scale-100
                         transition-all duration-300 ease-out"
            >
              <span>Get Started</span>
              <motion.span
                className="inline-block"
                animate={{ x: [0, 4, 0] }}
                transition={{ duration: 1.5, repeat: Infinity, ease: 'easeInOut' }}
              >
                →
              </motion.span>
            </Link>
          </motion.div>

          {/* Feature pills */}
          <motion.div
            variants={fadeUp}
            className="mt-16 flex flex-wrap justify-center gap-3"
          >
            {['Flight Logging', 'Hour Tracking', 'PDF Export', 'Multi-Aircraft'].map((f) => (
              <span
                key={f}
                className="px-4 py-2 text-xs font-medium text-blue-300/60 border border-blue-500/20 rounded-full
                           backdrop-blur-sm bg-white/5"
              >
                {f}
              </span>
            ))}
          </motion.div>
        </motion.div>
      </div>
    </div>
  )
}

