export interface Wind {
  directionDeg: number | null
  speedKt: number
  gustKt: number | null
  variable: boolean
}

export interface Visibility {
  value: string
  cavok: boolean
}

export interface Temperature {
  tempC: number
  dewpointC: number
}

export interface Pressure {
  qnhHpa: number
}

export interface CloudLayer {
  cover: string
  baseFt: number
}

export interface DecodedMetar {
  wind: Wind
  visibility: Visibility
  temperature: Temperature
  pressure: Pressure
  clouds: CloudLayer[]
  phenomena: string[]
  flightCategory: string
}

export interface MetarDto {
  icao: string
  observationTime: string | null
  rawMetar: string
  decodedMetar: DecodedMetar
}

export interface WeatherSnapshotResponse {
  phase: 'DEPARTURE' | 'ARRIVAL'
  status: 'PENDING' | 'AVAILABLE' | 'UNAVAILABLE'
  icao: string
  metar: MetarDto | null
}