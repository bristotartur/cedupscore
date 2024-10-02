import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PaginationResponse } from '../../shared/models/pagination-response.model';
import { handleError } from '../../shared/utils/common-utils';
import { Participant } from '../models/participant.model';
import { ParticipantRegistration } from '../models/participant-registration.model';

@Injectable({
  providedIn: 'root'
})
export class ParticipantService {

  private url = environment.api;
  private httpClient = inject(HttpClient);

  listParticipants(query?: string): Observable<PaginationResponse<Participant>> {
    return this.httpClient.get<PaginationResponse<Participant>>(`${this.url}/api/v1/participants${query}`);
  }

  findParticipantByCpf(cpf: string): Observable<Participant> {
    return this.httpClient.get<Participant>(`${this.url}/api/v1/participants/find?cpf=${cpf}`)
      .pipe(
        catchError(handleError)
      );
  }

  registerParticipant(req: ParticipantRegistration): Observable<Participant> {
    return this.httpClient.post<Participant>(`${this.url}/api/v1/participants`, req)
      .pipe(
        catchError(handleError)
      );
  }

}
