import { z } from 'zod'

export const newFlightSchema = z.object({
    originIcao: z.string().regex(/^[A-Za-z]{4}$/, 'Must be 4 letters'),
    destinationIcao: z.string().regex(/^[A-Za-z]{4}$/, 'Must be 4 letters'),
    departureTime: z.string().min(1, 'Required'),
    arrivalTime: z.string().min(1, 'Required'),
    aircraftId: z.string().min(1, 'Select an aircraft'),
    pilotFunction: z.enum(['PIC', 'SIC', 'DUAL', 'INSTRUCTOR']),
    flightType: z.enum(['VFR', 'IFR']),
    landings: z.string().min(1, 'Required'),
    passengers: z.string().optional(),
    cost: z.string().optional(),
    remarks: z.string().optional(),
})

export type NewFlightFormData = z.infer<typeof newFlightSchema>
