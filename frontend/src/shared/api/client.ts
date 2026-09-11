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

api.interceptors.response.use(
    (response) => response,
    (error: unknown) => {
        if (axios.isAxiosError(error)) {
            const status = error.response?.status;
            const hasBearerToken = Boolean(error.config?.headers?.Authorization);
            const isLoginRoute = window.location.pathname === '/login';

            if (status === 401 && hasBearerToken && !isLoginRoute) {
                localStorage.removeItem('@jcb:user');
                window.location.replace('/login');
            }
        }

        return Promise.reject(error);
    },
);
