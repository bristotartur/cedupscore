import { Component, inject, OnInit } from '@angular/core';
import { PageBodyComponent } from '../../../core/components/page-body/page-body.component';
import { EventModel } from '../../../shared/models/event.model';
import { EventService } from '../../../shared/services/event.service';
import { map, tap } from 'rxjs';
import { EventCardComponent } from '../../../shared/components/event-card/event-card.component';
import { Status } from '../../../shared/enums/status.enum';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    PageBodyComponent,
    EventCardComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {

  private router = inject(Router);
  private eventService = inject(EventService);

  events: EventModel[] = [];
  inProgressEvents: EventModel[] = [];

  ngOnInit(): void {
    this.eventService.previousDetailsUrl = this.router.url;
    this.loadEvents();
  }

  private loadEvents(): void {
    this.eventService.listEvents().pipe(
      map(page => page.content),
      tap(events => {
        this.inProgressEvents = events.filter(event => {
          return event.status === Status.IN_PROGRESS;
        });
      })
    ).subscribe(events => {
      this.events = events;
    });
  }

}
