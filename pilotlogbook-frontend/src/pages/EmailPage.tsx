import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { checkEmail } from '../api/auth'
import { useNavigate, Link } from 'react-router-dom'
import { useState } from 'react'
import { motion } from 'framer-motion'

const emailSchema = z.object({
  email: z.string().email('Not a valid email'),
})

type EmailFormData = z.infer<typeof emailSchema>

const fadeUp = {
  hidden: { opacity: 0, y: 20 },
  show: { opacity: 1, y: 0, transition: { duration: 0.5, ease: 'easeOut' } },
}

export default function EmailPage() {
  const navigate = useNavigate()
  const [error, setError] = useState('')

  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<EmailFormData>({
    resolver: zodResolver(emailSchema),
  })

  const onSubmit = async (data: EmailFormData) => {
    try {
      setError('')
      const res = await checkEmail(data.email)
      if (res.data.exists) {
        navigate('/login', { state: { email: data.email } })
      } else {
        navigate('/register', { state: { email: data.email } })
      }
    } catch {
      setError('Could not verify email. Please try again.')
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-950 via-blue-950 to-indigo-950 px-4">
      {/* Background orbs */}
      <motion.div
        className="absolute top-20 right-20 w-72 h-72 rounded-full bg-blue-500/10 blur-3xl"
        animate={{ scale: [1, 1.2, 1] }}
        transition={{ duration: 8, repeat: Infinity }}
      />
      <motion.div
        className="absolute bottom-20 left-20 w-64 h-64 rounded-full bg-indigo-500/10 blur-3xl"
        animate={{ scale: [1.2, 1, 1.2] }}
        transition={{ duration: 10, repeat: Infinity }}
      />

      <motion.div
        initial="hidden"
        animate="show"
        variants={{ show: { transition: { staggerChildren: 0.1 } } }}
        className="relative z-10 w-full max-w-md"
      >
        <motion.div
          variants={fadeUp}
          className="p-8 rounded-3xl border border-white/10 bg-white/5 backdrop-blur-2xl shadow-2xl shadow-black/20"
        >
          <motion.div variants={fadeUp} className="text-center mb-8">
            <div className="text-4xl mb-4">✈️</div>
            <h1 className="text-2xl font-bold text-white mb-2">Welcome</h1>
            <p className="text-blue-200/60 text-sm">Enter your email to continue</p>
          </motion.div>

          {error && (
            <motion.p
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              className="text-red-400 text-sm text-center mb-4 p-3 rounded-xl bg-red-500/10 border border-red-500/20"
            >
              {error}
            </motion.p>
          )}

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
            <motion.div variants={fadeUp}>
              <input
                {...register('email')}
                type="email"
                placeholder="Email"
                autoFocus
                className="w-full px-4 py-3 rounded-xl bg-white/10 border border-white/10
                           text-white placeholder-blue-200/30
                           focus:outline-none focus:ring-2 focus:ring-cyan-400/50 focus:border-transparent
                           transition-all duration-300"
              />
              {errors.email && <p className="text-red-400 text-xs mt-2 text-center">{errors.email.message}</p>}
            </motion.div>

            <motion.div variants={fadeUp}>
              <button
                type="submit"
                disabled={isSubmitting}
                className="w-full py-3 rounded-xl font-semibold text-white
                           bg-gradient-to-r from-blue-600 to-cyan-500
                           shadow-lg shadow-blue-500/25
                           hover:shadow-xl hover:shadow-blue-500/40 hover:scale-[1.02]
                           active:scale-100 disabled:opacity-50
                           transition-all duration-300"
              >
                {isSubmitting ? (
                  <span className="flex items-center justify-center gap-2">
                    <motion.span
                      className="inline-block w-4 h-4 border-2 border-white/30 border-t-white rounded-full"
                      animate={{ rotate: 360 }}
                      transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
                    />
                    Checking...
                  </span>
                ) : 'Continue'}
              </button>
            </motion.div>
          </form>

          <motion.p variants={fadeUp} className="text-sm text-center mt-6">
            <Link to="/" className="text-blue-300/50 hover:text-blue-200 transition-colors duration-300">
              ← Back to home
            </Link>
          </motion.p>
        </motion.div>
      </motion.div>
    </div>
  )
}

