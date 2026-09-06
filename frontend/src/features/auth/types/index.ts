export interface LoginCredentials {
    login: string;
    senha: string;
}

export interface AuthResponse {
    token: string;
    usuario: string;
}

export interface UserSession {
    login: string;
    nome: string;
    token: string;
}
