export interface LoginCredentials {
    login: string;
    senha: string;
}

export interface AuthResponse {
    token: string;
    login?: string;
    nome?: string;
}

export interface UserSession {
    login: string;
    nome: string;
}