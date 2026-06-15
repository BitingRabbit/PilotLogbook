import { z } from 'zod'

export const addAircraftSchema = z.object({
    registration: z.string().min(4).max(6),
    type: z.string().min(2).max(4),
    model: z.string().max(50).optional(),
    engineType: z.enum(['SINGLE_PISTON', 'MULTI_PISTON', 'TURBOPROP', 'JET']),
})

export type AddAircraftFormData = z.infer<typeof addAircraftSchema>
