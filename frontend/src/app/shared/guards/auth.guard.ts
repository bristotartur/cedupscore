import { CanActivateFn, Router } from '@angular/router';
import { UserService } from '../../user/services/user.service';
import { inject } from '@angular/core';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const userService = inject(UserService);

  const currentUser = userService.currentUserSignal();
  const currentUrl = state.url;
  const userId = +route.params['id'];
  const requiredRole = route.data['requiredRole'];

  if (!currentUser) {
    router.navigate(['/login']);
    return false;
  }

  if (currentUrl === `/users/${userId}` && userId === currentUser.id) {
    router.navigate(['/profile']);
    return true;
  }

  if (requiredRole && currentUser.role !== requiredRole) {
    router.navigate(['/**']);
    return false;
  }
  
  return true;
};
