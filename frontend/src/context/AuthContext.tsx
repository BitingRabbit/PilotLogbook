import {createContext, useState, type ReactNode} from 'react'
import { saveToken, getToken, removeToken } from '../utils/token'

export interface AuthContextType {
    token: string | null
    isAuthenticated: boolean
    login: (token: string) => void
    logout: () => void
}

// eslint-disable-next-line react-refresh/only-export-components
export const AuthContext = createContext<AuthContextType | undefined>(undefined)

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

