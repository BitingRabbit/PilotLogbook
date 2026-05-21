import api from './axios'
import type { MetarDto } from '../types/weather'

export const getMetar = (icao: string, time?: string) =>
  api.get<MetarDto>('/api/v1/metars', { params: { icao, ...(time ? { time } : {}) } }).then(r => r.data)

export const refreshWeatherSnapshots = (flightId: number) =>
  api.post(`/api/v1/metars/snapshots/refresh/${flightId}`).then(r => r.data)