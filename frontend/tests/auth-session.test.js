import assert from 'node:assert/strict';
import { describe, it } from 'node:test';
import { getTokenExpiration, isTokenExpired, readStoredSession } from '../src/features/auth/session.ts';

const tokenWithExpiration = (expiration) => {
    const payload = Buffer.from(JSON.stringify({ sub: 'dono', exp: expiration })).toString('base64url');
    return `header.${payload}.signature`;
};

const createStorage = (value) => {
    let stored = value;
    return {
        getItem: () => stored,
        removeItem: () => { stored = null; },
        hasValue: () => stored !== null,
    };
};

describe('sessão JWT no frontend', () => {
    it('@spec:AC-305 remove sessão expirada ou inválida ao iniciar', () => {
        const storage = createStorage(JSON.stringify({ login: 'dono', nome: 'DONO', token: 'invalido' }));

        const session = readStoredSession(storage, 1_700_000_000);

        assert.equal(session, null);
        assert.equal(storage.hasValue(), false);
    });

    it('@spec:AC-306 lê exp e identifica a transição para expiração', () => {
        const token = tokenWithExpiration(1_700_000_100);

        assert.equal(getTokenExpiration(token), 1_700_000_100);
        assert.equal(isTokenExpired(token, 1_700_000_099), false);
        assert.equal(isTokenExpired(token, 1_700_000_100), true);
    });
});
