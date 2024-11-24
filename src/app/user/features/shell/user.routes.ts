import { Routes } from "@angular/router";
import { LoginFormComponent } from "../login-form/login-form.component";
import { UserProfileComponent } from "../user-profile/user-profile.component";
import { authGuard } from "../../../shared/guards/auth.guard";
import { UserFormComponent } from "../user-form/user-form.component";
import { RoleType } from "../../../shared/enums/role-type.enum";
import { UserListComponent } from "../user-list/user-list.component";

export const routes: Routes = [
    {
        path: 'login',
        component: LoginFormComponent
    },
    {
        path: 'profile',
        component: UserProfileComponent
    },
    {
        path: 'users',
        component: UserListComponent,
        canActivate: [authGuard],
        data: { requiredRole: RoleType.SUPER_ADMIN }
    },
    {
        path: 'users/register',
        component: UserFormComponent,
        canActivate: [authGuard],
        data: { requiredRole: RoleType.SUPER_ADMIN }
    },
    {
        path: 'users/:id',
        component: UserProfileComponent,
        canActivate: [authGuard],
        data: { requiredRole: RoleType.SUPER_ADMIN }
    },
    {
        path: 'users/:id/update',
        component: UserFormComponent,
        canActivate: [authGuard],
        data: { requiredRole: RoleType.SUPER_ADMIN }
    }
];
