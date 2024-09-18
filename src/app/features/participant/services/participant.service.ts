import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { catchError, Observable } from 'rxjs';
import { PaginationResponse } from '../../../shared/models/pagination-response.model';
import { Participant } from '../models/participant.model';
import { handleError } from '../../../shared/utils/common-utils';

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

}
