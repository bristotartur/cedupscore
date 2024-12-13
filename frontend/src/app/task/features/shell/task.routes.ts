import { Routes } from "@angular/router";
import { TasksComponent } from "../tasks/tasks.component";
import { TaskFormComponent } from "../task-form/task-form.component";
import { authGuard } from "../../../shared/guards/auth.guard";
import { TaskDetailsComponent } from "../task-details/task-details.component";
import { ParticipantProfileComponent } from "../../../participant/features/participant-profile/participant-profile.component";
import { ParticipantEventRegistrationComponent } from "../../../participant/features/participant-event-registration/participant-event-registration.component";

export const routes: Routes = [
    {
        path: 'tasks',
        component: TasksComponent
    },
    {
        path: 'tasks/register',
        component: TaskFormComponent,
        canActivate: [authGuard]
    },
    {
        path: 'tasks/:id',
        component: TaskDetailsComponent
    },
    {
        path: 'tasks/:id/participants/:participantId',
        component: ParticipantProfileComponent
    },
    {
        path: 'tasks/:id/update',
        component: TaskFormComponent,
        canActivate: [authGuard]
    },
    {
        path: 'tasks/:id/registration',
        component: ParticipantEventRegistrationComponent,
        canActivate: [authGuard]
    }
];
