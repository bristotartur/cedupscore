import { CanActivateFn, Router } from '@angular/router';
import { UserService } from '../../user/services/user.service';
import { inject } from '@angular/core';

export const authGuard: CanActivateFn = (route, state) => {
  const userService = inject(UserService);
  const router = inject(Router);

  if (userService.currentUserSignal()) {
    return true;
  } else {
    router.navigate(['/login']);
    return false;
  }
};
