import type { AirportResponse } from './airport'
import type { WeatherSnapshotResponse } from './weather'

export type PilotFunction = 'PIC' | 'SIC' | 'DUAL' | 'INSTRUCTOR'
export type FlightType = 'VFR' | 'IFR'

export interface FlightResponse {
  id: number
  departureTime: string
  arrivalTime: string
  durationInMinutes: number
  aircraftId: number
  aircraftRegistration: string
  aircraftType: string
  aircraftModel: string
  passengers: number | null
  landings: number
  pilotFunction: PilotFunction
  flightType: FlightType
  cost: number | null
  remarks: string | null
  createdAt: string
  originAirport: AirportResponse
  destinationAirport: AirportResponse
  weatherSnapshots: WeatherSnapshotResponse[]
}

export interface CreateFlightRequest {
  originIcao: string
  destinationIcao: string
  departureTime: string
  arrivalTime: string
  aircraftId: number
  pilotFunction: PilotFunction
  flightType: FlightType
  landings: number
  passengers?: number
  cost?: number
  remarks?: string
}

export interface UpdateFlightRequest {
  originIcao?: string
  destinationIcao?: string
  departureTime?: string
  arrivalTime?: string
  aircraftId?: number
  pilotFunction?: PilotFunction
  flightType?: FlightType
  landings?: number
  passengers?: number
  cost?: number
  remarks?: string
}