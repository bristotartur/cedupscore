import { NgClass } from '@angular/common';
import { Component, Input } from '@angular/core';
import { TeamPosition } from '../../models/team-postion.model';

@Component({
  selector: 'app-leaderboard',
  standalone: true,
  imports: [NgClass],
  templateUrl: './leaderboard.component.html',
  styleUrl: './leaderboard.component.scss'
})
export class LeaderboardComponent {

  @Input() columns!: string[];
  @Input('legend') legends!: string[];
  @Input('positions') teamsPositions!: TeamPosition[];
  @Input('data') teamsData!: any[];

  getValues(data: any): any[] {
    return Object.values(data);
  }

}
