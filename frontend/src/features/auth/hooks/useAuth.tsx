import { createContext, useCallback, useContext, useEffect, useState, type ReactNode } from 'react';
import { authService } from '../api/authService';
import { getTokenExpiration, readStoredSession } from '../session';
import type { UserSession } from '../types';

interface AuthContextData {
    user: UserSession | null;
    isAuthenticated: boolean;
    isLoading: boolean;
    login: (usuario: string, senha: string) => Promise<void>;
    logout: () => void;
}

const AuthContext = createContext<AuthContextData>({} as AuthContextData);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [user, setUser] = useState<UserSession | null>(() => {
        return readStoredSession(localStorage) as UserSession | null;
    });
    const isLoading = false;

    const logout = useCallback((): void => {
        localStorage.removeItem('@jcb:user');
        setUser(null);
    }, []);

    useEffect(() => {
        if (!user) return undefined;

        const expiration = getTokenExpiration(user.token);
        if (expiration === null) {
            const timeoutId = window.setTimeout(logout, 0);
            return () => window.clearTimeout(timeoutId);
        }

        const delay = expiration * 1000 - Date.now();
        if (delay <= 0) {
            const timeoutId = window.setTimeout(logout, 0);
            return () => window.clearTimeout(timeoutId);
        }

        const timeoutId = window.setTimeout(logout, delay);
        return () => window.clearTimeout(timeoutId);
    }, [logout, user]);

    const login = async (usuario: string, senha: string): Promise<void> => {
        const resposta = await authService.login({ login: usuario, senha });
        const session: UserSession = {
            login: resposta.usuario,
            nome: resposta.usuario.toUpperCase(),
            token: resposta.token,
        };

        localStorage.setItem('@jcb:user', JSON.stringify(session));
        setUser(session);
    };

    return (
        <AuthContext.Provider value={{ user, isAuthenticated: !!user, isLoading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = (): AuthContextData => useContext(AuthContext);
