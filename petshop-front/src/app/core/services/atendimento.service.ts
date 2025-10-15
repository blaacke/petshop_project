import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminAtendimentoDTO, AtendimentoDTO, AtendimentoSit } from '../types/atendimento';
import { Page } from '../types/page';



@Injectable({ providedIn: 'root' })
export class AtendimentoService {
    private http = inject(HttpClient);
    private baseUrl = '/api/atendimentos';

    create(body: AtendimentoDTO): Observable<AtendimentoDTO> {
        return this.http.post<AtendimentoDTO>(this.baseUrl, body);
    }

    getBookedForDay(dateISO: string) {
        return this.http.get<string[]>(`${this.baseUrl}/day`, { params: { date: dateISO } });
        // back retorna LocalDateTime -> client ja transforma em string ISO
    }

    getPage(page = 0, size = 5, sort?: string): Observable<Page<AdminAtendimentoDTO>> {
        let params = new HttpParams().set('page', page).set('size', size);
        if (sort) params = params.set('sort', sort);
        return this.http.get<Page<AdminAtendimentoDTO>>(this.baseUrl, { params });
    }

    updateSituacao(id: number, situacao: AtendimentoSit): Observable<void> {
        return this.http.patch<void>(`${this.baseUrl}/${id}/situacao`, { situacao });
    }

    getAdminByDay(dateISO: string) {
        return this.http.get<AdminAtendimentoDTO[]>(
            `${this.baseUrl}/admin/day`,
            { params: { date: dateISO } }
        );
    }

}
