import { api } from '../../../shared/api/client';
import type { LoginCredentials, AuthResponse } from '../types';

export const authService = {
    async login(credentials: LoginCredentials): Promise<AuthResponse> {
        const response = await api.post<AuthResponse>('/auth/login', {
            usuario: credentials.login,
            senha: credentials.senha,
        });
        return response.data;
    },
};