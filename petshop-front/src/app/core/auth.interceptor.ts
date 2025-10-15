import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { TokenService } from './services/token.service';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { isExpired } from './utils/jwt.util';


const isApiUrl = (url: string) => {
    if (url.startsWith('/api')) return true;
    try { return new URL(url).pathname.startsWith('/api'); } catch { return false; }
};

const isPublicApi = (url: string) =>
    url.includes('/api/auth/') || url.includes('/api/cadastro');

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const tokens = inject(TokenService);
    const router = inject(Router);

    const api = isApiUrl(req.url);
    const isPublic = isPublicApi(req.url);

    const token = tokens.get();
    const hasValidToken = !!token && token !== 'null' && token !== 'undefined';

    if (api && !isPublic && hasValidToken && isExpired(token!)) {
        tokens.clear();
        if (!router.url?.startsWith('/login')) router.navigate(['/login']);
        return next(req);
    }

    let r = req;
    if (api && !isPublic && hasValidToken) {
        r = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
    }

    return next(r).pipe(
        catchError((err: unknown) => {
            const e = err as HttpErrorResponse;
            const onLoginPage = router.url?.startsWith('/login');
            if ((e.status === 401) && api && !isPublic && !onLoginPage) {
                tokens.clear();
                router.navigate(['/login']);
            }
            return throwError(() => err);
        })
    );
};
