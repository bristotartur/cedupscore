import { Routes } from '@angular/router';

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
        loadChildren: () => import('./sport/features/shell/sport.routes')
            .then((m) => m.routes)
    },
    {
        path: '',
        loadChildren: () => import('./participant/features/shell/participant.routes')
            .then((m) => m.routes)
    },
    {
        path: '',
        loadChildren: () => import('./punishment/features/shell/punishment.routes')
            .then((m) => m.routes)
    }
];
