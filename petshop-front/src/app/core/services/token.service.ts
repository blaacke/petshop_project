import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

const TOKEN_KEY = environment.tokenKey;

@Injectable({ providedIn: 'root' })
export class TokenService {
    set(token: string) { localStorage.setItem(TOKEN_KEY, token); }
    get(): string | null { return localStorage.getItem(TOKEN_KEY); }
    clear() { localStorage.removeItem(TOKEN_KEY); }
    has(): boolean { return !!this.get(); }
}
