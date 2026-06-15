import api from './axios'
import type { AircraftResponse, CreateAircraftPayload } from '../types/aircraft'

export const getAircraft = () =>
  api.get<AircraftResponse[]>('/api/v1/aircraft').then(r => r.data)

export const createAircraft = (payload: CreateAircraftPayload) =>
  api.post<AircraftResponse>('/api/v1/aircraft', payload).then(r => r.data)

export const deleteAircraft = (id: number) =>
  api.delete(`/api/v1/aircraft/${id}`)
