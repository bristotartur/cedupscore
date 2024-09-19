import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { Participant } from '../../models/participant.model';
import { ParticipantCardComponent } from '../participant-card/participant-card.component'
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-participant-pagination',
  standalone: true,
  imports: [
    ParticipantCardComponent,
    NgClass
  ],
  templateUrl: './participant-pagination.component.html',
  styleUrl: './participant-pagination.component.scss'
})
export class ParticipantPaginationComponent implements OnInit {

  @Input({ required: true }) totalPages!: number;
  @Input({ required: true }) content!: Participant[];
  @Input({ alias: 'edition', required: true }) editionId!: number | '';
  @Input() currentPage: number = 1;

  @Output() pageChange = new EventEmitter<number>();

  pages: number[] = [];
  teams: string[] = [];

  ngOnInit(): void {
    this.pages = this.setPages();
    this.teams = this.setTeams();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['totalPages'] || changes['currentPage']) {
      this.pages = this.setPages();
    }
    if (changes['content'] || changes['editionId']) {
      this.teams = this.setTeams();
    }
  }

  emitPageChange(page: number) {
    const total = this.totalPages;
    
    page = (page < 1) ? 1 : (page > total) ? total : page;

    this.currentPage = page;
    this.pages = this.setPages();

    this.pageChange.emit(this.currentPage);
  }

  setPages(): number[] {
    const total = this.totalPages;
  
    if (total <= 5) {
      return Array.from({ length: total }, (_, i) => i + 1);
    }
    const startPage = Math.max(1, Math.min(this.currentPage - 2, total - 4));
    const endPage = Math.min(total, startPage + 4);
  
    return Array.from({ length: endPage - startPage + 1 }, (_, i) => startPage + i);
  }

  setTeams(): string[] {
    return this.content.map(participant => {
      const registrations = participant.editionRegistrations;
      let registration = (this.editionId) 
        ? registrations.find(reg => reg.editionId === this.editionId)
        : registrations.reduce((prev, current) => prev.id > current.id ? prev : current);

      return registration ? registration.team.name : '';
    })
    .filter(teamName => teamName !== '');
  }

}
