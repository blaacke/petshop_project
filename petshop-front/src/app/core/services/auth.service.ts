import { Injectable, inject } from '@angular/core';
import { LoginRequest, LoginResponse } from '../types/auth';

import { BehaviorSubject, map, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { TokenService } from './token.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
    private http = inject(HttpClient);
    private tokens = inject(TokenService);

    private _isAuthed$ = new BehaviorSubject<boolean>(this.tokens.has());
    isAuthed$ = this._isAuthed$.asObservable();

    login(credentials: LoginRequest) {
        return this.http.post<LoginResponse>(`http://localhost:8080/api/auth/login`, {
            cpf: credentials.cpf,
            password: credentials.senha,
        }).pipe(
            map(res => res?.token),
            tap(token => {
                if (!token) throw new Error('Token não encontrado.');
                this.tokens.set(token);
                this._isAuthed$.next(true);
            })
        );
    }

    logout() {
        this.tokens.clear();
        this._isAuthed$.next(false);
    }

    isAuthenticated(): boolean {
        return this.tokens.has();
    }
}
