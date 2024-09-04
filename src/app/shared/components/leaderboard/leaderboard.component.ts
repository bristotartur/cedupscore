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
  @Input("legend") legends!: string[];
  @Input() teamsPositions!: TeamPosition[];
  @Input() teamsData!: any[];

  hasAttribute(obj: any, attr: string): boolean {
    return obj && obj.hasOwnProperty(attr);
  }

}
