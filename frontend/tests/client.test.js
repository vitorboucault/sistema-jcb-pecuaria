import assert from 'node:assert/strict';
import { afterEach, beforeEach, describe, it } from 'node:test';
import axios, { AxiosError } from 'axios';
import { api } from '../src/shared/api/client.ts';

const createStorage = () => {
    const values = new Map();
    return {
        getItem: (key) => values.get(key) ?? null,
        setItem: (key, value) => values.set(key, value),
        removeItem: (key) => values.delete(key),
        has: (key) => values.has(key),
    };
};

const createResponseError = (config, status) => new AxiosError(
    `HTTP ${status}`,
    AxiosError.ERR_BAD_REQUEST,
    config,
    undefined,
    {
        data: {},
        status,
        statusText: status === 401 ? 'Unauthorized' : 'Error',
        headers: {},
        config,
    },
);

describe('tratamento de JWT expirado', () => {
    let storage;
    let redirect;

    beforeEach(() => {
        storage = createStorage();
        redirect = [];
        globalThis.localStorage = storage;
        globalThis.window = {
            location: {
                pathname: '/dashboard',
                replace: (url) => redirect.push(url),
            },
        };
        api.defaults.adapter = async (config) => {
            throw createResponseError(config, 401);
        };
    });

    afterEach(() => {
        delete globalThis.localStorage;
        delete globalThis.window;
        api.defaults.adapter = axios.defaults.adapter;
    });

    it('@spec:AC-001 limpa a sessão e redireciona quando o JWT autenticado expira', async () => {
        storage.setItem('@jcb:user', JSON.stringify({ token: 'expirado' }));

        await assert.rejects(() => api.get('/dashboard'));

        assert.equal(storage.has('@jcb:user'), false);
        assert.deepEqual(redirect, ['/login']);
    });

    it('@spec:AC-002 não cria loop quando o login responde 401', async () => {
        window.location.pathname = '/login';
        storage.setItem('@jcb:user', JSON.stringify({ token: 'expirado' }));

        await assert.rejects(() => api.post('v1/auth/login', { usuario: 'x', senha: 'y' }));

        assert.equal(storage.has('@jcb:user'), true);
        assert.deepEqual(redirect, []);
    });

    it('@spec:AC-003 preserva 401 de requisição sem Bearer token', async () => {
        storage.setItem('@jcb:user', JSON.stringify({}));

        await assert.rejects(() => api.get('/publico'));

        assert.equal(storage.has('@jcb:user'), true);
        assert.deepEqual(redirect, []);
    });

    it('@spec:AC-004 preserva a sessão quando a API responde 403', async () => {
        storage.setItem('@jcb:user', JSON.stringify({ token: 'valido' }));
        api.defaults.adapter = async (config) => {
            throw createResponseError(config, 403);
        };

        await assert.rejects(() => api.get('/proibido'));

        assert.equal(storage.has('@jcb:user'), true);
        assert.deepEqual(redirect, []);
    });

    it('@spec:AC-005 preserva a sessão diante de erro não relacionado ao JWT', async () => {
        storage.setItem('@jcb:user', JSON.stringify({ token: 'valido' }));
        api.defaults.adapter = async (config) => {
            throw createResponseError(config, 500);
        };

        await assert.rejects(() => api.get('/indisponivel'));

        assert.equal(storage.has('@jcb:user'), true);
        assert.deepEqual(redirect, []);
    });
});
