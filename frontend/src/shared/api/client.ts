import axios from 'axios';

export const api = axios.create({
    baseURL: '/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

api.interceptors.request.use((config) => {
    const sessionJson = localStorage.getItem('@jcb:user');
    if (sessionJson) {
        try {
            const session = JSON.parse(sessionJson) as { token?: string };
            if (session.token) {
                config.headers.Authorization = `Bearer ${session.token}`;
            }
        } catch {
            localStorage.removeItem('@jcb:user');
        }
    }
    return config;
});
