import api from './axios'
import type {FlightResponse, CreateFlightRequest, UpdateFlightRequest} from '../types/flight'

export const getFlights = () =>
  api.get<FlightResponse[]>('/api/v1/flights').then(r => r.data)

export const getDashboardFlights = (params: { dep?: string; dest?: string; month?: number }) => {
  const cleaned: Record<string, string | number> = {}
  if (params.dep) cleaned.dep = params.dep
  if (params.dest) cleaned.dest = params.dest
  if (params.month) cleaned.month = params.month
  return api.get<FlightResponse[]>('/api/v1/flights/dashboard', { params: cleaned }).then(r => r.data)
}

export const createFlight = (data: CreateFlightRequest) =>
  api.post<FlightResponse>('/api/v1/flights', data).then(r => r.data)

export const deleteFlight = (id: number) =>
  api.delete(`/api/v1/flights/${id}`)

export const updateFlight = (id: number, data: UpdateFlightRequest) =>
    api.patch(`/api/v1/flights/${id}`, data).then(r => r.data)