import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClienteFullDTO, SituacaoUsuario } from '../types/cliente';
import { Page } from '../types/page';

@Injectable({ providedIn: 'root' })
export class ClienteService {
    private http = inject(HttpClient);
    private baseUrl = '/api/clientes';

    getPage(page = 0, size = 5, sort?: string): Observable<Page<ClienteFullDTO>> {
        let params = new HttpParams()
            .set('page', page)
            .set('size', size);
        if (sort) params = params.set('sort', sort);
        return this.http.get<Page<ClienteFullDTO>>(`${this.baseUrl}/full`, { params });
    }

    getMyFull() {
        return this.http.get<ClienteFullDTO>(`${this.baseUrl}/me/full`);
    }
}
