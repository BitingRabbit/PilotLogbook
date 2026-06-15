import api from './axios'
import type { AirportResponse } from '../types/airport'

export const getAirport = (icao: string) =>
  api.get<AirportResponse>(`/api/v1/airports/${icao}`).then(r => r.data)

export const getAirports = (icaos: string[]) =>
  api.get<AirportResponse[]>('/api/v1/airports', { params: { icaos: icaos.join(',') } }).then(r => r.data)