import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { ScoreboardComponent } from './pages/scoreboard/scoreboard.component';
import { TasksComponent } from './pages/tasks/tasks.component';
import { SportsComponent } from './pages/sports/sports.component';
import { ParticipantsComponent } from './pages/participants/participants.component';
import { PunishmentsComponent } from './pages/punishments/punishments.component';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'placares', component: ScoreboardComponent },
    { path: 'tarefas' , component: TasksComponent },
    { path: 'esportes', component: SportsComponent },
    { path: 'membros', component: ParticipantsComponent },
    { path: 'recursos', component: PunishmentsComponent }
];
