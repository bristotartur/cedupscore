import { AfterViewInit, Component, ElementRef, HostListener, inject, OnInit, ViewChild } from '@angular/core';
import { PageBodyComponent } from "../../../core/components/page-body/page-body.component";
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, catchError, exhaustMap, filter, map, of, switchMap, take, tap } from 'rxjs';
import { EventModel } from '../../../shared/models/event.model';
import { EventService } from '../../../shared/services/event.service';
import { AlertPopupComponent } from '../../../shared/components/alert-popup/alert-popup.component';
import { LeaderboardComponent } from "../../../shared/components/leaderboard/leaderboard.component";
import { EventScore } from '../../../shared/models/event-score.model';
import { StatusPipe } from '../../../shared/pipes/status.pipe';
import { EventTypePipe } from '../../../shared/pipes/event-type.pipe';
import { CustomDatePipe } from '../../../shared/pipes/custom-date.pipe';
import { CommonModule } from '@angular/common';
import { CustomTime } from '../../../shared/pipes/custom-time.pipe';
import { SearchBarComponent } from "../../../participant/ui/search-bar/search-bar.component";
import { SelectButtonComponent } from "../../../shared/components/select-button/select-button.component";
import { Option } from '../../../shared/models/option.model';
import { ParticipantPaginationComponent } from "../../../participant/ui/participant-pagination/participant-pagination.component";
import { Edition } from '../../../edition/models/edition.model';
import { Participant } from '../../../participant/models/participant.model';
import { EditionService } from '../../../edition/services/edition.service';
import { ParticipantService } from '../../../participant/services/participant.service';
import { PaginationResponse } from '../../../shared/models/pagination-response.model';
import { getAcceptableGender, getPossibleStatuses, transformStatus } from '../../../shared/utils/common-utils';
import { UserService } from '../../../user/services/user.service';
import { OptionsButtonComponent } from '../../../shared/components/options-button/options-button.component';
import { Status } from '../../../shared/enums/status.enum';
import { ExceptionResponse } from '../../../shared/models/exception-response.model';
import { SelectionPopupComponent } from '../../../shared/components/selection-popup/selection-popup.component';
import { CloseEventPopupComponent } from '../../../shared/components/close-event-popup/close-event-popup.component';
import { EventScoreRequest } from '../../../shared/models/event-score-request.model';

@Component({
  selector: 'app-task-details',
  standalone: true,
  imports: [
    CommonModule,
    PageBodyComponent,
    AlertPopupComponent,
    LeaderboardComponent,
    StatusPipe,
    EventTypePipe,
    CustomDatePipe,
    CustomTime,
    SearchBarComponent,
    SelectButtonComponent,
    ParticipantPaginationComponent,
    OptionsButtonComponent,
    SelectionPopupComponent,
    CloseEventPopupComponent
  ],
  templateUrl: './task-details.component.html',
  styleUrl: './task-details.component.scss'
})
export class TaskDetailsComponent implements OnInit, AfterViewInit {

  private router = inject(Router);
  private activatedRoute = inject(ActivatedRoute);
  private eventService = inject(EventService);
  private editionService = inject(EditionService);
  private participantService = inject(ParticipantService);
  userService= inject(UserService);

  @ViewChild('output') output!: ElementRef<HTMLDivElement>;
  @ViewChild('participantsContainer') participantsContainer!: ElementRef<HTMLDivElement>;
  @ViewChild('deletePopup') deletePopup!: AlertPopupComponent;
  @ViewChild('statusPopup') statusPopup!: SelectionPopupComponent;
  @ViewChild('closePopup') closePopup!: CloseEventPopupComponent;
  @ViewChild('errorPopup') errorPopup!: AlertPopupComponent;

  task$ = new BehaviorSubject<EventModel | null>(null);
  scores$ = new BehaviorSubject<EventScore[]>([]);
  edition$ = new BehaviorSubject<Edition | null>(null);
  participantsPage$ = new BehaviorSubject<PaginationResponse<Participant> | null>(null);
  
  dateType: 'full' | 'reduced' = 'full';
  currentSearchType: 'name' | 'cpf' = 'name';
  teamsOptions: Option[] = [{ name: 'Todas', value: '' }];
  buttonOptions: Option[] = [];
  statusOptions: Option[] = [];

  searchValue: string = '';
  currentPage: number = 1;
  totalPages: number = 1;
  rootQuery: string = '';
  currentQuery: string = '';
  selectedTeam: string | number = '';
  participantsPageHeight: number = 400;
  errorMessage: string = '';

  ngOnInit(): void {
    const id = this.activatedRoute.snapshot.paramMap.get('id')!;
    
    this.loadTask(+id);
    this.loadEdition();
    this.onResize();
  }

  ngAfterViewInit(): void {
    if (this.task$.value && this.output) {
      this.output.nativeElement.innerHTML = this.task$.value.description ?? '';
    }
    const resizeObserver = new ResizeObserver(() => {
      this.participantsPageHeight = this.participantsContainer.nativeElement.clientHeight;
    });
    resizeObserver.observe(this.participantsContainer.nativeElement);
  }

  @HostListener('window:resize', ['$event'])
  onResize(): void {
    const screenWidth = window.innerWidth;

    if (screenWidth < 530) {
      this.dateType = 'reduced'
    } else {
      this.dateType = 'full'
    }
  }

  private loadTask(id: number): void {
    this.eventService.findEventById(id, 'task').pipe(
      filter(task => !!task),
      tap(task => {
        this.createRootQuery(task);
        this.setTeamsOptions(task.scores);
        this.setButtonOptions(task);
        this.setStatusOptions(task.status);
      })
    ).subscribe({
      next: task => {
        this.task$.next(task);
        this.scores$.next(task.scores);

        this.loadParticipants();
        setTimeout(() => this.updateOutputContent(), 0);
      },
      error: () => this.router.navigate(['/**'])
    });
  }

  private createRootQuery(task: EventModel): void {
    const type = task.allowedParticipantType;
    const gender = getAcceptableGender(task.modality);
    const genderQuery = (gender) ? `&gender=${gender}` : '';

    this.rootQuery = `?event=${task.id}&type=${type}${genderQuery}`;
    this.currentQuery = this.rootQuery;
  }

  private setTeamsOptions(scores: EventScore[]): void {
    scores.forEach(score => {
      const team = score.team;
      this.teamsOptions.push({ name: team.name, value: team.id });
    });
  }

  private updateOutputContent(): void {
    if (this.output && this.task$.value) {
      this.output.nativeElement.innerHTML = this.task$.value.description ?? '';
    }
  }

  comeBack(): void {
    document.documentElement.scrollTop = 0;
    this.router.navigate(['/tasks']);
  }

  private loadEdition(): void {
    this.task$.pipe(
      filter(task => !!task),
      map(task => task.editionId),
      switchMap(editionId => {
        return this.editionService.findEditionByid(editionId)
      })
    ).subscribe(edition => {
      this.edition$.next(edition);
    });
  }

  onButtonOptionSelected(value: string | number): void {
    switch(value) {
      case 'delete': 
        this.deletePopup?.openModal(); 
        break;
      case 'updateStatus':
        this.statusPopup?.openModal();
        break;
      case 'close':
        this.closePopup?.openModal();
        break;
    }
  }

  onSearchValueChange(value: string): void {
    this.searchValue = value;
    this.loadParticipants();
  }

  onTeamSelect(value: string | number): void {
    this.selectedTeam = value;
    this.loadParticipants();
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.loadParticipants();

    window.scrollBy({ top: -this.participantsPageHeight });
  }

  private loadParticipants(): void {
    if (this.currentSearchType === 'name') {
      this.participantService.listParticipants(this.createQuery()).pipe(
        exhaustMap(page => of(page))
      ).subscribe(response => {
        this.participantsPage$.next(response); 
        this.totalPages = Math.ceil(response.page.totalElements / response.page.size);
        this.currentPage = response.page.number + 1;
      });
      return;
    }
    if (this.searchValue.length === 14) {
      this.participantService.findParticipantByCpf(this.searchValue).pipe(
        map(participant => this.resetParticipantsPage([participant], 1)),
        catchError(() => {
          this.participantsPage$.next(this.resetParticipantsPage([], 0));
          return of(null);
        })
      ).subscribe(response => {
        if (response) {
          this.participantsPage$.next(response);
        }
      });
    }
  }

  private createQuery(): string {
    let query = `${this.rootQuery}&page=${this.currentPage - 1}`;
    
    if (this.searchValue) query = `${query}&name=${this.searchValue}`;
    if (this.selectedTeam) query = `${query}&team=${this.selectedTeam}`;

    const lastQuery = this.currentQuery.replace(/&?page=\d+/, '').trim();

    if (query.replace(/&?page=\d+/, '') !== lastQuery) {
      query = query.replace(/page=\d+/, 'page=0');
      this.currentPage = 1; 
    }
    this.currentQuery = query;
    return query;
  }

  private resetParticipantsPage(content: Participant[], total: number): PaginationResponse<Participant> {
    this.totalPages = 1;
    this.currentPage = 1;

    return {
      content: content,
      page: { size: 1, number: 1, totalElements: total, totalPages: 1 }
    };
  }

  updateTaskStatus(status: string | number): void {
    this.task$.pipe(
      take(1),
      filter(task => !!task),
      map(task => task.id),
      switchMap(id => {
        const newStatus = status as Status;
        return this.eventService.updateEventStatus(id, newStatus)
      }),
    ).subscribe({
      next: task => {
        this.task$.next(task);

        this.setButtonOptions(task);
        this.setStatusOptions(task.status);
      },
      error: (err: ExceptionResponse) => {
        this.errorMessage = err.details;
        this.errorPopup?.openModal();
      }
    });
  }

  onScoresDefined(scores: EventScoreRequest[]): void {
    this.task$.pipe(
      take(1),
      filter(task => !!task),
      map(task => task.id),
      switchMap(id => this.eventService.closeEvent(id, scores))
    ).subscribe({
      next: task => {
        this.task$.next(task);
        this.scores$.next(task.scores);
      },
      error: (err: ExceptionResponse) => {
        this.errorMessage = err.details;
        this.errorPopup?.openModal();
      }
    });
  }

  private setButtonOptions(task: EventModel): void {
    const id = task.id;
    const status = task.status;

    this.buttonOptions = [{ name: 'Editar', value: `/tasks/${id}/update`, isLink: true }];

    if (status === Status.SCHEDULED) {
      this.buttonOptions.push({ name: 'Inscrever participantes', value: `/tasks/${id}/registration`, isLink: true });
    }
    if (status !== Status.CANCELED) {
      this.buttonOptions.push({ name: 'Atualizar status', value: 'updateStatus' });
    }
    if (status === Status.IN_PROGRESS) {
      this.buttonOptions.push({ name: 'Encerrar tarefa', value: 'close' });
    };
    this.buttonOptions.push({ name: 'Excluir', value: 'delete' });
  }

  private setStatusOptions(status: Status): void {
    this.statusOptions = getPossibleStatuses(status)
      .filter(status => status !== Status.ENDED)
      .map(status => {
        return { name: transformStatus(status, 'a'), value: status }
      });
  }

  deleteTask(): void {
    this.task$.pipe(
      filter(task => !!task),
      switchMap(task => {
        return this.eventService.deleteEvent(task?.id);
      })
    ).subscribe({
      next: () => this.comeBack(),
      error: (err: ExceptionResponse) => {
        this.errorMessage = err.details;
        if (this.errorPopup) this.errorPopup.openModal();
      }
    });
  }

}
