import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Edition } from '../models/edition.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EditionService {

  private url = environment.api;
  private httpClient = inject(HttpClient);

  listEditions(): Observable<Edition[]> {
    return this.httpClient.get<Edition[]>(`${this.url}/api/v1/editions`);
  }
  
}
