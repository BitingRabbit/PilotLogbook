import { isAxiosError } from 'axios'

interface ApiErrorBody {
  error?: string
  fields?: Record<string, string>
}

export function extractErrorMessage(err: unknown, fallback = 'Something went wrong'): string {
  if (isAxiosError(err)) {
    if (err.code === 'ERR_NETWORK') return 'Network error: please check your connection'
    return (err.response?.data as ApiErrorBody | undefined)?.error ?? fallback
  }
  return fallback
}

export function extractFieldErrors(err: unknown): Record<string, string> | null {
  if (isAxiosError(err)) {
    return (err.response?.data as ApiErrorBody | undefined)?.fields ?? null
  }
  return null
}