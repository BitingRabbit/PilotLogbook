import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { loginSchema, type LoginFormData } from '../schemas/loginSchema'
import { loginUser } from '../api/auth'
import { useAuth } from '../context/AuthContext'
import { useNavigate, useLocation, Link } from 'react-router-dom'
import { useState } from 'react'
import { motion } from 'framer-motion'

const fadeUp = {
  hidden: { opacity: 0, y: 20 },
  show: { opacity: 1, y: 0, transition: { duration: 0.5, ease: 'easeOut' } },
}

export default function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const email = (location.state as { email?: string })?.email ?? ''
  const [error, setError] = useState('')

  const { register, handleSubmit, setError: setFormError, formState: { errors, isSubmitting } } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: { email, password: '' },
  })

  const onSubmit = async (data: LoginFormData) => {
    try {
      setError('')
      const res = await loginUser(data)
      login(res.data.token)
      navigate('/dashboard')
    } catch (err: unknown) {
      const responseData = (err as { response?: { data?: { error?: string; fields?: { password?: string } } } })
        ?.response?.data

      if (responseData?.fields?.password) {
        setFormError('password', { type: 'server', message: responseData.fields.password })
        return
      }

      setError(responseData?.error || 'Invalid password')
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-950 via-blue-950 to-indigo-950 px-4">
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
            <motion.div
              className="inline-flex items-center justify-center w-16 h-16 rounded-2xl bg-gradient-to-br from-blue-500/20 to-cyan-500/20 border border-white/10 mb-4"
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              transition={{ type: 'spring', stiffness: 200, delay: 0.2 }}
            >
              <span className="text-2xl">🔐</span>
            </motion.div>
            <h1 className="text-2xl font-bold text-white mb-2">Welcome back</h1>
            <p className="text-blue-200/60 text-sm">{email}</p>
          </motion.div>

          {error && (
            <motion.p
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              className="text-red-400 text-sm text-center mb-4 p-3 rounded-xl bg-red-500/10 border border-red-500/20"
            >
              {error}
            </motion.p>
          )}

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
            <input type="hidden" {...register('email')} />

            <motion.div variants={fadeUp}>
              <input
                {...register('password')}
                type="password"
                placeholder="Password"
                autoFocus
                className="w-full px-4 py-3 rounded-xl bg-white/10 border border-white/10
                           text-white placeholder-blue-200/30
                           focus:outline-none focus:ring-2 focus:ring-cyan-400/50 focus:border-transparent
                           transition-all duration-300"
              />
              {errors.password && (
                <motion.p
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  className="text-red-400 text-xs mt-2"
                >
                  {errors.password.message}
                </motion.p>
              )}
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
                    Logging in...
                  </span>
                ) : 'Login'}
              </button>
            </motion.div>
          </form>

          <motion.p variants={fadeUp} className="text-sm text-center mt-6">
            <Link to="/email" className="text-blue-300/50 hover:text-blue-200 transition-colors duration-300">
              ← Use a different email
            </Link>
          </motion.p>
        </motion.div>
      </motion.div>
    </div>
  )
}

