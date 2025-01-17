import { Component, inject, OnInit } from '@angular/core';
import { PageBodyComponent } from '../../../core/components/page-body/page-body.component';
import { EventCardComponent } from '../../../shared/components/event-card/event-card.component';
import { SelectButtonComponent } from "../../../shared/components/select-button/select-button.component";
import { BehaviorSubject, map, Observable, tap } from 'rxjs';
import { EventModel } from '../../../shared/models/event.model';
import { EventService } from '../../../shared/services/event.service';
import { Edition } from '../../../edition/models/edition.model';
import { EditionService } from '../../../edition/services/edition.service';
import { Option } from '../../../shared/models/option.model';
import { Status } from '../../../shared/enums/status.enum';
import { NgClass } from '@angular/common';
import { UserService } from '../../../user/services/user.service';
import { OptionsButtonComponent } from '../../../shared/components/options-button/options-button.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-tasks',
  standalone: true,
  imports: [
    NgClass,
    PageBodyComponent,
    EventCardComponent,
    SelectButtonComponent,
    OptionsButtonComponent
  ],
  templateUrl: './tasks.component.html',
  styleUrl: './tasks.component.scss'
})
export class TasksComponent implements OnInit {

  private router = inject(Router);
  private eventService = inject(EventService);
  private editionService = inject(EditionService);
  protected userService = inject(UserService);

  tasks$ = new BehaviorSubject<EventModel[] | null>(null);
  latestEdition$ = new BehaviorSubject<Edition | null>(null);
  editions$!: Observable<Edition[]>;
  
  buttonOptions: Option[] = [{ name: 'Adicionar nova tarefa', value: '/tasks/register', isLink: true }];
  editionsOptions: Option[] = [];
  inProgressTasks: EventModel[] = [];
  selectedOption: number = 1;

  constructor() {
    this.editions$ = this.editionService.listEditions();
    this.editions$.subscribe(editions => {
      this.loadTasks(editions[0].id);
    });
  }

  ngOnInit(): void {
    this.eventService.previousDetailsUrl = this.router.url;
    this.editions$.pipe(
      tap(editions => this.setEditinsOptions(editions))
    ).subscribe();
  }

  private setEditinsOptions(editions: Edition[]): void {
    editions.forEach((edition, index) => {
      let startDate = new Date(edition.startDate);
      let year = startDate.getFullYear().toString();

      this.editionsOptions.push({ name: year, value: index + 1 });
    });
  }

  onEditionChange(index: number | string): void {
    this.editions$.pipe(
      map(editions => {
        return (index === 0) ? editions[1] : editions[+index - 1]
      })
    ).subscribe(edition => {
      this.selectedOption = +index;
      this.loadTasks(edition.id);
    });
  }

  private loadTasks(editionId?: number): void {
    this.eventService.listEvents('task', editionId).pipe(
      map(page => page.content),
      tap(editions => {
        this.inProgressTasks = editions.filter(edition => {
          const status = edition.status;
          return status === Status.IN_PROGRESS || status === Status.STOPPED
        })
      })
    ).subscribe(events => {
      this.tasks$.next(events);
    });
  }

}
