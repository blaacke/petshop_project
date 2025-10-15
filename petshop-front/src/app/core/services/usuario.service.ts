import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { UsuarioDTO, UsuarioCreateResponse } from '../types/user';
import { SituacaoUsuario } from '../types/cliente';

export interface DecideRequest {
    token: string;
    secret: string;
    approve: boolean;
}

@Injectable({ providedIn: 'root' })
export class UsuarioService {
    private baseUrl = '/api/usuarios';

    constructor(private http: HttpClient) { }

    create(dto: UsuarioDTO): Observable<UsuarioCreateResponse> {
        const payload: any = { ...dto, password: dto.password };
        delete payload.senha;
        return this.http.post<UsuarioCreateResponse>(`http://localhost:8080${this.baseUrl}/create`, payload);
    }

    decidir(req: DecideRequest): Observable<{ message: string }> {
        return this.http.post<{ message: string }>(`http://localhost:8080/api/public/admin-approval`, req);
    }

    extractTokenFromLink(link: string): string | null {
        try {
            const url = new URL(link);
            return url.searchParams.get('token');
        } catch {
            return link || null;
        }
    }

    toggleSituacao(doc: string, situacao: SituacaoUsuario): Observable<void> {
        return this.http.patch<void>(`http://localhost:8080/api/usuarios/situacao/${doc}`, { situacao });
    }
}
