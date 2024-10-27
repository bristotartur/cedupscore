import { Component, HostListener, Input } from '@angular/core';
import { TeamPosition } from '../../models/team-postion.model';
import { NgClass } from '@angular/common';
import { EventModel } from '../../models/event.model';
import { calculateTeamsPositions, transformDate, transformStatus } from '../../utils/common-utils';
import { RouterLink } from '@angular/router';
import { EventScore } from '../../models/event-score.model';
import { Status } from '../../enums/status.enum';

@Component({
  selector: 'app-event-card',
  standalone: true,
  imports: [
    NgClass,
    RouterLink
  ],
  templateUrl: './event-card.component.html',
  styleUrl: './event-card.component.scss'
})
export class EventCardComponent {

  @Input({ required: true }) event!: EventModel;

  status!: string;
  date!: string;
  teamsPostions!: TeamPosition[];
  names: string[] = [];
  eventUrl!: string;
  linkMessage!: string;

  ngOnInit(): void {
    this.status = transformStatus(this.event.status, 'o');
    this.date = transformDate(new Date(this.event.endedAt), 'full', this.event.status);
    this.setPositions();
    this.names = this.teamsPostions.map(pos => pos.name);
    this.eventUrl = this.createUrl();
    this.onResize();
  }

  @HostListener('window:resize', ['$event'])
  onResize(): void {
    const screenWidth = window.innerWidth;

    if (screenWidth < 480) {
      this.linkMessage = 'Detalhes';
      this.date = transformDate(new Date(this.event.endedAt), 'reduced', this.event.status);
      this.reduceNames();
    } else {
      this.linkMessage = 'Mais detalhes';
      this.date = transformDate(new Date(this.event.endedAt), 'full', this.event.status);
      this.restoreNames();
    }
  }

  private reduceNames(): void {
    const reducedPositions = this.teamsPostions.map(team => {
      const name = team.name.split(/[\s-]/);
      return { ...team, name: name[0] };
    });
    this.teamsPostions = reducedPositions;
  }

  private restoreNames(): void {
    const restoredPositions = this.teamsPostions.map((team, index) => ({
      ...team,
      name: this.names[index]
    }));
    this.teamsPostions = restoredPositions;
  }

  private setPositions(): void {
    const status = this.event.status;
    const scores = this.event.scores;

    if (status === Status.ENDED || status === Status.OPEN_FOR_EDITS) {
      this.teamsPostions = calculateTeamsPositions(scores).slice(0, 3);
      return;
    }
    if (status === Status.CANCELED) {
      this.teamsPostions = this.getBlankPositions(scores, true).slice(0, 3);
      return;
    }
    this.teamsPostions = this.getBlankPositions(scores, false).slice(0, 3);
  }

  private getBlankPositions(eventScores: EventScore[], isEnded: boolean): TeamPosition[] {
    const positions: TeamPosition[] = [];

    eventScores.forEach(eventScore => {
      if (isEnded) {
        positions.push({ position: 0, name: eventScore.team.name, score: 0 });
      } else {
        positions.push({ position: 0, name: '???', score: 0 });
      }
    });
    return positions;
  }

  private createUrl(): string {
    if (this.event.taskType !== undefined) {
      return `/tasks/${this.event.id}`
    }
    return `/sports/${this.event.id}`;
  }

  onClick(): void {
    document.documentElement.scrollTop = 0;
  }

}
