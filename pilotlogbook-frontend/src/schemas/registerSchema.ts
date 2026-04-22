import { z } from 'zod'

export const registerSchema = z.object({
    firstName: z.string().min(1, 'First Name ist required'),
    lastName: z.string().min(1, 'Last Name ist required'),
    email: z.string().email('Not a valid email'),
    password: z
        .string()
        .min(8, 'Password must be at least 8 characters')
        .regex(/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$/,
            'Password must contain upper, lower case letters and a number'),
})

export type RegisterFormData = z.infer<typeof registerSchema>