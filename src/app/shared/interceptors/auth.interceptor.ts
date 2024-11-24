import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../user/services/user.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const userService = inject(UserService)
  const router = inject(Router);
  const token = localStorage.getItem('accessToken');

  if (!token) return next(req);

  const tokenPayload = JSON.parse(atob(token.split('.')[1]));
  const expirationDate = new Date(tokenPayload.exp * 1000);
  const currentDate = new Date();

  if (currentDate > expirationDate) {
    localStorage.removeItem('accessToken');
    userService.currentUserSignal.set(null);
    router.navigate(['/login'])
  }
  const clonedRequest = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });
  return next(clonedRequest);
};
