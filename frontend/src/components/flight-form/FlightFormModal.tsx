import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { createFlight, updateFlight } from '../../api/flights'
import { getAircraft, deleteAircraft } from '../../api/aircraft'
import type { AircraftResponse } from '../../types/aircraft'
import type { FlightResponse, UpdateFlightRequest } from '../../types/flight'
import { newFlightSchema } from '../../schemas/newFlightSchema'
import { editFlightSchema, type EditFlightFormData } from '../../schemas/editFlightSchema'
import { toLocalUtcString, formatDateTime } from '../../utils/format'
import { extractErrorMessage } from '../../utils/apiError'
import { Modal } from '../ui/Modal'
import { Field } from '../ui/Field'
import { Input, Select, Textarea } from '../ui/Input'
import { Button } from '../ui/Button'
import { Alert } from '../ui/Alert'
import AircraftSelect from './AircraftSelect'
import AddAircraftModal from './AddAircraftModal'
import SegmentedControl from './SegmentedControl'

interface FlightFormModalProps {
  flight?: FlightResponse
  onClose: () => void
  onSuccess: () => void
}

const FLIGHT_TYPES = ['VFR', 'IFR'] as const

export default function FlightFormModal({ flight, onClose, onSuccess }: FlightFormModalProps) {
  const isEdit = flight !== undefined

  const [aircraft, setAircraft] = useState<AircraftResponse[]>([])
  const [loadingAircraft, setLoadingAircraft] = useState(true)
  const [deletingAircraftId, setDeletingAircraftId] = useState<number | null>(null)
  const [submitError, setSubmitError] = useState<string | null>(null)
  const [deleteError, setDeleteError] = useState<string | null>(null)
  const [showAddAircraft, setShowAddAircraft] = useState(false)

  const { register, handleSubmit, setValue, watch, formState: { errors, isSubmitting } } = useForm<EditFlightFormData>({
    resolver: zodResolver(isEdit ? editFlightSchema : newFlightSchema),
    defaultValues: isEdit
      ? {
          originIcao: '', destinationIcao: '', departureTime: '', arrivalTime: '',
          landings: '', passengers: '', cost: '', remarks: '',
          aircraftId: String(flight.aircraftId),
          pilotFunction: flight.pilotFunction,
          flightType: flight.flightType,
        }
      : { pilotFunction: 'PIC', flightType: 'VFR', landings: '1' },
  })

  const selectedAircraftId = watch('aircraftId') ?? ''
  const flightType = watch('flightType') ?? 'VFR'

  useEffect(() => {
    getAircraft()
      .then(setAircraft)
      .catch(() => {})
      .finally(() => setLoadingAircraft(false))
  }, [])

  const handleDeleteAircraft = async (id: number) => {
    setDeleteError(null)
    setDeletingAircraftId(id)
    try {
      await deleteAircraft(id)
      setAircraft(prev => prev.filter(a => a.id !== id))
      if (selectedAircraftId === String(id)) setValue('aircraftId', '')
    } catch (e) {
      setDeleteError(extractErrorMessage(e, 'Failed to delete aircraft.'))
    } finally {
      setDeletingAircraftId(null)
    }
  }

  const submit = async (data: EditFlightFormData) => {
    setSubmitError(null)
    try {
      if (isEdit) {
        const payload = buildPatchPayload(data, flight)
        if (Object.keys(payload).length === 0) {
          onClose()
          return
        }
        await updateFlight(flight.id, payload)
      } else {
        await createFlight({
          originIcao: data.originIcao!.toUpperCase(),
          destinationIcao: data.destinationIcao!.toUpperCase(),
          departureTime: toLocalUtcString(data.departureTime!),
          arrivalTime: toLocalUtcString(data.arrivalTime!),
          aircraftId: Number(data.aircraftId),
          pilotFunction: data.pilotFunction!,
          flightType: data.flightType!,
          landings: Number(data.landings),
          ...(data.passengers ? { passengers: Number(data.passengers) } : {}),
          ...(data.cost ? { cost: Number(data.cost) } : {}),
          ...(data.remarks ? { remarks: data.remarks } : {}),
        })
      }
      onSuccess()
    } catch (err) {
      setSubmitError(extractErrorMessage(err, isEdit ? 'Failed to update flight.' : 'Failed to create flight.'))
    }
  }

  return (
    <>
      <Modal title={isEdit ? 'Edit flight' : 'Log new flight'} onClose={onClose} size="lg">
        <form onSubmit={handleSubmit(submit)} className="p-4 space-y-4">
          <div className="grid grid-cols-2 gap-3">
            <Field label="Origin ICAO" htmlFor="originIcao" required={!isEdit} error={errors.originIcao?.message}>
              <Input
                id="originIcao"
                {...register('originIcao')}
                placeholder={isEdit ? flight.originAirport.icao : 'EDDS'}
                maxLength={4}
                className="font-mono uppercase"
              />
            </Field>
            <Field label="Destination ICAO" htmlFor="destinationIcao" required={!isEdit} error={errors.destinationIcao?.message}>
              <Input
                id="destinationIcao"
                {...register('destinationIcao')}
                placeholder={isEdit ? flight.destinationAirport.icao : 'EDDM'}
                maxLength={4}
                className="font-mono uppercase"
              />
            </Field>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <Field
              label="Departure (local)"
              htmlFor="departureTime"
              required={!isEdit}
              hint={isEdit ? `Current: ${formatDateTime(flight.departureTime)}` : undefined}
              error={errors.departureTime?.message}
            >
              <Input id="departureTime" type="datetime-local" {...register('departureTime')} className="[color-scheme:dark]" />
            </Field>
            <Field
              label="Arrival (local)"
              htmlFor="arrivalTime"
              required={!isEdit}
              hint={isEdit ? `Current: ${formatDateTime(flight.arrivalTime)}` : undefined}
              error={errors.arrivalTime?.message}
            >
              <Input id="arrivalTime" type="datetime-local" {...register('arrivalTime')} className="[color-scheme:dark]" />
            </Field>
          </div>

          <Field label="Aircraft" required={!isEdit} error={errors.aircraftId?.message}>
            <div className="flex flex-col gap-1.5">
              <AircraftSelect
                aircraft={aircraft}
                value={selectedAircraftId}
                onChange={v => setValue('aircraftId', v, { shouldValidate: true })}
                onDelete={handleDeleteAircraft}
                loading={loadingAircraft}
                deletingId={deletingAircraftId}
              />
              <button
                type="button"
                onClick={() => setShowAddAircraft(true)}
                className="self-start text-xs text-amber-400 hover:text-amber-300 cursor-pointer"
              >
                + Add aircraft
              </button>
              {deleteError && <Alert tone="error">{deleteError}</Alert>}
            </div>
          </Field>

          <div className="grid grid-cols-2 gap-3">
            <Field label="Pilot function" htmlFor="pilotFunction" required={!isEdit}>
              <Select id="pilotFunction" {...register('pilotFunction')}>
                <option value="PIC">PIC</option>
                <option value="SIC">SIC</option>
                <option value="DUAL">Dual</option>
                <option value="INSTRUCTOR">Instructor</option>
              </Select>
            </Field>
            <Field label="Flight type" required={!isEdit}>
              <SegmentedControl
                options={FLIGHT_TYPES}
                value={flightType}
                onChange={v => setValue('flightType', v, { shouldValidate: true })}
              />
            </Field>
          </div>

          <div className="grid grid-cols-3 gap-3">
            <Field label="Landings" htmlFor="landings" required={!isEdit} error={errors.landings?.message}>
              <Input
                id="landings"
                type="number"
                min="0"
                {...register('landings')}
                placeholder={isEdit ? String(flight.landings) : undefined}
                className="font-mono"
              />
            </Field>
            <Field label="Passengers" htmlFor="passengers">
              <Input
                id="passengers"
                type="number"
                min="0"
                {...register('passengers')}
                placeholder={isEdit && flight.passengers != null ? String(flight.passengers) : undefined}
                className="font-mono"
              />
            </Field>
            <Field label="Cost (€)" htmlFor="cost">
              <Input
                id="cost"
                type="number"
                min="0"
                step="0.01"
                {...register('cost')}
                placeholder={isEdit && flight.cost != null ? flight.cost.toFixed(2) : undefined}
                className="font-mono"
              />
            </Field>
          </div>

          <Field label="Remarks" htmlFor="remarks">
            <Textarea
              id="remarks"
              {...register('remarks')}
              rows={2}
              placeholder={isEdit ? (flight.remarks ?? 'Optional notes…') : 'Optional notes…'}
            />
          </Field>

          {submitError && <Alert tone="error">{submitError}</Alert>}

          <div className="flex justify-end gap-2 pt-1">
            <Button type="button" variant="ghost" onClick={onClose}>Cancel</Button>
            <Button type="submit" disabled={isSubmitting}>
              {isEdit
                ? (isSubmitting ? 'Saving…' : 'Save changes')
                : (isSubmitting ? 'Logging…' : 'Log flight')}
            </Button>
          </div>
        </form>
      </Modal>

      {showAddAircraft && (
        <AddAircraftModal
          onClose={() => setShowAddAircraft(false)}
          onCreated={a => {
            setAircraft(prev => [...prev, a])
            setValue('aircraftId', String(a.id), { shouldValidate: true })
            setShowAddAircraft(false)
          }}
        />
      )}
    </>
  )
}

// Builds PATCH body containing only fields the user actually changed
function buildPatchPayload(data: EditFlightFormData, original: FlightResponse): UpdateFlightRequest {
  const payload: UpdateFlightRequest = {}

  if (data.originIcao)      payload.originIcao      = data.originIcao.toUpperCase()
  if (data.destinationIcao) payload.destinationIcao = data.destinationIcao.toUpperCase()
  if (data.departureTime)   payload.departureTime   = toLocalUtcString(data.departureTime)
  if (data.arrivalTime)     payload.arrivalTime     = toLocalUtcString(data.arrivalTime)
  if (data.landings)        payload.landings        = Number(data.landings)
  if (data.passengers)      payload.passengers      = Number(data.passengers)
  if (data.cost)            payload.cost            = Number(data.cost)
  if (data.remarks)         payload.remarks         = data.remarks

  if (data.aircraftId && Number(data.aircraftId) !== original.aircraftId) {
    payload.aircraftId = Number(data.aircraftId)
  }
  if (data.pilotFunction && data.pilotFunction !== original.pilotFunction) {
    payload.pilotFunction = data.pilotFunction
  }
  if (data.flightType && data.flightType !== original.flightType) {
    payload.flightType = data.flightType
  }

  return payload
}