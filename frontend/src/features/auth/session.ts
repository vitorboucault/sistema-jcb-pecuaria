export interface StoredSession {
    login: string;
    nome: string;
    token: string;
}

type StorageLike = Pick<Storage, 'getItem' | 'removeItem'>;

interface JwtPayload {
    exp?: unknown;
}

const decodePayload = (token: string): JwtPayload | null => {
    const partes = token.split('.');
    if (partes.length !== 3) return null;

    const base64 = partes[1].replace(/-/g, '+').replace(/_/g, '/');
    const preenchido = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=');

    try {
        const binario = atob(preenchido);
        const bytes = Uint8Array.from(binario, (caractere) => caractere.charCodeAt(0));
        return JSON.parse(new TextDecoder().decode(bytes)) as JwtPayload;
    } catch {
        return null;
    }
};

export const getTokenExpiration = (token: string): number | null => {
    const exp = decodePayload(token)?.exp;
    return typeof exp === 'number' && Number.isFinite(exp) ? exp : null;
};

export const isTokenExpired = (token: string, agoraEmSegundos = Math.floor(Date.now() / 1000)): boolean => {
    const expiration = getTokenExpiration(token);
    return expiration === null || expiration <= agoraEmSegundos;
};

export const readStoredSession = (
    storage: StorageLike,
    agoraEmSegundos = Math.floor(Date.now() / 1000),
): StoredSession | null => {
    const storedUser = storage.getItem('@jcb:user');
    if (!storedUser) return null;

    try {
        const parsed: unknown = JSON.parse(storedUser);
        if (
            typeof parsed !== 'object' ||
            parsed === null ||
            !('login' in parsed) ||
            !('nome' in parsed) ||
            !('token' in parsed) ||
            typeof parsed.login !== 'string' ||
            typeof parsed.nome !== 'string' ||
            typeof parsed.token !== 'string' ||
            isTokenExpired(parsed.token, agoraEmSegundos)
        ) {
            storage.removeItem('@jcb:user');
            return null;
        }

        return parsed as StoredSession;
    } catch {
        storage.removeItem('@jcb:user');
        return null;
    }
};
