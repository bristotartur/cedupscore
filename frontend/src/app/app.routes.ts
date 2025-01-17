import { Routes } from '@angular/router';
import { NotFoundComponent } from './shared/components/not-found/not-found.component';

export const routes: Routes = [
    {
        path: '',
        loadChildren: () => import('./home/features/shell/home.routes')
            .then((m) => m.routes)
    },
    {
        path: '',
        loadChildren: () => import('./user/features/shell/user.routes')
            .then((m) => m.routes)
    },
    {
        path: '',
        loadChildren: () => import('./edition/features/shell/edition.routes')
            .then((m) => m.routes)
    },
    {
        path: '',
        loadChildren: () => import('./task/features/shell/task.routes')
            .then((m) => m.routes)
    },
    {
        path: '',
        loadChildren: () => import('./participant/features/shell/participant.routes')
            .then((m) => m.routes)
    },
    {
        path: 'not-found',
        component: NotFoundComponent
    },
    {
        path: '**',
        redirectTo: '/not-found',
        pathMatch: 'full'
    },
];
