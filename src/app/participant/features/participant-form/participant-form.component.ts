import { AfterViewInit, Component, inject, OnInit, ViewChild } from '@angular/core';
import { PageBodyComponent } from "../../../core/components/page-body/page-body.component";
import { NgClass } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { InputComponent } from "../../../shared/components/input/input.component";
import { SelectButtonComponent } from '../../../shared/components/select-button/select-button.component';
import { Gender } from '../../../shared/enums/gender.enum';
import { ParticipantType } from '../../../shared/enums/participant-type.enum';
import { EditionService } from '../../../edition/services/edition.service';
import { BehaviorSubject, catchError, map, of, switchMap, take } from 'rxjs';
import { Team } from '../../../shared/models/team.model';
import { Option } from '../../../shared/models/option.model';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import { ParticipantService } from '../../services/participant.service';
import { ParticipantRegistration } from '../../models/participant-registration.model';
import { ExceptionResponse } from '../../../shared/models/exception-response.model';
import { Participant } from '../../models/participant.model';
import { AlertPopupComponent } from '../../../shared/components/alert-popup/alert-popup.component';

@Component({
  selector: 'app-participant-form',
  standalone: true,
  imports: [
    NgClass,
    ReactiveFormsModule,
    NgxMaskDirective,
    PageBodyComponent,
    InputComponent,
    SelectButtonComponent,
    AlertPopupComponent
  ],
  templateUrl: './participant-form.component.html',
  styleUrl: './participant-form.component.scss',
  providers: [provideNgxMask()]
})
export class ParticipantFormComponent implements OnInit, AfterViewInit {

  private formBuilder = inject(FormBuilder);
  private router = inject(Router);
  private activatedRoute = inject(ActivatedRoute);
  private editionService = inject(EditionService);
  private participantService = inject(ParticipantService);

  @ViewChild(InputComponent) nameInput!: InputComponent;
  @ViewChild('updatePopup') updatePopup!: AlertPopupComponent;

  protected form = this.formBuilder.group({
    name: ['', Validators.required],
    cpf: ['', Validators.required],
    gender: [null as Gender | null, Validators.required],
    type: [null as ParticipantType | null, Validators.required],
    team: [0, Validators.required]
  })

  participant$ = new BehaviorSubject<Participant | null>(null);

  isUpdateForm: boolean = false;
  teamsOptions: Option[] = [];
  isRequesting: boolean = false;
  mask: string = '000.000.000-00';
  errorMessage: string = '';

  genderOptions = [
    { name: 'Masculino', value: Gender.MALE },
    { name: 'Feminino', value: Gender.FEMALE }
  ];

  participantTypeOptions = [
    { name: 'Aluno', value: ParticipantType.STUDENT },
    { name: 'Professor', value: ParticipantType.TEACHER },
    { name: 'Pai', value: ParticipantType.PARENT },
    { name: 'Pai e aluno', value: ParticipantType.STUDENT_PARENT },
    { name: 'Pai e professor', value: ParticipantType.TEACHER_PARENT },
  ];

  constructor() {
    this.editionService.listEditions().pipe(
      map(editions => editions[0]?.teamsScores || []),
      map(scores => scores.map(score => score.team)), 
      catchError(() => {
        return of([]);
      })
    ).subscribe(teams => {
      this.setTeamsOptions(teams);
    });
  }

  ngOnInit(): void {
    const id = this.activatedRoute.snapshot.paramMap.get('id');
    if (!id || isNaN(+id)) return;

    this.isUpdateForm = true;
    this.participantService.findParticipantForUpdate(+id)
      .subscribe({
        next: (participant) => {
            this.participant$.next(participant);
            this.fillForm(participant);
          },
        error: () => this.router.navigate(['/**'])
      });
  }

  ngAfterViewInit(): void {
    if (this.nameInput && !this.isUpdateForm) {
      this.nameInput.input.nativeElement.focus();
    }
  }

  private fillForm(participant: Participant): void {
    this.form.patchValue({
      name: participant.name.toLowerCase(),
      cpf: participant.cpf,
      gender: participant.gender,  
      type: participant.type
    });
  }

  private setTeamsOptions(teams: Team[]): void {
    teams.forEach(team => {
      this.teamsOptions.push({ name: team.name, value: team.id });
    })
  }

  comeBack(): void {
    document.documentElement.scrollTop = 0;

    if (this.isUpdateForm) {
      this.participant$.subscribe(participant => {
        const id = participant?.id;
        this.router.navigate([`/participants/${id}`]);
      });
    } else {
      this.router.navigate(['/participants']);
    }
  }

  onGenderSelect(value: string | number) {
    if (Object.values(Gender).includes(value as Gender)) {
      this.form.patchValue({ gender: value as Gender });
    }
  }

  onTypeSelect(value: string | number) {
    if (Object.values(ParticipantType).includes(value as ParticipantType)) {
      this.form.patchValue({ type: value as ParticipantType });
    }
  }

  onTeamSelect(value: string | number) {
    if (!isNaN(+value)) {
      this.form.patchValue({ team: +value });
    }
  }

  onSubmit(event: Event): void {
    event.stopPropagation();
    
    const cpf = this.form.get('cpf')!.value as string;
    this.form.patchValue({ cpf: this.formatCpf(cpf) });

    if (!this.checkForm()) return;

    if (this.isUpdateForm) {
      this.updatePopup.openModal();
    } else {
      const request = this.prepareRequest();
      this.registerParticipant(request);
    }
  }

  onPopupAccepted(): void {
    const request = this.prepareRequest();
    this.updateParticipant(request);
  }

  private formatCpf(cpf: string) {
    return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  }

  private checkForm(): boolean {
    if (this.form.invalid) {
      this.errorMessage = 'Todos os campos devem ser preenchidos.';
      return false;
    }
    const cpf = this.form.get('cpf')!.value as string;
    const isValidFormat = !/^\d{3}\.\d{3}\.\d{3}\-\d{2}$/.test(cpf);

    if (isValidFormat) {
      this.errorMessage = 'CPF inválido';
      return false;
    }
    return true;
  }

  prepareRequest(): ParticipantRegistration {
    return {
      name: this.form.get('name')!.value as string,
      cpf: this.form.get('cpf')!.value as string,
      gender: this.form.get('gender')!.value as Gender,
      type: this.form.get('type')!.value as ParticipantType,
      teamId: this.form.get('team')!.value as number
    }
  }

  private registerParticipant(request: ParticipantRegistration) {
    this.isRequesting = true;
    this.participantService.registerParticipant(request).subscribe({
      next: () => {
        this.comeBack()
      },
      error: (err: ExceptionResponse) => {
        this.isRequesting = false;
        this.errorMessage = err.details;
      }
    });
  }

  private updateParticipant(request: ParticipantRegistration) {
    let id = 0;

    this.isRequesting = true;
    this.participant$.pipe(
      take(1),
      switchMap(participant => {
        id = participant?.id ?? 0;
        return this.participantService.updateParticipant(id, request);
      })
    ).subscribe({
      next: () => {
        document.documentElement.scrollTop = 0;
        this.router.navigate([`/participants/${id}`]);
      },
      error: (err: ExceptionResponse) => {
        this.isRequesting = false;
        this.errorMessage = err.details;
      }
    });
  }

}
