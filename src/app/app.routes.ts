import { Routes } from '@angular/router';
import { ScoreboardComponent } from './edition/features/scoreboard/scoreboard.component';
import { HomeComponent } from './home/features/home/home.component';
import { ParticipantListComponent } from './participant/features/participant-list/participant-list.component';
import { PunishmentsComponent } from './punishment/features/punishments/punishments.component';
import { SportsComponent } from './sport/features/sports/sports.component';
import { TasksComponent } from './task/features/tasks/tasks.component';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'placares', component: ScoreboardComponent },
    { path: 'tarefas' , component: TasksComponent },
    { path: 'esportes', component: SportsComponent },
    { path: 'membros', component: ParticipantListComponent },
    { path: 'recursos', component: PunishmentsComponent }
];
