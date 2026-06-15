export type EngineType = 'SINGLE_PISTON' | 'MULTI_PISTON' | 'TURBOPROP' | 'JET'

export interface AircraftResponse {
    id: number
    registration: string
    type: string
    model: string
}

export interface CreateAircraftPayload {
    registration: string
    type: string
    model?: string
    engineType: EngineType
}
