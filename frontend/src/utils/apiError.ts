import { isAxiosError } from 'axios'

// RFC 9457 ProblemDetail body: `detail` carries the message, `title` the short
// summary; `fields` is our extension for per-field validation errors.
interface ApiErrorBody {
  detail?: string
  title?: string
  fields?: Record<string, string>
}

export function extractErrorMessage(err: unknown, fallback = 'Something went wrong'): string {
  if (isAxiosError(err)) {
    if (err.code === 'ERR_NETWORK') return 'Network error: please check your connection'
    const data = err.response?.data as ApiErrorBody | undefined
    return data?.detail ?? data?.title ?? fallback
  }
  return fallback
}

export function extractFieldErrors(err: unknown): Record<string, string> | null {
  if (isAxiosError(err)) {
    return (err.response?.data as ApiErrorBody | undefined)?.fields ?? null
  }
  return null
}