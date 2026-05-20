import { z } from 'zod'

export const editFlightSchema = z.object({
    originIcao: z.string().regex(/^[A-Za-z]{4}$/, 'Must be 4 letters')
        .optional().or(z.literal((''))),
    destinationIcao: z.string().regex(/^[A-Za-z]{4}$/, 'Must be 4 letters')
        .optional().or(z.literal((''))),
    departureTime: z.string().min(1, 'Required')
        .optional().or(z.literal((''))),
    arrivalTime: z.string().min(1, 'Required')
        .optional().or(z.literal((''))),
    aircraftId: z.string().min(1, 'Select an aircraft')
        .optional().or(z.literal((''))),
    pilotFunction: z.enum(['PIC', 'SIC', 'DUAL', 'INSTRUCTOR']).optional(),
    flightType: z.enum(['VFR', 'IFR']).optional(),
    landings: z.string().min(1, 'Required')
        .optional().or(z.literal((''))),
    passengers: z.string().optional(),
    cost: z.string().optional(),
    remarks: z.string().optional(),
})

export type EditFlightFormData = z.infer<typeof editFlightSchema>