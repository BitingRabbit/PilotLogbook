import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useNavigate, useLocation, Link } from 'react-router-dom'
import { useState } from 'react'
import { registerSchema, type RegisterFormData } from '../schemas/registerSchema'
import { registerUser } from '../api/auth'
import { useAuth } from '../context/AuthContext'
import { AuthShell } from '../components/ui/PageShell'
import AuthCard from '../components/auth/AuthCard'
import { Field } from '../components/ui/Field'
import { Input } from '../components/ui/Input'
import { Button } from '../components/ui/Button'
import { Alert } from '../components/ui/Alert'
import { extractErrorMessage, extractFieldErrors } from '../utils/apiError'

export default function RegisterPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const email = (location.state as { email?: string })?.email ?? ''
  const [error, setError] = useState('')

  const { register, handleSubmit, setError: setFormError, formState: { errors, isSubmitting } } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
    defaultValues: { firstName: '', lastName: '', email, password: '' },
  })

  const submit = async (data: RegisterFormData) => {
    setError('')
    try {
      const res = await registerUser(data)
      login(res.data.token)
      navigate('/dashboard')
    } catch (err) {
      const fields = extractFieldErrors(err)
      if (fields) {
        Object.entries(fields).forEach(([key, message]) => {
          setFormError(key as keyof RegisterFormData, { type: 'server', message })
        })
        return
      }
      setError(extractErrorMessage(err, 'Registration failed'))
    }
  }

  return (
    <AuthShell>
      <AuthCard
        title="Create account"
        subtitle={<span className="font-mono">{email}</span>}
        footer={<Link to="/email" className="text-zinc-500 hover:text-zinc-300 transition-colors">← Use a different email</Link>}
      >
        <form onSubmit={handleSubmit(submit)} className="space-y-4">
          <input type="hidden" {...register('email')} />
          <div className="grid grid-cols-2 gap-3">
            <Field label="First name" required error={errors.firstName?.message}>
              <Input placeholder="Jane" autoFocus {...register('firstName')} />
            </Field>
            <Field label="Last name" required error={errors.lastName?.message}>
              <Input placeholder="Doe" {...register('lastName')} />
            </Field>
          </div>
          <Field
            label="Password"
            required
            error={errors.password?.message}
            hint="At least 8 chars, with upper, lower case and a number."
          >
            <Input type="password" placeholder="••••••••" {...register('password')} />
          </Field>
          {error && <Alert tone="error">{error}</Alert>}
          <Button type="submit" disabled={isSubmitting} className="w-full">
            {isSubmitting ? 'Creating…' : 'Create account'}
          </Button>
        </form>
      </AuthCard>
    </AuthShell>
  )
}
