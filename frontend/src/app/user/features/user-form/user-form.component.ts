import { AfterViewInit, Component, inject, OnInit, ViewChild } from '@angular/core';
import { catchError, EMPTY, map, Observable, of, switchMap, tap } from 'rxjs';
import { User } from '../../models/user.model';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Option } from '../../../shared/models/option.model';
import { RoleType } from '../../../shared/enums/role-type.enum';
import { AlertPopupComponent } from '../../../shared/components/alert-popup/alert-popup.component';
import { InputComponent } from '../../../shared/components/input/input.component';
import { PageBodyComponent } from "../../../core/components/page-body/page-body.component";
import { SelectButtonComponent } from "../../../shared/components/select-button/select-button.component";
import { checkEmail } from '../../../shared/utils/common-utils';
import { ExceptionResponse } from '../../../shared/models/exception-response.model';

@Component({
  selector: 'app-user-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    PageBodyComponent,
    InputComponent,
    SelectButtonComponent,
    AlertPopupComponent
  ],
  templateUrl: './user-form.component.html',
  styleUrl: './user-form.component.scss'
})
export class UserFormComponent implements OnInit, AfterViewInit {

  private router = inject(Router);
  private activatedRoute = inject(ActivatedRoute);
  private userService = inject(UserService);
  private formBuilder = inject(FormBuilder);

  @ViewChild(InputComponent) nameInput!: InputComponent;
  @ViewChild('updatePopup') updatePopup!: AlertPopupComponent;

  user$!: Observable<User>;

  currentUrl: string = '';
  formMode: 'register' | 'update' = 'register';
  errorMessage: string = '';
  isRequesting: boolean = false;

  protected form = this.formBuilder.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
    role: [null as RoleType | null, Validators.required]
  });

  roleOptions: Option[] = [
    { name: 'Adm de evento', value: RoleType.EVENT_ADMIN },
    { name: 'Adm de edição', value: RoleType.EDITION_ADMIN },
    { name: 'Super adm', value: RoleType.SUPER_ADMIN },
  ];

  ngOnInit(): void {
    this.currentUrl = this.router.url;
    this.formMode = (this.currentUrl.includes('update'))
      ? 'update'
      : 'register';

    if (this.formMode === 'update') {
      this.user$ = this.loadUser();
      this.fillForm();
    }
  }

  ngAfterViewInit(): void {
    if (this.nameInput && this.formMode !== 'update') {
      this.nameInput.input.nativeElement.focus();
    }
  }

  private loadUser(): Observable<User> {
    return this.activatedRoute.paramMap.pipe(
      map(params => +params.get('id')!),
      switchMap(id => {
        const currentUser = this.userService.currentUserSignal()!;
        return (id === currentUser.id)
          ? of(currentUser)
          : this.userService.findUser(id);
      }),
      catchError(() => {
        this.router.navigate(['/**'])
        return EMPTY;
      })
    );
  }

  private fillForm(): void {
    this.user$.pipe(
      tap(user => {
        this.form.patchValue({
          name: user.name,
          email: user.email,
          role: user.role
        });
      })
    ).subscribe();
  }

  comeBack(): void {
    if (this.formMode === 'register') this.router.navigate(['/users']);

    this.user$.pipe(
      tap(user => {
        const currentUser = this.userService.currentUserSignal()!;

        if (user.id === currentUser.id) {
          this.router.navigate(['/profile']);
        } else {
          this.router.navigate([`/users/${user.id}`]);
        }
      })
    ).subscribe();
  }

  onRoleSelect(value: string | number) {
    if (Object.values(RoleType).includes(value as RoleType)) {
      this.form.patchValue({ role: value as RoleType });
    }
  }

  onSubmit(event: Event): void {
    event.stopPropagation();

    if (!this.checkForm()) return;

    if (this.formMode === 'update') {
      this.updatePopup.openModal();
    } else {
      this.registerUser();
    }
  }

  private checkForm(): boolean {
    if (this.form.invalid) {
      this.errorMessage = 'Todos os campos devem ser preenchidos.';
      return false;
    }
    if (!checkEmail(this.form.get('email')!.value as string)) {
      this.errorMessage = 'Email inválido';
      return false;
    }
    return true;
  }

  registerUser(): void {
    const req = (this.form.value) as User;
    
    this.isRequesting = true;
    this.userService.createUser(req).subscribe({
      next: user => {
        const id = user.id;
        this.router.navigate([`/users/${id}`]);
      },
      error: (err: ExceptionResponse) => {
        this.isRequesting = false;
        this.errorMessage = err.details;
      }
    });
  }

  updateUser(): void {
    const req = (this.form.value) as User;

    this.isRequesting = true;
    this.user$.pipe(
      switchMap(user => {
        return this.userService.updateUser(user.id!, req)
      })
    ).subscribe({
      next: user => {
        const id = user.id;
        const currentUser = this.userService.currentUserSignal()!;

        if (currentUser.id === id) {
          this.userService.currentUserSignal.set(user);
        }
        this.router.navigate([`/users/${id}`]);
      },
      error: (err: ExceptionResponse) => {
        this.isRequesting = false;
        this.errorMessage = err.details;
      }
    });
  }

}
