import { Routes } from "@angular/router";
import { ParticipantListComponent } from "../participant-list/participant-list.component";
import { ParticipantFormComponent } from "../participant-form/participant-form.component";
import { authGuard } from "../../../shared/guards/auth.guard";
import { ParticipantsFilesManagerComponent } from "../participants-files-manager/participants-files-manager.component";
import { ParticipantProfileComponent } from "../participant-profile/participant-profile.component";

export const routes: Routes = [
    {
        path: 'participants',
        component: ParticipantListComponent,
    },
    {
        path: 'participants/register',
        component: ParticipantFormComponent,
        canActivate: [authGuard]
    },
    {
        path: 'participants/files',
        component: ParticipantsFilesManagerComponent,
        canActivate: [authGuard]
    },
    {
        path: 'participants/:id',
        component: ParticipantProfileComponent
    },
    {
        path: 'participants/:id/update',
        component: ParticipantFormComponent,
        canActivate: [authGuard]
    }
];
