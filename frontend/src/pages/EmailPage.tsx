import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useNavigate, Link } from 'react-router-dom'
import { useState } from 'react'
import { checkEmail } from '../api/auth'
import { emailSchema, type EmailFormData } from '../schemas/emailSchema'
import { AuthShell } from '../components/ui/PageShell'
import AuthCard from '../components/auth/AuthCard'
import { Field } from '../components/ui/Field'
import { Input } from '../components/ui/Input'
import { Button } from '../components/ui/Button'
import { Alert } from '../components/ui/Alert'

export default function EmailPage() {
  const navigate = useNavigate()
  const [error, setError] = useState('')
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<EmailFormData>({
    resolver: zodResolver(emailSchema),
  })

  const submit = async (data: EmailFormData) => {
    setError('')
    try {
      const res = await checkEmail(data.email)
      navigate(res.data.exists ? '/login' : '/register', { state: { email: data.email } })
    } catch {
      setError('Could not verify email. Please try again.')
    }
  }

  return (
    <AuthShell>
      <AuthCard
        title="Welcome"
        subtitle="Enter your email to continue."
        footer={<Link to="/" className="text-zinc-500 hover:text-zinc-300 transition-colors">← Back</Link>}
      >
        <form onSubmit={handleSubmit(submit)} className="space-y-4">
          <Field label="Email" htmlFor="email" required error={errors.email?.message}>
            <Input id="email" type="email" placeholder="you@example.com" autoFocus {...register('email')} />
          </Field>
          {error && <Alert tone="error">{error}</Alert>}
          <Button type="submit" disabled={isSubmitting} className="w-full">
            {isSubmitting ? 'Checking…' : 'Continue'}
          </Button>
        </form>
      </AuthCard>
    </AuthShell>
  )
}
