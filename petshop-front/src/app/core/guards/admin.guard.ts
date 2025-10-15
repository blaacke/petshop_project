import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenService } from '../services/token.service';
import { decodeJwt, getTipoFromClaims, isExpired } from '../utils/jwt.util';

export const adminGuard: CanActivateFn = () => {
    const router = inject(Router);
    const tokens = inject(TokenService);

    const token = tokens.get();
    if (!token || isExpired(token)) {
        tokens.clear();
        router.navigateByUrl('/login');
        return false;
    }

    const tipo = getTipoFromClaims(decodeJwt(token));
    if (tipo === 'ADMIN') return true;

    router.navigateByUrl('/');
    return false;
};
