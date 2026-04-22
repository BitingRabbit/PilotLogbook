import {createContext, useContext, useState, type ReactNode, useMemo} from 'react'
import { saveToken, getToken, removeToken } from '../utils/token'

interface AuthContextType {
    token: string | null
    isAuthenticated: boolean
    login: (token: string) => void
    logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
    const [token, setToken] = useState<string | null>(getToken())

    const login = (newToken: string) => {
        saveToken(newToken)
        setToken(newToken)
    }

    const logout = () => {
        removeToken()
        setToken(null)
    }

    const isAuthenticated = !!token
    return (
        <AuthContext.Provider value={{ token, isAuthenticated, login, logout }}>
            {children}
        </AuthContext.Provider>
    )
}

export function useAuth() {
    const context = useContext(AuthContext)
    if (!context) throw new Error('useAuth must be used within AuthProvider')
    return context
}