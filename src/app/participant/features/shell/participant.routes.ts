import { Routes } from "@angular/router";
import { ParticipantListComponent } from "../participant-list/participant-list.component";
import { ParticipantFormComponent } from "../participant-form/participant-form.component";
import { authGuard } from "../../../shared/guards/auth.guard";

export const routes: Routes = [
    {
        path: 'participants',
        component: ParticipantListComponent
    },
    {
        path: 'participants/register',
        component: ParticipantFormComponent,
        canActivate: [authGuard]
    }
];
