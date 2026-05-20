export interface RunwayResponse {
  lengthInFt: number
  widthInFt: number
  hasLights: boolean
}

export interface AirportResponse {
  icao: string
  iata: string
  name: string
  city: string
  country: string
  elevationInFt: number
  latitude: number
  longitude: number
  timezone: string
  size: string
  runways: RunwayResponse[]
}