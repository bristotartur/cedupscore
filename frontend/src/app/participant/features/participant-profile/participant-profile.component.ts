import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { PageBodyComponent } from "../../../core/components/page-body/page-body.component";
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { ParticipantService } from '../../services/participant.service';
import { BehaviorSubject, filter, first, map, switchMap, take, tap} from 'rxjs';
import { Participant } from '../../models/participant.model';
import { CommonModule } from '@angular/common';
import { ProfileComponent } from '../../../shared/components/profile/profile.component';
import { Gender } from '../../../shared/enums/gender.enum';
import { ParticipantType } from '../../../shared/enums/participant-type.enum';
import { SelectButtonComponent } from "../../../shared/components/select-button/select-button.component";
import { Option } from '../../../shared/models/option.model';
import { EditionRegistration } from '../../../edition/models/edition-registration.model';
import { OptionsButtonComponent } from "../../../shared/components/options-button/options-button.component";
import { UserService } from '../../../user/services/user.service';
import { EditionService } from '../../../edition/services/edition.service';
import { Edition } from '../../../edition/models/edition.model';
import { AlertPopupComponent } from '../../../shared/components/alert-popup/alert-popup.component';
import { ExceptionResponse } from '../../../shared/models/exception-response.model';
import { SelectionPopupComponent } from '../../../shared/components/selection-popup/selection-popup.component';
import { EventService } from '../../../shared/services/event.service';
import { EventModel } from '../../../shared/models/event.model';
import { EventCardComponent } from "../../../shared/components/event-card/event-card.component";

@Component({
  selector: 'app-participant-profile',
  standalone: true,
  imports: [
    CommonModule,
    PageBodyComponent,
    ProfileComponent,
    SelectButtonComponent,
    OptionsButtonComponent,
    AlertPopupComponent,
    SelectionPopupComponent,
    EventCardComponent
],
  templateUrl: './participant-profile.component.html',
  styleUrl: './participant-profile.component.scss'
})
export class ParticipantProfileComponent implements OnInit {

  private router = inject(Router);
  private activatedRoute = inject(ActivatedRoute);
  private participantService = inject(ParticipantService);
  private editionService = inject(EditionService);
  private eventService = inject(EventService);
  protected userService = inject(UserService);

  @ViewChild('deletePopup') deletePopup!: AlertPopupComponent;
  @ViewChild('inactivationPopup') inactivationPopup!: AlertPopupComponent;
  @ViewChild('activationPopup') activationPopup!: AlertPopupComponent;
  @ViewChild('registrationPopup') registrationPopup!: AlertPopupComponent;
  @ViewChild('unregistrationPopup') unregistrationPopup!: AlertPopupComponent;
  @ViewChild('teamSelectionPopup') teamSelectionPopup!: SelectionPopupComponent;
  @ViewChild('errorPopup') errorPopup!: AlertPopupComponent;

  participant$ = new BehaviorSubject<Participant>({
    id: 0, name: '', type: ParticipantType.STUDENT, gender: Gender.MALE, isActive: false, editionRegistrations: []
  });
  currentEdition$ = new BehaviorSubject<Edition | null>(null);
  selectedRegistration$ = new BehaviorSubject<EditionRegistration | null>(null);
  
  currentUrl: string = '';
  previousLocation: string = ''
  buttonOptions!: Option[];
  latestRegistration!: EditionRegistration;
  registrationsOptions: Option[] = [];
  events: EventModel[] = [];
  teamsOptions: Option[] = [];
  errorMessage: string = '';

  constructor() {
    this.currentUrl = this.router.url;
    this.eventService.previousDetailsUrl = this.currentUrl;

    if (this.currentUrl.includes('tasks') || this.currentUrl.includes('sports')) {
      this.previousLocation = this.currentUrl.split('/participants')[0];
    } else {
      this.previousLocation = '/participants';
    }
    const previousUrl = this.participantService.previousProfileUrl;

    if (!previousUrl || previousUrl.includes('/update')) return;

    this.router.events.subscribe(event => {
      if (!(event instanceof NavigationEnd)) return;

      this.participantService.previousProfileUrl = event.url;
      this.previousLocation = event.url;
    });
  }

  ngOnInit(): void {
    let id: string;

    if (this.currentUrl.includes('tasks') || this.currentUrl.includes('sports')) {
      id = this.activatedRoute.snapshot.paramMap.get('participantId')!;
    } else {
      id = this.activatedRoute.snapshot.paramMap.get('id')!;
    }
    this.loadParticipant(+id);
    
    this.editionService.listEditions().pipe(
      map(editions => editions[0]),
      tap(edition => {
          edition.teamsScores.map(score => score.team)
            .forEach(team => {
                this.teamsOptions.push({ name: team.name, value: team.id });
            });
          this.currentEdition$.next(edition);
      })
    ).subscribe();
  }

  comeBack(): void {
    document.documentElement.scrollTop = 0;
    this.router.navigateByUrl(this.previousLocation);
  }

  onButtonOptionSelected(value: string | number): void {
    switch(value) {
      case 'delete': 
        if (this.deletePopup) this.deletePopup.openModal(); 
        break;
        case 'register':
          this.handleRegistration();
        break;
      case 'unregister':
        if (this.unregistrationPopup) this.unregistrationPopup.openModal();
        break;
      case 'inactive':
        if (this.inactivationPopup) this.inactivationPopup.openModal();
        break;
      case 'active':
        if (this.activationPopup) this.activationPopup.openModal();
        break;
    }
  }

  private handleRegistration(): void {
    this.currentEdition$.pipe(
      filter(edition => !!edition),
      take(1),
      map(edition => {
        const registration = this.participant$.value.editionRegistrations
          .find(registration => registration.editionId === edition.id);
        return registration;
      }),
      tap(registration => {
        if (!registration && this.teamSelectionPopup) {
          this.teamSelectionPopup.openModal();
          return;
        }
        if (this.registrationPopup) {
          this.registrationPopup.openModal();
        }
      })
    ).subscribe();
  }

  openTeamsSelection(): void {
    if (this.teamSelectionPopup) {
      this.teamSelectionPopup.clicksCount = -1;
      this.teamSelectionPopup.openModal();
    }
  }

  deleteParticipant(): void {
    this.participant$.pipe(
      switchMap(participant => {
        return this.participantService.deleteParticipant(participant.id);
      })
    ).subscribe({
      next: () => {
        this.comeBack()
      },
      error: (err: ExceptionResponse) => {
        this.errorMessage = err.details;
        if (this.errorPopup) this.errorPopup.openModal();
      }
    });
  }

  updateParticipantStatus(newStatus: boolean): void {
    this.participant$.pipe(
      take(1),
      switchMap(participant => {
        const id = participant.id;
        return this.participantService.setStatus(id, newStatus);
      })
    ).subscribe({
      next: participant => {
        this.loadParticipant(participant.id);
      },
      error: (err: ExceptionResponse) => {
        this.errorMessage = err.details;
        if (this.errorPopup) this.errorPopup.openModal();
      } 
    })
  }

  registerParticipant(value: string | number) {
    if (isNaN(+value)) return;

    this.currentEdition$.pipe(
      filter(edition => !!edition),
      take(1),
      switchMap(edition => {
        const id = this.participant$.value.id;
        const teamId = +value;

        return this.participantService.registerParticipantInEdition(id, edition.id, teamId);
      })
    ).subscribe({
      next: participant => {
        this.participant$.next(participant);
        this.setButtonOptions(participant.id);
        this.setRegistrationsOptions(participant.editionRegistrations);
      },
      error: (err: ExceptionResponse) => {
        this.errorMessage = err.details;
        if (this.errorPopup) this.errorPopup.openModal();
      }
    })
  }

  unregisterParticipant() {
    this.currentEdition$.pipe(
      filter(edition => !!edition),
      take(1),
      map(edition => {
        const participant = this.participant$.value;
        const registration = participant.editionRegistrations.find(registration => {
          registration.editionId === edition.id
        });
        return { participant, registration };
      }),
      filter(({ registration }) => !!registration),
      switchMap(({ participant, registration }) => 
          this.participantService.unregisterParticipantInEdition(participant.id, registration!.id)
            .pipe(
              tap(() => this.loadParticipant(participant.id))
            )
      )
    ).subscribe({
      error: (err: ExceptionResponse) => {
        this.errorMessage = err.details;
        if (this.errorPopup) this.errorPopup.openModal();
      }
    });
  }

  private loadParticipant(id: number): void {
    this.participantService.findParticipantById(+id)
      .subscribe({
        next: (participant) => {
            this.participant$.next(participant);
            this.setButtonOptions(id);
            this.loadParticipantEvents(id);
            this.setRegistrationsOptions(participant.editionRegistrations);
          },
        error: () => this.router.navigate(['/**'])
      });
  }

  private setButtonOptions(id: number) {
    this.buttonOptions = [
      { name: 'Editar', value: `/participants/${id}/update`, isLink: true }
    ];
    this.participant$.pipe(
      tap(participant => {
        if (!(participant.isActive)) {
          this.buttonOptions.push({ name: 'Ativar participante', value: 'active' });
          return;
        }
        if (participant.editionRegistrations.length > 1) {
          this.buttonOptions.push({ name: 'Desinscrever da edição atual', value: 'unregister' });
        }
        this.buttonOptions.push({ name: 'Inscrever na edição atual', value: 'register' });
        this.buttonOptions.push({ name: 'Desativar participante', value: 'inactive' });
      })
    ).subscribe();
    this.buttonOptions.push({ name: 'Excluir', value: 'delete' });
  }

  private setRegistrationsOptions(registrations: EditionRegistration[]): void {
    const sortedRegistrations = registrations.sort((a, b) => {
      return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
    });
    const defaultOption = { name: 'Geral', value: 0 };

    this.registrationsOptions = [
      defaultOption, 
      ...sortedRegistrations.map(registration => {
        const year = new Date(registration.createdAt).getFullYear();
        return {
          name: year.toString(),
          value: registration.id
        };
      })
    ];
    this.latestRegistration = sortedRegistrations[0];
    this.selectedRegistration$.next(this.latestRegistration);
  }

  onEditionSelected(value: string | number): void {
    if (isNaN(+value) || value === 0) {
      this.selectedRegistration$.next(this.latestRegistration);
      this.loadParticipantEvents(this.participant$.value.id);
      return;
    }
    this.participant$.pipe(
      switchMap(participant => participant.editionRegistrations),
      filter(registration => registration.id === +value),
      first()
    ).subscribe(registration => {
      this.selectedRegistration$.next(registration);
      this.loadParticipantEvents(this.participant$.value.id, registration.editionId);
    });
  }

  private loadParticipantEvents(participantId: number, editionId?: number): void {
    if (editionId) {
      this.eventService.listEvents(undefined, editionId, undefined, participantId).pipe(
        tap(events => this.events = events.content)
      ).subscribe();
    } else {
      this.eventService.listEvents(undefined, undefined, undefined, participantId).pipe(
        tap(events => this.events = events.content)
      ).subscribe();
    }
  }

}
