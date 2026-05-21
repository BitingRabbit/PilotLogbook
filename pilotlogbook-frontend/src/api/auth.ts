import api from "./axios"
import type {
    LoginRequest,
    RegisterRequest,
    AuthResponse,
} from "../types/auth"

export const checkEmail = (email: string) =>
    api.get<{ exists: boolean }>("/api/v1/auth/check-email", { params: { email } })

export const registerUser = (data: RegisterRequest) =>
    api.post<AuthResponse>("/api/v1/auth/register", data)

export const loginUser = (data: LoginRequest) =>
    api.post<AuthResponse>("/api/v1/auth/login", data)