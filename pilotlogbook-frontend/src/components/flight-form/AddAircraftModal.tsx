import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { createAircraft } from '../../api/aircraft'
import type { AircraftResponse } from '../../types/aircraft'
import { addAircraftSchema, type AddAircraftFormData } from '../../schemas/addAircraftSchema'
import { Modal } from '../ui/Modal'
import { Field } from '../ui/Field'
import { Input, Select } from '../ui/Input'
import { Button } from '../ui/Button'
import { Alert } from '../ui/Alert'
import { extractErrorMessage } from '../../utils/apiError'

interface Props {
  onClose: () => void
  onCreated: (a: AircraftResponse) => void
}

export default function AddAircraftModal({ onClose, onCreated }: Props) {
  const [error, setError] = useState<string | null>(null)
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<AddAircraftFormData>({
    resolver: zodResolver(addAircraftSchema),
    defaultValues: { engineType: 'SINGLE_PISTON' },
  })

  const submit = async (data: AddAircraftFormData) => {
    setError(null)
    try {
      const created = await createAircraft({
        registration: data.registration.toUpperCase(),
        type: data.type.toUpperCase(),
        model: data.model || undefined,
        engineType: data.engineType,
      })
      onCreated(created)
    } catch (e) {
      setError(extractErrorMessage(e, 'Failed to add aircraft.'))
    }
  }

  return (
    <Modal title="Add aircraft" onClose={onClose} size="sm">
      <form onSubmit={handleSubmit(submit)} className="p-4 space-y-3">
        <div className="grid grid-cols-2 gap-3">
          <Field label="Registration" required error={errors.registration?.message}>
            <Input {...register('registration')} placeholder="D-ABCD" className="font-mono uppercase" />
          </Field>
          <Field label="Type" required error={errors.type?.message}>
            <Input {...register('type')} placeholder="C172" maxLength={4} className="font-mono uppercase" />
          </Field>
        </div>
        <Field label="Model">
          <Input {...register('model')} placeholder="Skyhawk (optional)" />
        </Field>
        <Field label="Engine type" required>
          <Select {...register('engineType')}>
            <option value="SINGLE_PISTON">Single piston</option>
            <option value="MULTI_PISTON">Multi piston</option>
            <option value="TURBOPROP">Turboprop</option>
            <option value="JET">Jet</option>
          </Select>
        </Field>

        {error && <Alert tone="error">{error}</Alert>}

        <div className="flex justify-end gap-2 pt-1">
          <Button type="button" variant="ghost" size="sm" onClick={onClose}>Cancel</Button>
          <Button type="submit" size="sm" disabled={isSubmitting}>
            {isSubmitting ? 'Adding…' : 'Add'}
          </Button>
        </div>
      </form>
    </Modal>
  )
}
