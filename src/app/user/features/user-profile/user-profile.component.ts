import { Component, inject, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { PageBodyComponent } from "../../../core/components/page-body/page-body.component";
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { catchError, EMPTY, map, Observable, of, switchMap, tap } from 'rxjs';
import { User } from '../../models/user.model';
import { ProfileComponent } from '../../../shared/components/profile/profile.component';
import { CommonModule } from '@angular/common';
import { RoleType } from '../../../shared/enums/role-type.enum';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    PageBodyComponent,
    ProfileComponent
  ],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.scss'
})
export class UserProfileComponent implements OnInit {

  private activatedRoute = inject(ActivatedRoute);
  protected router = inject(Router);
  protected userService = inject(UserService);

  user$!: Observable<User>;

  isNotCurrentUserProfile: boolean = false;
  isSuperAdmin: boolean = false;
  userUpdateLink: string = '';

  ngOnInit(): void {
    const currentUrl = this.router.url;
    const currentUser = this.userService.currentUserSignal();

    this.isSuperAdmin = currentUser?.role == RoleType.SUPER_ADMIN;
    this.isNotCurrentUserProfile = currentUrl.includes('/users');

    if (!this.isNotCurrentUserProfile && !currentUser) return;
      
    if (this.isNotCurrentUserProfile) {
      this.user$ = this.loadUser(currentUser);
    } else {
      this.user$ = of(currentUser!);
      this.userUpdateLink = `/users/${currentUser!.id}/update`;
    }
  }

  private loadUser(currentUser?: User | null): Observable<User> {
    return this.activatedRoute.paramMap.pipe(
      map(params => +params.get('id')!),
      tap(id => {
        if (currentUser && currentUser.id === id) {
          this.router.navigate(['/profile']);
        }
      }),
      switchMap(id => this.userService.findUser(id)),
      tap(user => this.userUpdateLink = `/users/${user.id}/update`),
      catchError(() => {
        this.router.navigate(['/**']);
        return EMPTY;
      })
    );
  }

}
