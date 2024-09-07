import { Component, HostListener, inject, OnInit } from '@angular/core';
import { PageBodyComponent } from "../../core/components/page-body/page-body.component";
import { LeaderboardComponent } from "../../shared/components/leaderboard/leaderboard.component";
import { EditionService } from '../../features/edition/services/edition.service';
import { Edition } from '../../features/edition/models/edition.model';
import { TeamPosition } from '../../shared/models/team-postion.model';
import { TeamScoreboard } from '../../features/edition/models/team-scoreboard.model';

@Component({
  selector: 'app-scoreboard',
  standalone: true,
  imports: [
    PageBodyComponent,
    LeaderboardComponent,
  ],
  templateUrl: './scoreboard.component.html',
  styleUrl: './scoreboard.component.scss'
})
export class ScoreboardComponent implements OnInit {

  private editionService = inject(EditionService);

  columns = ['Pts', 'TG', 'EG'];
  legend = ['TC - Tarefas ganhas', 'EC - Esportes ganhos'];

  editions: Edition[] = [];
  names: string[] = [];

  postions!: TeamPosition[];
  teamsData!: TeamScoreboard[];

  constructor() {}

  ngOnInit(): void {
    this.getEditions();
    this.onResize();
  }

  @HostListener('window:resize', ['$event'])
  onResize(): void {
    const screenWidth = window.innerWidth;

    if (screenWidth < 768) {
      this.reduceNames();
    } else {
      this.postions.forEach((team, index) => {
        team.name = this.names[index];
      });
    }
  }

  getEditions(): void {
    this.editionService.listEditions().subscribe(page => {
      this.editions = page.content;
      this.setData(this.editions[0]);
    });
  }

  setData(edition: Edition): void {
    const sortedTeams = [...edition.teamsScores].sort((a, b) => b.score - a.score);

    let currentPos = 1;
    let previousScore = sortedTeams[0]?.score;
    let tiedTeamsCount = 0;

    this.postions = [];
    this.teamsData = [];
    this.names = [];

    sortedTeams.forEach((teamScore, index) => {
      if (teamScore.score === previousScore && index !== 0) {
        tiedTeamsCount++;
      } else if (index !== 0) {
        currentPos += tiedTeamsCount + 1;
        tiedTeamsCount = 0;
      }
      this.postions.push({ position: currentPos, name: teamScore.team.name });
      this.teamsData.push({ score: teamScore.score, tasksWon: teamScore.tasksWon, sportsWon: teamScore.sportsWon });
      this.names.push(teamScore.team.name);

      previousScore = teamScore.score;
    });
    this.onResize();
  }

  reduceNames(): void {
    this.postions.forEach(team => {
      const name = team.name.split(/[\s-]/);
      if (name.length > 1) {
        team.name = name[0];
      }
    });
  }

}
