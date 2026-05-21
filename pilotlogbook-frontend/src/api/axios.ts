import axios from "axios"

const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
    headers: { "Content-Type": "application/json" },
})

api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

// Auto-logout when the server returns 401 (expired/invalid JWT)
api.interceptors.response.use(
    response => response,
    error => {
        const isAuthRequest = error.config?.url?.includes('/api/v1/auth')
        if (error.response?.status === 401 && !isAuthRequest) {
            localStorage.removeItem('token')
            window.location.href = '/email'
        }
        return Promise.reject(error)
    }
)

export default api

