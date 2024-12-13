import { Component, ElementRef, EventEmitter, HostListener, Input, OnInit, Output, ViewChild } from '@angular/core';
import { EventScore } from '../../models/event-score.model';
import { SelectButtonComponent } from "../select-button/select-button.component";
import { Option } from '../../models/option.model';
import { TaskType } from '../../enums/task-type.enum';
import { SportType } from '../../enums/sport-type.enum';
import { TeamNamePipe } from '../../pipes/team-name.pipe';
import { EventScoreRequest } from '../../models/event-score-request.model';

@Component({
  selector: 'app-close-event-popup',
  standalone: true,
  imports: [
    SelectButtonComponent,
    TeamNamePipe
  ],
  templateUrl: './close-event-popup.component.html',
  styleUrl: './close-event-popup.component.scss'
})
export class CloseEventPopupComponent implements OnInit {

  @ViewChild('modal') modal!: ElementRef<HTMLDialogElement>;

  @Input({ required: true }) scores: EventScore[] = [];
  @Input({ required: true }) eventType!: TaskType | SportType;

  @Output() scoresDefined = new EventEmitter<EventScoreRequest[]>();

  options: Option[] = [];
  canFinish: boolean = false;
  newScores: EventScoreRequest[] = [];
  scoresUpTo50 = [50, 40, 30, 20, 10];
  scoresUpTo100 = [100, 90, 80, 70, 50];
  screenWidth: number = window.innerWidth;
  isModalOpen: boolean = false;
  clicksCount: number = 0;

  ngOnInit(): void {
    this.setOptions();
  }

  @HostListener('window:resize', ['$event'])
  onResize(): void {
    this.screenWidth = window.innerWidth;
  }

  @HostListener('document:click', ['$event'])
  handleDocumentClick(event: MouseEvent): void {
    const clickedElement = event.target as HTMLElement;
    const dialogElement = this.modal.nativeElement;

    if (dialogElement !== clickedElement && dialogElement.contains(clickedElement)) return;

    this.clicksCount++;

    if (this.isModalOpen && this.clicksCount > 1) {
      this.closeModal();
    }
  }

  private setOptions(): void {
    this.options = [
      { name: 'Primeiro', value: 0 },
      { name: 'Segundo', value: 1 },
      { name: 'Terceiro', value: 2 },
      { name: 'Quarto', value: 3 },
      { name: 'Quinto', value: 4 }
    ];
  }

  onOptionSelected(value: string | number, index: number): void {
    const score = this.scores[index];
    const addedScoreIndex = this.newScores.findIndex(s => s.id === score.id);

    if (this.newScores.length < 1 || addedScoreIndex < 0) {
      this.newScores.push({ id: score.id, score: this.getScore(+value) });
    } else {
      this.newScores[addedScoreIndex].score = this.getScore(+value);
    }
    this.canFinish = this.newScores.length === this.scores.length;
  }

  private getScore(index: number): number {
    if (this.eventType === TaskType.NORMAL || this.eventType === TaskType.COMPLETION) {
      return this.scoresUpTo50[index]
    }
    return this.scoresUpTo100[index];
  }
  
  onClick(isDefined?: boolean): void {
    this.closeModal();
    
    if (isDefined) this.scoresDefined.emit(this.newScores);
  }
  
  openModal(): void {
    this.modal.nativeElement.showModal();
    this.isModalOpen = true;
    this.clicksCount++;
  }

  closeModal(): void {
    this.modal.nativeElement.close();
    this.isModalOpen = false;
    this.clicksCount = 0;
  }

}
