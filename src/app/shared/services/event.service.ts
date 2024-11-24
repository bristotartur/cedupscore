import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { catchError, Observable } from 'rxjs';
import { PaginationResponse } from '../models/pagination-response.model';
import { EventModel } from '../models/event.model';
import { EventRequest } from '../models/event-request.model';
import { handleError } from '../utils/common-utils';
import { Status } from '../enums/status.enum';
import { EventScoreRequest } from '../models/event-score-request.model';

@Injectable({
  providedIn: 'root'
})
export class EventService {

  private url = environment.api;
  private httpClient = inject(HttpClient);
  
  previousDetailsUrl: string = '/';

  listEvents(type?: 'task' | 'sport', editionId?: number, userId?: number): Observable<PaginationResponse<EventModel>> {
    const params = ['?size=50'];
    
    if (type) params.push(`type=${type}`);
    if (editionId) params.push(`edition=${editionId}`);
    if (userId) params.push(`responsible-user=${userId}`);

    const query = params.join('&');

    return this.httpClient.get<PaginationResponse<EventModel>>(`${this.url}/api/v1/events${query}`);
  }

  findEventById(id: number, type?: 'task' | 'sport'): Observable<EventModel> {
    const query = (type) ? `?type=${type}` : '';

    return this.httpClient.get<EventModel>(`${this.url}/api/v1/events/${id}${query}`)
      .pipe(
        catchError(handleError)
      );
  }

  registerEvent(req: EventRequest): Observable<EventModel> {
    return this.httpClient.post<EventModel>(`${this.url}/api/v1/events`, req)
      .pipe(
        catchError(handleError)
      );
  }

  deleteEvent(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.url}/api/v1/events/${id}`)
      .pipe(
        catchError(handleError)
      );
  }

  updateEvent(id: number, req: EventRequest): Observable<EventModel> {
    return this.httpClient.put<EventModel>(`${this.url}/api/v1/events/${id}`, req)
      .pipe(
        catchError(handleError)
      );
  }

  updateEventStatus(id: number, status: Status): Observable<EventModel> {
    return this.httpClient.patch<EventModel>(`${this.url}/api/v1/events/${id}/update?status=${status}`, null)
      .pipe(
        catchError(handleError)
      );
  }

  closeEvent(id: number, scores: EventScoreRequest[]): Observable<EventModel> {
    return this.httpClient.patch<EventModel>(`${this.url}/api/v1/events/${id}/close`, scores)
      .pipe(
        catchError(handleError)
      );
  }
  
}
