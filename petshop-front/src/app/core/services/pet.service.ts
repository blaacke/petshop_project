import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PetDTO } from '../types/user';

@Injectable({ providedIn: 'root' })
export class PetService {
    private http = inject(HttpClient);
    private baseUrl = '/api/pets';

    create(clienteId: number, body: PetDTO): Observable<PetDTO> {
        const params = new HttpParams().set('clienteId', String(clienteId));
        return this.http.post<PetDTO>(this.baseUrl, body, { params });
    }
}
