import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { Gender } from '../../../../shared/enums/gender.enum';
import { ParticipantType } from '../../../../shared/enums/participant-type.enum';
import { SelectButtonComponent } from "../../../../shared/components/select-button/select-button.component";
import { Team } from '../../../../shared/models/team.model';
import { SearchFilter } from '../../models/search-filter.model';
import { Option } from '../../../../shared/models/option.model';

@Component({
  selector: 'app-filter',
  standalone: true,
  imports: [SelectButtonComponent],
  templateUrl: './filter.component.html',
  styleUrl: './filter.component.scss'
})
export class FilterComponent implements OnChanges {

  @Input('teams') inputTeams!: Team[];
  @Output() filterChange = new EventEmitter<SearchFilter>();

  teams: Option[] = [{ name: 'Todas', value: '' }];
  filter: SearchFilter = { team: '', gender: '', type: '', status: '', ordering: '' };

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['inputTeams']) {
      this.updateTeams();
    }
  }

  updateTeams(): void {
    this.teams = [{ name: 'Todas', value: '' }];
    this.inputTeams.forEach(team => {
      let name = team.name.split(/[\s-]/)[0];
      this.teams.push({ name: name, value: team.id });
    });
  }

  genderOptions = [
    { name: 'Misto', value: '' },
    { name: 'Masculino', value: Gender.MALE },
    { name: 'Feminino', value: Gender.FEMALE }
  ];

  participantTypeOptions = [
    { name: 'Todos', value: '' },
    { name: 'Aluno', value: ParticipantType.STUDENT },
    { name: 'Professor', value: ParticipantType.TEACHER },
    { name: 'Pai', value: ParticipantType.PARENT }
  ];

  statusOptions = [
    { name: 'Todos', value: '' },
    { name: 'Ativos', value: 'active' },
    { name: 'Inativos', value: 'inactive' }
  ];
  
  orderingOptions = [
    { name: 'Normal', value: '' },
    { name: 'A-Z', value: 'a-z' },
    { name: 'Z-A', value: 'z-a' },
  ];

  updateFilter(optionName: keyof SearchFilter, value: any): void {
    this.filter[optionName] = (value === 0) ? '' : value;
    this.filterChange.emit(this.filter);
  }
  
}
