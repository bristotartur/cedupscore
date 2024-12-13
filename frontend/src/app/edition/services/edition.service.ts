import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { catchError, Observable } from 'rxjs';
import { Edition } from '../models/edition.model';
import { environment } from '../../../environments/environment';
import { Status } from '../../shared/enums/status.enum';
import { handleError } from '../../shared/utils/common-utils';

@Injectable({
  providedIn: 'root'
})
export class EditionService {

  private url = environment.api;
  private httpClient = inject(HttpClient);

  listEditions(): Observable<Edition[]> {
    return this.httpClient.get<Edition[]>(`${this.url}/api/v1/editions`);
  }

  findEditionByid(id: number): Observable<Edition> {
    return this.httpClient.get<Edition>(`${this.url}/api/v1/editions/${id}`)
      .pipe(
        catchError(handleError)
      );
  }

  openNewEdition(): Observable<Edition> {
    return this.httpClient.post<Edition>(`${this.url}/api/v1/editions/open-edition`, null)
      .pipe(
        catchError(handleError)
      );
  }

  deleteEdition(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.url}/api/v1/editions/${id}`)
      .pipe(
        catchError(handleError)
      );
  }

  updateEditionStatus(id: number, status: Status): Observable<Edition> {
    return this.httpClient.patch<Edition>(`${this.url}/api/v1/editions/${id}/update?status=${status}`, null)
      .pipe(
        catchError(handleError)
      );
  }
  
}
