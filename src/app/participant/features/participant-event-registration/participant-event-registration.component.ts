import { AfterViewInit, Component, ElementRef, inject, ViewChild } from '@angular/core';
import { PageBodyComponent } from "../../../core/components/page-body/page-body.component";
import { ActivatedRoute, Router } from '@angular/router';
import { ParticipantService } from '../../services/participant.service';
import { EventService } from '../../../shared/services/event.service';
import { BehaviorSubject, catchError, exhaustMap, filter, map, of, switchMap, take, tap } from 'rxjs';
import { EventModel } from '../../../shared/models/event.model';
import { getAcceptableGender } from '../../../shared/utils/common-utils';
import { PaginationResponse } from '../../../shared/models/pagination-response.model';
import { Participant } from '../../models/participant.model';
import { ParticipantPaginationComponent } from "../../ui/participant-pagination/participant-pagination.component";
import { Edition } from '../../../edition/models/edition.model';
import { EditionService } from '../../../edition/services/edition.service';
import { SearchBarComponent } from "../../ui/search-bar/search-bar.component";
import { SelectButtonComponent } from "../../../shared/components/select-button/select-button.component";
import { Option } from '../../../shared/models/option.model';
import { EventScore } from '../../../shared/models/event-score.model';
import { ParticipantCardComponent } from "../../ui/participant-card/participant-card.component";
import { NgClass } from '@angular/common';
import { AlertPopupComponent } from '../../../shared/components/alert-popup/alert-popup.component';
import { ExceptionResponse } from '../../../shared/models/exception-response.model';

@Component({
  selector: 'app-participant-event-registration',
  standalone: true,
  imports: [
    NgClass,
    PageBodyComponent,
    ParticipantPaginationComponent,
    SearchBarComponent,
    SelectButtonComponent,
    ParticipantCardComponent,
    AlertPopupComponent
  ],
  templateUrl: './participant-event-registration.component.html',
  styleUrl: './participant-event-registration.component.scss'
})
export class ParticipantEventRegistrationComponent implements AfterViewInit {

  private router = inject(Router);
  private activatedRoute = inject(ActivatedRoute);
  private eventService = inject(EventService);
  private editionService = inject(EditionService);
  private participantService = inject(ParticipantService);

  @ViewChild('count') count!: ElementRef<HTMLSpanElement>;
  @ViewChild('paginationContainer') paginationContainer!: ElementRef<HTMLDivElement>;
  @ViewChild('errorPopup') errorPopup!: AlertPopupComponent;

  event$ = new BehaviorSubject<EventModel | null>(null);
  edition$ = new BehaviorSubject<Edition | null>(null)
  participantsPage$ = new BehaviorSubject<PaginationResponse<Participant> | null>(null);
  participantsToAdd$ = new BehaviorSubject<Participant[]>([]);
  registeredParticipants$ = new BehaviorSubject<Participant[]>([]);

  currentList: 'selectedParticipants' | 'registerdParticipants' = 'selectedParticipants';
  currentSearchType: 'name' | 'cpf' = 'name';
  excludeIds: number[] = [];
  teamsOptions: Option[] = [{ name: 'Todas', value: '' }];

  eventId: number = 0;
  errorMessage = '';
  comeBackUrl: string = '';
  showActionButton: boolean = false;
  searchValue: string = '';
  currentPage: number = 1;
  totalPages: number = 1;
  baseParticipantQuery: string = '';
  currentQuery: string = '';
  selectedTeam: string | number = '';
  selectedTeamInParticipantsContainer: string | number = '';
  participantsPageHeight: number = 400;

  constructor() {
    this.eventId = +this.activatedRoute.snapshot.paramMap.get('id')!;
    const urlSegments = this.activatedRoute.snapshot.url;

    const isTaskRoute = urlSegments.some(segment => segment.path === 'tasks');
    const type = (isTaskRoute) ? 'task' : 'sport';
    
    this.comeBackUrl = (isTaskRoute) ? `/tasks/${this.eventId}` : `/sports/${this.eventId}`;
    this.loadEvent(this.eventId, type);
  }

  ngAfterViewInit(): void {
    const resizeObserver = new ResizeObserver(() => {
      this.participantsPageHeight = this.paginationContainer.nativeElement.clientHeight;
    });
    resizeObserver.observe(this.paginationContainer.nativeElement);
  }

  comeBack(): void {
    document.documentElement.scrollTop = 0;
    this.router.navigate([this.comeBackUrl]);
  }

  private loadEvent(eventId: number, type: 'task' | 'sport'): void {
    this.eventService.findEventById(+eventId, type).pipe(
      filter(event => !!event),
      tap(event => {
        this.createBaseParticipantQuery(event);
        this.setTeamsOptions(event.scores);
      })
    ).subscribe({
      next: event => {
        this.event$.next(event);
        this.loadEdition(event.editionId);
        this.loadRegisteredParticipants(event.id);
        this.loadSelectedParticipants(event.id);
        this.loadParticipants(this.excludeIds);
      },
      error: () => this.router.navigate(['/**'])
    });
  }

  private createBaseParticipantQuery(event: EventModel): void {
    const eventId = event.id;
    const participantType = event.allowedParticipantType;
    const gender = getAcceptableGender(event.modality);

    const genderQuery = (gender) ? `&gender=${gender}` : '';

    this.baseParticipantQuery = `?type=${participantType}${genderQuery}&status=active&not-in-event=${eventId}`;
  }

  private setTeamsOptions(scores: EventScore[]): void {
    scores.forEach(score => {
      const team = score.team;
      this.teamsOptions.push({ name: team.name, value: team.id });
    });
  }

  private loadEdition(editionId: number): void {
    this.editionService.findEditionByid(editionId).subscribe(edition => {
      this.edition$.next(edition);
    });
  }

  changeList(value: 'selectedParticipants' | 'registerdParticipants'): void {
    this.currentList = value;

    switch(value) {
      case 'selectedParticipants':
        this.showActionButton = this.participantsToAdd$.value.length > 0;
        this.loadSelectedParticipants(this.eventId, this.selectedTeamInParticipantsContainer);
        break;
      case 'registerdParticipants':
        this.showActionButton = this.registeredParticipants$.value.length > 0;
        this.loadRegisteredParticipants(this.eventId, this.selectedTeamInParticipantsContainer);
        break;
    }
  }

  onTeamSelect(value: string | number, scope: 'management-list' | 'search'): void {
    switch (scope) {
      case 'search':
        this.selectedTeam = value;
        this.loadParticipants();
        break;
      case 'management-list':
        this.selectedTeamInParticipantsContainer = value;
        if (this.currentList === 'selectedParticipants') {
          this.loadSelectedParticipants(this.eventId, value);
        } else {
          this.loadRegisteredParticipants(this.eventId, value);
        }
        break;
    }
  }

  private loadSelectedParticipants(eventId: number, value?: string | number): void {
    const teamId = (value && !isNaN(+value)) ? +value : null;
    const selectedParticipants = sessionStorage.getItem(`selectedFrom${eventId}`);
    
    if (selectedParticipants) {
      let participants: Participant[] = [];
      
      if (teamId) {
        participants = (JSON.parse(selectedParticipants) as Participant[])
          .filter(p => p.editionRegistrations[0].team.id === +teamId);
        this.loadRegisteredParticipants(this.eventId, teamId);
      } else {
        participants = JSON.parse(selectedParticipants);
        this.loadRegisteredParticipants(this.eventId);
      }
      this.participantsToAdd$.next(participants);
      this.excludeIds = participants.map(p => p.id);
      this.showActionButton = participants.length > 0;
    } else {
      sessionStorage.setItem(`selectedFrom${eventId}`, JSON.stringify([]));
    }
  }

  onSearchValueChange(value: string): void {
    this.searchValue = value;
    this.loadParticipants();
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.loadParticipants();

    window.scrollBy({ top: -this.participantsPageHeight });
  }

  onCardSelected(value: { id: number, action: 'add' | 'remove' }): void {
    switch (value.action) {
      case 'add':
        this.addToSelectedParticipants(value.id);
        this.changeList('selectedParticipants');
        break;
      case 'remove':
        this.removeFromSelectedParticipants(value.id);
        break;
    }
  }

  private addToSelectedParticipants(id: number): void {
    this.participantsPage$.pipe(
      take(1),
      filter(page => !!page),
      map(page => page.content.find(p => p.id === id) || null),
      tap(selectedParticipant => {
        if (selectedParticipant) {
          const participantsToAdd = [...this.participantsToAdd$.value, selectedParticipant];

          this.excludeIds.push(id);
          this.participantsToAdd$.next(participantsToAdd);
          this.showActionButton = participantsToAdd.length > 0;

          sessionStorage.setItem(`selectedFrom${this.eventId}`, JSON.stringify(participantsToAdd));
        }
      })
    ).subscribe(() => {
      if (this.currentSearchType === 'cpf' && this.searchValue.length === 14) {
        this.currentSearchType = 'name';
        this.searchValue = '';
      }
      this.loadParticipants(this.excludeIds);
    });
  }

  handleActionBtnClick(): void {
    if (this.currentList === 'selectedParticipants') {
      this.registerParticipants();
    } else {
      this.registeredParticipants$.pipe(
        take(1)
      ).subscribe(participants => {
        this.unregisterParticipants(participants);
      });
    }
  }

  registerParticipants(): void {
    this.participantsToAdd$.pipe(
      take(1),
      map(participants => {
        return participants.map(p =>({
          participantId: p.id,
          teamId:  p.editionRegistrations[0].team.id
        }));
      }),
      switchMap(registrations => this.participantService.registerParticipantsInEvent(this.eventId, registrations)),
      tap(participants => participants.forEach(p => this.removeFromSelectedParticipants(p.id)))
    ).subscribe({
      next: () => {
        this.loadRegisteredParticipants(this.eventId);
      },
      error: (err: ExceptionResponse) => {
        this.errorMessage = err.details;
        this.errorPopup?.openModal();
      } 
    });
  }

  private removeFromSelectedParticipants(participantId: number): void {
    let index: number;

    this.participantsToAdd$.pipe(
      take(1),
      tap(participants => {
        index = participants.findIndex(p => p.id == participantId);
        this.excludeIds = this.excludeIds.filter(excludedId => excludedId !== participantId);
      })
    ).subscribe(participants => {
      participants.splice(index, 1);
      
      this.participantsToAdd$.next(participants);
      this.showActionButton = participants.length > 0;
      this.loadParticipants(this.excludeIds);

      this.updateCachedSelectedParticipants(participantId);
    });
  }

  private loadParticipants(excludeIds?: number[]): void {
    if (this.currentSearchType === 'name' || excludeIds) {
      const ids = (excludeIds && excludeIds.length > 0) ? excludeIds : [];

      this.participantService.listParticipants(this.createQuery(), ids).pipe(
        exhaustMap(page => of(page)),
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
    let query = `${this.baseParticipantQuery}&page=${this.currentPage - 1}`;
    
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

  private updateCachedSelectedParticipants(participantId: number): void {
    const selectedParticipants = sessionStorage.getItem(`selectedFrom${this.eventId}`)!;
    let participants = (JSON.parse(selectedParticipants) as Participant[]);

    participants = participants.filter(p => p.id != participantId);

    sessionStorage.setItem(`selectedFrom${this.eventId}`, JSON.stringify(participants));
  }

  private loadRegisteredParticipants(eventId: number, value?: string | number): void {
    const teamId = (value && !isNaN(+value)) ? +value : null;
    const teamQuery = (teamId) ? `&team=${teamId}` : '';

    this.participantService.listParticipants(`?event=${eventId}${teamQuery}`).pipe(
      map(page => page.content)
    ).subscribe(participants => {
      this.registeredParticipants$.next(participants);
      this.setCount(this.event$.value!);

      if (teamId) {
        this.setCount(this.event$.value!, teamId);
      }
    });
  }

  unregisterParticipants(participants: Participant[]): void {
    const registrationIds = participants.map(participant => {
      return participant.eventRegistration?.id!;
    })
    this.participantService.removeEventRegistrations(this.eventId, registrationIds).subscribe({
      next: () => {
        this.loadRegisteredParticipants(this.eventId);
      },
      error: (err: ExceptionResponse) => {
        this.errorMessage = err.details;
        this.errorPopup?.openModal();
      }
    })
  }

  private setCount(event: EventModel, teamId?: number): void {
    const teamsCount = event.scores.length;
    const min = event.minParticipantsPerTeam;
    const max = event.maxParticipantsPerTeam;
    const registeredParticipants = this.registeredParticipants$.value;

    let registeredCount = (!teamId)
      ? registeredParticipants.length
      : registeredParticipants.filter(p => p.eventRegistration?.team.id === teamId).length;
      
    if (min === max) {
      this.count.nativeElement.innerHTML = `Inscritos necessários: ${registeredCount}/${(teamId) ? min : min * teamsCount}`;
    } else {
      this.count.nativeElement.innerHTML = `
          Inscritos necessários: ${registeredCount}/${(teamId) ? min : min * teamsCount}<br>
          Máximo permitido: ${registeredCount}/${(teamId) ? max : max * teamsCount} 
        `;
    }
  }

}
