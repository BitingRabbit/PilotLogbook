export interface Wind {
  directionDeg: number | null
  speedKt: number | null
  gustKt: number | null
  variable: boolean
}

export interface Visibility {
  value: string | null
  cavok: boolean
}

export interface Temperature {
  tempC: number | null
  dewpointC: number | null
}

export interface Pressure {
  qnhHpa: number | null
}

export interface CloudLayer {
  cover: string | null
  baseFt: number | null
}

export interface DecodedMetar {
  wind: Wind
  visibility: Visibility
  temperature: Temperature
  pressure: Pressure
  clouds: CloudLayer[]
  phenomena: string[]
  flightCategory: string | null
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