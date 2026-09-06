import { createContext, useContext, useState, useEffect, type ReactNode } from 'react';

export interface UserSession {
    login: string;
    nome: string;
    perfil: 'admin' | 'GERENTE' | 'CAPATAZ';
}

interface AuthContextData {
    user: UserSession | null;
    isAuthenticated: boolean;
    isLoading: boolean;
    loginLocal: (usuario: string) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextData>({} as AuthContextData);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [user, setUser] = useState<UserSession | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(true);

    useEffect(() => {
        const storedUser = localStorage.getItem('@jcb:user');
        if (storedUser) {
            try {
                setUser(JSON.parse(storedUser) as UserSession);
            } catch {
                localStorage.removeItem('@jcb:user');
            }
        }
        setIsLoading(false);
    }, []);

    const loginLocal = (usuario: string): void => {
        const session: UserSession = {
            login: usuario.trim() || 'gestor',
            nome: usuario.trim() ? usuario.toUpperCase() : 'ADMINISTRADOR DA FAZENDA',
            perfil: 'admin',
        };

        localStorage.setItem('@jcb:user', JSON.stringify(session));
        setUser(session);
    };

    const logout = (): void => {
        localStorage.removeItem('@jcb:user');
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, isAuthenticated: !!user, isLoading, loginLocal, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = (): AuthContextData => useContext(AuthContext);