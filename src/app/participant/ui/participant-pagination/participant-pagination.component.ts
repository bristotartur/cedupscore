import { Component, EventEmitter, inject, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { Participant } from '../../models/participant.model';
import { ParticipantCardComponent } from '../participant-card/participant-card.component'
import { NgClass } from '@angular/common';
import { EditionRegistration } from '../../../edition/models/edition-registration.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-participant-pagination',
  standalone: true,
  imports: [
    NgClass,
    ParticipantCardComponent
  ],
  templateUrl: './participant-pagination.component.html',
  styleUrl: './participant-pagination.component.scss'
})
export class ParticipantPaginationComponent implements OnInit {

  private router = inject(Router);

  @Input({ required: true }) totalPages!: number;
  @Input({ required: true }) content!: Participant[];
  @Input({ alias: 'edition', required: true }) editionId!: number | '';
  @Input() currentPage: number = 1;

  @Output() pageChange = new EventEmitter<number>();

  rootUrl: string = '';
  pages: number[] = [];
  teams: string[] = [];

  constructor() {
    this.rootUrl = this.router.url;
  }

  ngOnInit(): void {
    this.setPages();
    this.setTeams();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['totalPages'] || changes['currentPage']) {
      this.setPages();
    }
    if (changes['content'] || changes['editionId']) {
      this.setTeams();
    }
  }

  emitPageChange(page: number) {
    const total = this.totalPages;
    
    page = (page < 1) ? 1 : (page > total) ? total : page;

    this.currentPage = page;
    this.setPages();

    this.pageChange.emit(this.currentPage);
  }

  private setPages(): void {
    const total = this.totalPages;
  
    if (total <= 5) {
      this.pages = Array.from({ length: total }, (_, i) => i + 1);
    }
    const startPage = Math.max(1, Math.min(this.currentPage - 2, total - 4));
    const endPage = Math.min(total, startPage + 4);
  
    this.pages = Array.from({ length: endPage - startPage + 1 }, (_, i) => startPage + i);
  }

  private setTeams(): void {
    this.teams = this.content.map(participant => {
      const registrations = participant.editionRegistrations;
      
      const registration = (this.editionId) 
        ? registrations.find(reg => reg.editionId === this.editionId)
        : ((registrations.length > 0)
            ? this.getLatestRegistration(registrations)
            : null
          );
      return (registration) ? registration.team.name : '';
    })
    .filter(teamName => teamName !== '');
  }

  private getLatestRegistration(registrations: EditionRegistration[]): EditionRegistration {
    return registrations.sort((prev, current) => {
      return new Date(current.createdAt).getTime() - new Date(prev.createdAt).getTime()
    })[0];
  }

}
