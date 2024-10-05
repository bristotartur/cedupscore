import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { ParticipantWithProblem } from '../../models/participant-with-problem.model';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-participants-table',
  standalone: true,
  imports: [NgClass],
  templateUrl: './participants-table.component.html',
  styleUrl: './participants-table.component.scss'
})
export class ParticipantsTableComponent implements OnInit, OnChanges {

  @Input({ required: true }) content!: ParticipantWithProblem[];

  isContentEmpty: boolean = true;
  headers: string[] = [];

  ngOnInit(): void {
    this.setHeaders();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['content']) {
      this.isContentEmpty = this.content.length === 0;
      this.setHeaders();
    }
  }

  private setHeaders(): void {
    const allKeys = this.content.flatMap(item => 
      Object.entries(item)
        .filter(([_, value]) => value !== undefined)
        .map(([key]) => key)
    );
    const uniqueKeys = Array.from(new Set(allKeys));

    this.headers = uniqueKeys
      .map(key => this.mapKeyToHeader(key))
      .filter(key => key);
  }
  
  private mapKeyToHeader(key: string): string {
    switch (key) {
      case 'name': return 'Nome';
      case 'cpf': return 'CPF';
      case 'gender': return 'Gênero';
      case 'type': return 'Tipo';
      case 'teamName': return 'Equipe';
      case 'message': return 'Motivo do erro';
      default: return '';
    }
  }

  getItemValues(item: ParticipantWithProblem): string[] {
    return Object.entries(item)
      .filter(([key, value]) => value !== undefined && key !== 'teamId')
      .map(([_, value]) => {
        if (!value) return 'N/A'
        return String(value)
      });
  }

}
