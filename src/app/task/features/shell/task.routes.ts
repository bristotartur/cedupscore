import { Routes } from "@angular/router";
import { TasksComponent } from "../tasks/tasks.component";
import { TaskFormComponent } from "../task-form/task-form.component";
import { authGuard } from "../../../shared/guards/auth.guard";

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
        path: 'tasks/:id/update',
        component: TaskFormComponent,
        canActivate: [authGuard]
    }
];
