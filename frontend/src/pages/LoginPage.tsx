import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useNavigate, useLocation, Link } from 'react-router-dom'
import { useState } from 'react'
import { loginSchema, type LoginFormData } from '../schemas/loginSchema'
import { loginUser } from '../api/auth'
import { useAuth } from '../context/useAuth'
import { AuthShell } from '../components/ui/PageShell'
import AuthCard from '../components/auth/AuthCard'
import { Field } from '../components/ui/Field'
import { Input } from '../components/ui/Input'
import { Button } from '../components/ui/Button'
import { Alert } from '../components/ui/Alert'
import { extractErrorMessage, extractFieldErrors } from '../utils/apiError'

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

  const submit = async (data: LoginFormData) => {
    setError('')
    try {
      const res = await loginUser(data)
      login(res.data.token)
      navigate('/dashboard')
    } catch (err) {
      const fields = extractFieldErrors(err)
      if (fields?.password) {
        setFormError('password', { type: 'server', message: fields.password })
        return
      }
      setError(extractErrorMessage(err, 'Invalid email or password'))
    }
  }

  return (
    <AuthShell>
      <AuthCard
        title="Welcome back"
        subtitle={<span className="font-mono">{email}</span>}
        footer={<Link to="/email" className="text-zinc-500 hover:text-zinc-300 transition-colors">← Use a different email</Link>}
      >
        <form onSubmit={handleSubmit(submit)} className="space-y-4">
          <input type="hidden" {...register('email')} />
          <Field label="Password" htmlFor="password" required error={errors.password?.message}>
            <Input id="password" type="password" placeholder="••••••••" autoFocus {...register('password')} />
          </Field>
          {error && <Alert tone="error">{error}</Alert>}
          <Button type="submit" disabled={isSubmitting} className="w-full">
            {isSubmitting ? 'Signing in…' : 'Sign in'}
          </Button>
        </form>
      </AuthCard>
    </AuthShell>
  )
}
