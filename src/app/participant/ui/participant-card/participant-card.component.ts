import { Component, HostListener, Input, OnInit, SimpleChanges } from '@angular/core';
import { reduceName, transformParticipantStatus, transformParticipantType } from '../../../shared/utils/common-utils';
import { ParticipantType } from '../../../shared/enums/participant-type.enum';

@Component({
  selector: 'app-participant-card',
  standalone: true,
  imports: [],
  templateUrl: './participant-card.component.html',
  styleUrl: './participant-card.component.scss'
})
export class ParticipantCardComponent implements OnInit{

  @Input({ required: true }) name!: string;
  @Input({ required: true }) team!: string;

  @Input({ required: true, transform: transformParticipantStatus }) 
  status!: string;

  @Input({ required: true, transform: transformParticipantType }) 
  type!: ParticipantType;

  screenWidth: number = 0;
  adjustedName: string = '';
  adjuestedTeamName: string = '';

  ngOnInit(): void {
    this.onResize();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['name']) this.onResize();
  }

  @HostListener('window:resize', ['$event'])
  onResize(): void {
    this.screenWidth = window.innerWidth;
    this.adjustName();

    this.adjuestedTeamName = (this.screenWidth <= 586) 
      ? this.team.split(/[\s-]/)[0]
      : this.team;
  }

  adjustName() {
    if (
      (this.screenWidth > 892 && this.screenWidth <= 982) ||
      (this.screenWidth > 602 && this.screenWidth <= 768) 
    ) {
      this.adjustedName = reduceName(this.name, 25);
      return;
    }
    if ((this.screenWidth > 768 && this.screenWidth <= 892) || this.screenWidth <= 480) {
      this.adjustedName = reduceName(this.name, 20);
      return;
    }
    if (this.screenWidth > 586 && this.screenWidth <= 602) {
      this.adjustedName = reduceName(this.name, 18);
      return;
    }
    if (this.screenWidth > 480 && this.screenWidth <= 586) {
      this.adjustedName = reduceName(this.name, 22);
      return;
    }
    this.adjustedName = reduceName(this.name, 50);
  }

}
