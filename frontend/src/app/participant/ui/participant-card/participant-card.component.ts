import { Component, EventEmitter, HostListener, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { reduceName, transformParticipantStatus, transformParticipantType } from '../../../shared/utils/common-utils';
import { ParticipantType } from '../../../shared/enums/participant-type.enum';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-participant-card',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './participant-card.component.html',
  styleUrl: './participant-card.component.scss'
})
export class ParticipantCardComponent implements OnInit, OnChanges {

  @Input({ required: true }) id!: number;
  @Input({ required: true }) name!: string;
  @Input({ required: true }) team!: string;
  @Input({ required: true }) rootUrl!: string;

  @Input({ required: true, transform: transformParticipantStatus })
  status!: string;

  @Input({ required: true, transform: transformParticipantType })
  type!: ParticipantType;

  @Input() mode: 'view' | 'registration' = 'view';
  @Input() isSelected: boolean = false;

  @Output() onSelect = new EventEmitter<{ id: number, action: 'add' | 'remove' }>()

  root: string = '';
  screenWidth: number = 0;
  adjustedName: string = '';
  adjuestedTeamName: string = '';
  participantUrl!: string;
  buttonMessage: 'Adicionar' | 'Remover' = 'Adicionar';
  abbrMessage: string = '';

  ngOnInit(): void {
    this.createRoot();

    this.participantUrl = `${this.root}${this.id}`;
    this.onResize();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.createRoot();

    if (changes['id']) this.participantUrl = `${this.root}${this.id}`;

    if (changes['name'] || changes['team']) this.onResize();

    if (this.isSelected) {
      this.buttonMessage = 'Remover';
      this.abbrMessage = 'Remover da lista de seleção'
    } else {
      this.buttonMessage = 'Adicionar';
      this.abbrMessage = 'Adicionar a lista de seleção'
    }
  }

  private createRoot(): void {
    this.root = (this.rootUrl.includes('participants'))
      ? `${this.rootUrl}/`
      : `${this.rootUrl}/participants/`
  }

  @HostListener('window:resize', ['$event'])
  onResize(): void {
    this.screenWidth = window.innerWidth;
    this.adjustName();

    this.adjuestedTeamName = (this.screenWidth <= 586)
      ? this.team.split(/[\s-]/)[0]
      : this.team;
  }

  private adjustName() {
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

  onLinkClick(): void {
    document.documentElement.scrollTop = 0;
  }

  onButtonClick(): void {
    const action = (this.isSelected) ? 'remove' : 'add';
    this.onSelect.emit({ id: this.id, action: action });
  }

}
