import { AfterViewInit, Component, inject, ViewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { InputComponent } from "../../../shared/components/input/input.component";
import { PageBodyComponent } from '../../../core/components/page-body/page-body.component';
import { UserService } from '../../services/user.service';
import { Router, RouterLink } from '@angular/router';
import { LoginRequest } from '../../models/login-request.model';
import { ExceptionResponse } from '../../../shared/models/exception-response.model';
import { NgClass } from '@angular/common';
import { checkEmail } from '../../../shared/utils/common-utils';

@Component({
  selector: 'app-login-form',
  standalone: true,
  imports: [
    NgClass,
    ReactiveFormsModule,
    RouterLink,
    PageBodyComponent,
    InputComponent
  ],
  templateUrl: './login-form.component.html',
  styleUrl: './login-form.component.scss'
})
export class LoginFormComponent implements AfterViewInit {

  private formBuilder = inject(FormBuilder);
  private userService = inject(UserService);
  private router = inject(Router);

  @ViewChild(InputComponent) emailInputComponent!: InputComponent;

  protected form = this.formBuilder.group({
    email: ['', Validators.required],
    password: ['', Validators.required]
  });

  errorMessage: string = '';
  isRequesting: boolean = false;

  ngAfterViewInit(): void {
    if (this.emailInputComponent) this.emailInputComponent.input.nativeElement.focus();
  }

  onClick(): void {
    if (!this.checkForm()) return

    const request: LoginRequest = {
      email: this.form.get('email')!.value as string,
      password: this.form.get('password')!.value as string
    };
    this.isRequesting = true;

    this.userService.login(request).subscribe({
      next: (response) => {
        const userId = JSON.parse(atob(response.accessToken.split('.')[1])).sub;

        this.userService.findUser(userId).subscribe((user) => {
          localStorage.setItem('accessToken', response.accessToken);
          this.userService.currentUserSignal.set(user);
          this.errorMessage = '';
          this.router.navigate(['/']);
        });
      },
      error: (err: ExceptionResponse) => {
        this.isRequesting = false;
        this.errorMessage = err.details;
      }
    });
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

}
