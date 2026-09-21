import assert from 'node:assert/strict';
import fs from 'node:fs';
import { describe, it } from 'node:test';

const authSource = fs.readFileSync(new URL('../src/features/auth/hooks/useAuth.tsx', import.meta.url), 'utf8');
const securitySource = fs.readFileSync(new URL('../src/shared/api/client.ts', import.meta.url), 'utf8');
const packageSource = fs.readFileSync(new URL('../package.json', import.meta.url), 'utf8');
const harnessSource = fs.readFileSync(new URL('../../scripts/check-frontend.sh', import.meta.url), 'utf8');
const docsSource = fs.readFileSync(new URL('../../docs/product/rebanho.md', import.meta.url), 'utf8');

describe('contrato de sessão autenticada', () => {
    it('@spec:AC-302 documentação registra lote preservado e composição ativa', () => {
        assert.match(docsSource, /preserva o `loteId`/);
        assert.match(docsSource, /somente animais com `status == ATIVO`/);
        assert.doesNotMatch(docsSource, /loteId = null/);
    });

    it('@spec:AC-303 harness executa testes, lint e build', () => {
        assert.match(packageSource, /"test": "node --test tests\/\*\.test\.js(?:[^"\\]|\\.)*"/);
        assert.match(harnessSource, /npm run test/);
        assert.match(harnessSource, /npm run lint/);
        assert.match(harnessSource, /npm run build/);
    });

    it('@spec:AC-306 agenda logout na expiração do JWT', () => {
        assert.match(authSource, /getTokenExpiration\(user\.token\)/);
        assert.match(authSource, /window\.setTimeout\(logout, delay\)/);
        assert.match(authSource, /window\.clearTimeout\(timeoutId\)/);
    });

    it('@spec:AC-307 interceptor encerra qualquer 401 fora do login', () => {
        assert.match(securitySource, /status === 401 && !isLoginRoute/);
        assert.doesNotMatch(securitySource, /hasBearerToken/);
        assert.match(securitySource, /localStorage\.removeItem\('@jcb:user'\)/);
        assert.match(securitySource, /window\.location\.replace\('\/login'\)/);
    });

    it('@spec:AC-309 não adiciona refresh token nem dependência JWT nova', () => {
        assert.doesNotMatch(authSource, /refreshToken|refresh token/i);
        assert.doesNotMatch(securitySource, /refreshToken|refresh token/i);
        assert.doesNotMatch(packageSource, /jsonwebtoken|jwt-decode|jose/);
    });
});
