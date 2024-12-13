import { NgClass } from '@angular/common';
import { Component, HostListener, Input, OnChanges, SimpleChanges } from '@angular/core';
import { TeamPosition } from '../../models/team-postion.model';
import { TeamScore } from '../../../edition/models/team-score.model';
import { EventScore } from '../../models/event-score.model';
import { calculateTeamsPositions } from '../../utils/common-utils';
import { BehaviorSubject, of } from 'rxjs';
import { Status } from '../../enums/status.enum';

@Component({
  selector: 'app-leaderboard',
  standalone: true,
  imports: [NgClass],
  templateUrl: './leaderboard.component.html',
  styleUrl: './leaderboard.component.scss'
})
export class LeaderboardComponent implements OnChanges {

  @Input({ required: true }) scores!: TeamScore[] | EventScore[];
  @Input({ required: true }) columns!: string[];
  @Input({ alias: 'legend' }) legends: string[] = [];
  @Input() fit: boolean = false;

  positions$ = new BehaviorSubject<TeamPosition[]>([]);
  names: string[] = [];
  teamsData!: any[];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['scores'] && this.scores) {
      this.setTeamsPositions(this.scores);
      this.setTeamsData(this.scores);
    }
    this.onResize();
  }

  private setTeamsPositions(scores: TeamScore[] | EventScore[]): void {
    of(calculateTeamsPositions(scores))
      .subscribe(positions => {
        this.positions$.next(positions);
        this.names = positions.map(pos => pos.name);
      });
  }

  private setTeamsData(scores: TeamScore[] | EventScore[]): void {
    scores.sort((a, b) => b.score - a.score);

    if (this.isTeamScoreArray(scores)) {
      this.teamsData = (scores as TeamScore[]).map(teamScore => ({
        score: teamScore.score,
        tasksWon: teamScore.tasksWon,
        sportsWon: teamScore.sportsWon
      }));
    } else {
      this.teamsData = (scores as EventScore[]).map(eventScore => ({
        score: eventScore.score
      }));
    }
  }

  private isTeamScoreArray(scores: TeamScore[] | EventScore[]): scores is TeamScore[] {
    return scores.length > 0 && 'tasksWon' in scores[0];
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

  private reduceNames(): void {
    const updatedPositions = this.positions$.value.map(pos => {
      const name = pos.name.split(/[\s-]/);
      return { ...pos, name: name[0] };
    });
    this.positions$.next(updatedPositions);
  }

  private restoreNames(): void {
    const updatedPositions = this.positions$.value.map((pos, index) => ({
      ...pos,
      name: this.names[index]
    }));
    this.positions$.next(updatedPositions);
  }

  getValues(data: any): any[] {
    return Object.values(data);
  }

}
