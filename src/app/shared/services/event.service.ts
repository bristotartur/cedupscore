import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PaginationResponse } from '../models/pagination-response.model';
import { Event } from '../models/event.model';

@Injectable({
  providedIn: 'root'
})
export class EventService {

  private url = environment.api;
  private httpClient = inject(HttpClient);

  listEvents(type?: 'task' | 'sport', editionId?: number, userId?: number): Observable<PaginationResponse<Event>> {
    const params = ['?size=50'];
    
    if (type) params.push(`type=${type}`);
    if (editionId) params.push(`edition=${editionId}`);
    if (userId) params.push(`responsible-user=${userId}`);

    const query = params.join('&');

    return this.httpClient.get<PaginationResponse<Event>>(`${this.url}/api/v1/events${query}`);
  }
  
}
