import { AfterViewInit, Component, inject, OnInit, ViewChild } from '@angular/core';
import { PageBodyComponent } from "../../../core/components/page-body/page-body.component";
import { BehaviorSubject, filter, tap } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { EventModel } from '../../../shared/models/event.model';
import { NgClass } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TaskType } from '../../../shared/enums/task-type.enum';
import { Modality } from '../../../shared/enums/modality.enum';
import { ParticipantType } from '../../../shared/enums/participant-type.enum';
import { InputComponent } from "../../../shared/components/input/input.component";
import { SelectButtonComponent } from '../../../shared/components/select-button/select-button.component';
import { Option } from '../../../shared/models/option.model';
import { AlertPopupComponent } from '../../../shared/components/alert-popup/alert-popup.component';
import { TextEditorComponent } from "../../../shared/components/text-editor/text-editor.component";
import { EventService } from '../../../shared/services/event.service';
import { EditionService } from '../../../edition/services/edition.service';
import { UserService } from '../../../user/services/user.service';
import { Edition } from '../../../edition/models/edition.model';
import { EventRequest } from '../../../shared/models/event-request.model';
import { Status } from '../../../shared/enums/status.enum';
import { ExceptionResponse } from '../../../shared/models/exception-response.model';

@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [
    NgClass,
    ReactiveFormsModule,
    PageBodyComponent,
    InputComponent,
    SelectButtonComponent,
    AlertPopupComponent,
    TextEditorComponent
  ],
  templateUrl: './task-form.component.html',
  styleUrl: './task-form.component.scss'
})
export class TaskFormComponent implements OnInit, AfterViewInit {

  private router = inject(Router);
  private activatedRoute = inject(ActivatedRoute);
  private formBuilder = inject(FormBuilder);
  private eventService = inject(EventService);
  private editionService = inject(EditionService);
  private userService = inject(UserService);

  @ViewChild(InputComponent) nameInput!: InputComponent;
  @ViewChild('updatePopup') updatePopup!: AlertPopupComponent;

  protected form = this.formBuilder.group({
    name: ['', Validators.required],
    taskType: [null as TaskType | null, Validators.required],
    modality: [null as Modality | null, Validators.required],
    allowedParticipantType: [null as ParticipantType | null, Validators.required],
    minParticipantsPerTeam: [null as number | null, Validators.required],
    maxParticipantsPerTeam: [null as number | null, Validators.required],
    description: ['', Validators.required]
  });

  task$ = new BehaviorSubject<EventModel | null>(null);
  currentEdition$ = new BehaviorSubject<Edition | null>(null);

  taskOriginalDescription = '';
  isUpdateForm: boolean = false;
  isRequesting: boolean = false;
  errorMessage: string = '';

  typeOptions: Option[] = [
    { name: 'Normal', value: TaskType.NORMAL },
    { name: 'Conclusão', value: TaskType.COMPLETION },
    { name: 'Cultural', value: TaskType.CULTURAL },
  ];

  modalityOptions: Option[] = [
    { name: 'Misto', value: Modality.MIXED },
    { name: 'Masculino', value: Modality.MASCULINE },
    { name: 'Feminino', value: Modality.FEMININE }
  ];

  participantTypeOptions: Option[] = [
    { name: 'Alunos', value: ParticipantType.STUDENT },
    { name: 'Professores', value: ParticipantType.TEACHER },
    { name: 'Pais', value: ParticipantType.PARENT },
    { name: 'Alunos e professores', value: ParticipantType.TEACHER_STUDENT },
    { name: 'Alunos e pais', value: ParticipantType.STUDENT_PARENT },
    { name: 'Professores e pais', value: ParticipantType.TEACHER_PARENT },
    { name: 'Todos', value: ParticipantType.ALL }
  ];

  ngOnInit(): void {
    this.editionService.listEditions().subscribe(editions => {
      this.currentEdition$.next(editions[0]);
    });
    this.setUpForm();
  }

  ngAfterViewInit(): void {
    if (this.nameInput && !this.isUpdateForm) {
      this.nameInput.input.nativeElement.focus();
    }
  }

  private setUpForm(): void {
    const id = this.activatedRoute.snapshot.paramMap.get('id');
    if (!id || isNaN(+id)) return;

    this.isUpdateForm = true;
    this.eventService.findEventById(+id, 'task')
      .subscribe({
        next: (task) => {
            this.task$.next(task);
            this.fillForm(task);
          },
        error: () => this.router.navigate(['/**'])
      });
  }

  private fillForm(task: EventModel): void {
    this.form.patchValue({
      name: task.name,
      allowedParticipantType: task.allowedParticipantType,
      modality: task.modality,
      minParticipantsPerTeam: task.minParticipantsPerTeam,
      maxParticipantsPerTeam: task.maxParticipantsPerTeam,
      taskType: task.taskType,
      description: task.description
    });
    this.taskOriginalDescription = task.description ?? '';
  }

  comeBack(): void {
    document.documentElement.scrollTop = 0;

    if (this.isUpdateForm) {
      this.task$.subscribe(task => {
        const id = task?.id;
        this.router.navigate([`/tasks/${id}`]);
      })
    } else {
      this.router.navigate(['/tasks']);
    }
  }

  onTypeSelect(value: string | number): void {
    if (Object.values(TaskType).includes(value as TaskType)) {
      this.form.patchValue({ taskType: value as TaskType });
    }
  }

  onModalitySelect(value: string | number): void {
    if (Object.values(Modality).includes(value as Modality)) {
      this.form.patchValue({ modality: value as Modality });
    }
  }

  onParticipantTypeSelect(value: string | number): void {
    if (Object.values(ParticipantType).includes(value as ParticipantType)) {
      this.form.patchValue({ allowedParticipantType: value as ParticipantType });
    }
  }

  onContentChange(value: string): void {
    this.form.patchValue({ description: value });
  }

  onSubmit(event: Event): void {
    event.stopPropagation();
    if (!this.currentEdition$.value || !this.userService.currentUserSignal()) {
      this.errorMessage = 'A edição ou usuário atual são inválidos.';
      return;
    }
    if (!this.checkForm()) return;
    const status = this.currentEdition$.value.status;

    if (status !== Status.SCHEDULED && status !== Status.IN_PROGRESS) {
      this.errorMessage = 'Não há nenhuma edição agendada ou em andamento no momento.';
      return;
    }
    const editionId = this.currentEdition$.value.id;
    const userId = Number(this.userService.currentUserSignal()?.id);

    if (this.isUpdateForm) {
      this.updatePopup.openModal();
    } else {
      const request = this.prepareRequest(editionId, userId);
      this.registerTask(request);
    }
  }

  private checkForm(): boolean {
    if (this.form.invalid) {
      this.errorMessage = 'Todos os campos devem ser preenchidos.';
      return false;
    }
    const min = this.form.get('minParticipantsPerTeam')!.value as number;
    const max = this.form.get('maxParticipantsPerTeam')!.value as number;

    if (min <= 0) {
      this.errorMessage = 'A quantidade mínima de participantes não pode ser igual ou menor que 0';
      return false;
    }
    if (max <= 0) {
      this.errorMessage = 'A quantidade máxima de participantes não pode ser igual ou menor que 0';
      return false;
    }
    if (max < min) {
      this.errorMessage = 'A quantidade máxima de participantes não pode ser menor que a mínima';
      return false;
    }
    return true;
  }

  onPopupAccepted(): void {
    this.task$.pipe(
      filter((task): task is EventModel => task !== null),
      tap(task => {
        const editionId = task.editionId;
        const responsibleUserId = task.responsibleUserId;
        const request = this.prepareRequest(editionId, responsibleUserId);

        this.updateTask(task.id, request);
      })
    ).subscribe();
  }

  private prepareRequest(editionId: number, userId: number): EventRequest {
    const formValues = this.form.getRawValue();

    return {
      type: 'task',
      name: formValues.name as string,
      allowedParticipantType: formValues.allowedParticipantType as ParticipantType,
      modality: formValues.modality as Modality,
      minParticipantsPerTeam: formValues.minParticipantsPerTeam as number,
      maxParticipantsPerTeam: formValues.maxParticipantsPerTeam as number,
      taskType: formValues.taskType as TaskType,
      description: formValues.description as string,
      editionId: editionId,
      responsibleUserId: userId
    };
  }

  private registerTask(request: EventRequest): void {
    this.isRequesting = true;
    this.eventService.registerEvent(request).subscribe({
      next: () => {
        this.comeBack();
      },
      error: (err: ExceptionResponse) => {
        this.isRequesting = false;
        this.errorMessage = err.details;
      }
    });
  }

  private updateTask(id: number, request: EventRequest): void {
    this.isRequesting = true;
    this.eventService.updateEvent(id, request).subscribe({
      next: () => {
        document.documentElement.scrollTop = 0;
        this.router.navigate([`/tasks/${id}`]);
      },
      error: (err: ExceptionResponse) => {
        this.isRequesting = false;
        this.errorMessage = err.details;
      }
    });
  }

}
