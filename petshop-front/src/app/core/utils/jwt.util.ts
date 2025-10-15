

export interface JwtClaims {
    sub?: string;
    nome?: string;
    cpf?: string;
    tipo?: 'ADMIN' | 'CLIENTE';
    roles?: string[];
    authorities?: string[];
    scope?: string;
    exp?: number;
    [k: string]: any;
}

function base64UrlDecode(input: string): string {
    const pad = input.length % 4 === 2 ? '==' : input.length % 4 === 3 ? '=' : '';
    const b64 = input.replace(/-/g, '+').replace(/_/g, '/') + pad;
    try {
        return decodeURIComponent(
            atob(b64).split('').map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join('')
        );
    } catch {
        return '';
    }
}

export function decodeJwt<T = JwtClaims>(token: string): T | null {
    try {
        const payload = token.split('.')[1];
        if (!payload) return null;
        const json = base64UrlDecode(payload);
        return JSON.parse(json);
    } catch {
        return null;
    }
}

export function getTipoFromClaims(claims: JwtClaims | null): 'ADMIN' | 'CLIENTE' | null {
    if (!claims) return null;
    if (claims.tipo?.toUpperCase() === 'ADMIN' || claims.tipo?.toUpperCase() === 'CLIENTE') return claims.tipo;

    const roles = [
        ...(claims.roles ?? []),
        ...(claims.authorities ?? []),
        ...(claims.scope ? claims.scope.split(/\s+/) : []),
    ].map(r => r.toUpperCase());

    if (roles.some(r => r.includes('ADMIN'))) return 'ADMIN';
    if (roles.length) return 'CLIENTE';
    return null;
}

export function isExpired(token: string | null): boolean {
    if (!token) return true;
    const claims = decodeJwt<JwtClaims>(token);
    if (!claims?.exp) return false;
    const now = Math.floor(Date.now() / 1000);
    return now >= claims.exp;
}
