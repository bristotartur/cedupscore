import { Component, inject, OnInit } from '@angular/core';
import { PageBodyComponent } from "../../core/components/page-body/page-body.component";
import { SelectButtonComponent } from "../../shared/components/select-button/select-button.component";
import { SearchBarComponent } from "../../features/participant/components/search-bar/search-bar.component";
import { FilterComponent } from "../../features/participant/components/filter/filter.component";
import { SearchFilter } from '../../features/participant/models/search-filter.model';
import { BehaviorSubject, catchError, exhaustMap, map, Observable, of, tap } from 'rxjs';
import { Edition } from '../../features/edition/models/edition.model';
import { Participant } from '../../features/participant/models/participant.model';
import { ParticipantService } from '../../features/participant/services/participant.service';
import { EditionService } from '../../features/edition/services/edition.service';
import { PaginationResponse } from '../../shared/models/pagination-response.model';
import { Team } from '../../shared/models/team.model';
import { CommonModule } from '@angular/common';
import { TeamScore } from '../../features/edition/models/team-score.model';
import { Option } from '../../shared/models/option.model';
import { ParticipantPageComponent } from '../../features/participant/components/participant-page/participant-page.component';

@Component({
  selector: 'app-participants',
  standalone: true,
  imports: [
    PageBodyComponent,
    SelectButtonComponent,
    SearchBarComponent,
    FilterComponent,
    CommonModule,
    ParticipantPageComponent
],
  templateUrl: './participants.component.html',
  styleUrl: './participants.component.scss'
})
export class ParticipantsComponent implements OnInit {

  private participantService = inject(ParticipantService);
  private editionService = inject(EditionService);

  participantsPage$ = new BehaviorSubject<PaginationResponse<Participant> | null>(null);
  teams$ = new BehaviorSubject<Team[]>([]);
  editions$!: Observable<Edition[]>;

  editionsOptions: Option[] = [{ name: 'Geral', value: '' }];
  filter: SearchFilter = { edition: '', team: '', gender: '', type: '', status: '', order: '' };
  
  searchValue: string = '';
  currentPage: number = 1;
  totalPages: number = 1;
  currentQuery: string = '';
  currentSearchType: 'name' | 'cpf' = 'name';

  constructor() {
    this.editions$ = this.editionService.listEditions();
    this.loadParticipants();
  }

  ngOnInit(): void {
    this.editions$.pipe(
      tap(editions => {
        this.setEditinsOptions(editions);
        this.setTeamsOptions(editions[0].teamsScores);
      }),
    ).subscribe();
  }

  setEditinsOptions(editions: Edition[]): void {
    editions.forEach((edition, index) => {
      let startDate = new Date(edition.startDate);
      let year = startDate.getFullYear().toString();

      this.editionsOptions.push({ name: year, value: index + 1 });
    });
  }

  setTeamsOptions(scores: TeamScore[]): void {
    let teams: Team[] = [];
    scores.forEach(score => {
      teams.push(score.team);
    })
    this.teams$.next(teams);
  }

  onSearchTypeChange(value: 'name' | 'cpf') {
    this.currentSearchType = value;
  }

  onEditionChange(index: number | string): void {
    this.editions$.pipe(
      map(editions => {
        return (index === 0) ? editions[1] : editions[+index - 1]
      }),
      tap(edition => {
        this.setTeamsOptions(edition.teamsScores);
        this.filter = {
          ...this.filter,
          edition: (index === 0) ? '' : edition.id
        }
        this.loadParticipants();
      })
    ).subscribe();
  }

  onSearchInputChange(value: string): void {
    this.searchValue = value;
    this.loadParticipants();
  }

  onFilterChange(filter: SearchFilter): void {
    this.filter = { ...filter, edition: this.filter.edition };
    this.loadParticipants();
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.loadParticipants();

    document.documentElement.scrollTop = 0;
  }

  loadParticipants(): void {
    if (this.currentSearchType === 'name') {
      this.participantService.listParticipants(this.createQuery()).pipe(
        exhaustMap(page => of(page))
      ).subscribe(response => {
        this.participantsPage$.next(response); 
        this.totalPages = Math.ceil(response.page.totalElements / response.page.size);
        this.currentPage = response.page.number + 1;
      });
      return;
    }
    if (this.searchValue.length === 14) {
      this.participantService.findParticipantByCpf(this.searchValue).pipe(
        map(participant => this.resetParticipantsPage([participant], 1)),
        catchError(() => {
          this.participantsPage$.next(this.resetParticipantsPage([], 0))
          return of(null)
        })
      ).subscribe(response => {
        if (response) {
          this.participantsPage$.next(response);
        }
      });
    }
  }

  private createQuery(): string {
    let query = Object.entries(this.filter)
      .filter(([_, value]) => value !== '')
      .map(([key, value]) => `${key}=${value}`)
      .join('&');

    if (this.searchValue !== '') {
      query = (query) ? `${query}&name=${this.searchValue}` : `name=${this.searchValue}`;
    }
    let page = this.currentPage - 1;
    query = (query) ? `${query}&page=${page}` : `page=${page}`;

    return this.adjustQuery(query ? `?${query}` : '');
  }

  private adjustQuery(query: string) {
    const lastQuery = this.currentQuery.replace(/&?page=\d+/, '').replace(/\?&/, '?').trim();

    if (query.replace(/&?page=\d+/, '') !== lastQuery) {
      query = query.replace(/page=\d+/, 'page=0');
      this.currentPage = 1; 
    }
    this.currentQuery = query;
    return query;
  }

  private resetParticipantsPage(content: Participant[], total: number): PaginationResponse<Participant> {
    this.totalPages = 1;
    this.currentPage = 1;

    return {
      content: content,
      page: { size: 1, number: 1, totalElements: total, totalPages: 1 }
    };
  }

}
