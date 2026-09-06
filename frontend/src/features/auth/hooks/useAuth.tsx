import { createContext, useContext, useState, type ReactNode } from 'react';
import { authService } from '../api/authService';
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
        const storedUser = localStorage.getItem('@jcb:user');
        if (!storedUser) {
            return null;
        }
        try {
            return JSON.parse(storedUser) as UserSession;
        } catch {
            localStorage.removeItem('@jcb:user');
            return null;
        }
    });
    const isLoading = false;

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

    const logout = (): void => {
        localStorage.removeItem('@jcb:user');
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, isAuthenticated: !!user, isLoading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = (): AuthContextData => useContext(AuthContext);
