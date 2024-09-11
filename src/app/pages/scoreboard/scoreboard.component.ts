import { Component, HostListener, inject, OnInit } from '@angular/core';
import { PageBodyComponent } from "../../core/components/page-body/page-body.component";
import { LeaderboardComponent } from "../../shared/components/leaderboard/leaderboard.component";
import { EditionService } from '../../features/edition/services/edition.service';
import { Edition } from '../../features/edition/models/edition.model';
import { TeamPosition } from '../../shared/models/team-postion.model';
import { TeamScoreboard } from '../../features/edition/models/team-scoreboard.model';
import { SelectButtonComponent } from "../../shared/components/select-button/select-button.component";
import { BehaviorSubject, filter, from, map, Observable, switchMap, tap, toArray } from 'rxjs';
import { CommonModule } from '@angular/common';
import { TeamScore } from '../../features/edition/models/team-score.model';

@Component({
  selector: 'app-scoreboard',
  standalone: true,
  imports: [
    PageBodyComponent,
    LeaderboardComponent,
    SelectButtonComponent,
    CommonModule
  ],
  templateUrl: './scoreboard.component.html',
  styleUrls: ['./scoreboard.component.scss']
})
export class ScoreboardComponent implements OnInit {

  private editionService = inject(EditionService);

  selectedEdition$ = new BehaviorSubject<Edition | null>(null);
  postions$ = new BehaviorSubject<TeamPosition[]>([]);
  editions$!: Observable<Edition[]>;
  teamsData$!: Observable<TeamScoreboard[]>;

  names: string[] = [];
  options: { name: string, value: number }[] = [];

  constructor() {
    this.editions$ = this.editionService.listEditions();
  }

  ngOnInit(): void {
    this.editions$.pipe(
      tap(editions => this.setOptions(editions)),
      tap(editions => this.selectedEdition$.next(editions[0]))
    ).subscribe();

    this.selectedEdition$.pipe(
      filter(edition => !!edition),
      switchMap(edition => this.getTeamsPositions(edition!))
    ).subscribe(positions => {
      this.postions$.next(positions)
      if (window.innerWidth < 768) this.reduceNames();
    });

    this.teamsData$ = this.selectedEdition$.pipe(
      filter(edition => !!edition),
      switchMap(edition => this.getTeamScoreboard(edition!))
    );
    this.onResize();
  }

  setOptions(editions: Edition[]): void {
    this.options = editions.map((edition, index) => {
      let startDate = new Date(edition.startDate);
      let year = startDate.getFullYear().toString();

      return { name: year, value: index };
    });
  }

  getTeamsPositions(edition: Edition): Observable<TeamPosition[]> {
    return from(edition.teamsScores).pipe(
      toArray(),
      map(teamsScores => teamsScores.sort((a, b) => b.score - a.score)),
      map(sortedTeams => this.calculateTeamsPositions(sortedTeams)),
      tap(positions => {
        this.names = positions.map(pos => pos.name);
      })
    );
  }

  private calculateTeamsPositions(sortedTeams: TeamScore[]): TeamPosition[] {
    let currentPos = 1;
    let previousScore = sortedTeams[0]?.score;
    let tiedTeamsCount = 0;

    return sortedTeams.map((teamScore, index) => {
      if (teamScore.score === previousScore && index !== 0) {
        tiedTeamsCount++;
      } else if (index !== 0) {
        currentPos += tiedTeamsCount + 1;
        tiedTeamsCount = 0;
      }
      previousScore = teamScore.score;

      return { position: currentPos, name: teamScore.team.name };
    });
  }

  getTeamScoreboard(edition: Edition): Observable<TeamScoreboard[]> {
    return from(edition.teamsScores).pipe(
      toArray(), 
      map(teamsScores => teamsScores.sort((a, b) => b.score - a.score)),
      map(sortedTeams => sortedTeams.map(teamScore => ({
        score: teamScore.score,
        tasksWon: teamScore.tasksWon,
        sportsWon: teamScore.sportsWon
      })))
    );
  }

  selectOption(value: string | number): void {
    this.editions$.subscribe(editions => {
      const selectedEdition = editions[+value];
      this.selectedEdition$.next(selectedEdition);
    });
  }

  @HostListener('window:resize', ['$event'])
  onResize(): void {
    const screenWidth = window.innerWidth;

    if (screenWidth < 768) {
      this.reduceNames();
    } else {
      this.restoreNames();
    }
  }

  reduceNames(): void {
    const reducedPositions = this.postions$.value.map(team => {
      const name = team.name.split(/[\s-]/);
      return { ...team, name: name[0] };
    });
    this.postions$.next(reducedPositions);
  }

  restoreNames(): void {
    const restoredPositions = this.postions$.value.map((team, index) => ({
      ...team,
      name: this.names[index]
    }));
    this.postions$.next(restoredPositions);
  }

}
