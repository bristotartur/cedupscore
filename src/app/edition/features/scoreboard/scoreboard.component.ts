import { Component, HostListener, inject, OnInit, ViewChild } from '@angular/core';
import { PageBodyComponent } from '../../../core/components/page-body/page-body.component';
import { LeaderboardComponent } from '../../../shared/components/leaderboard/leaderboard.component';
import { EditionService } from '../../services/edition.service';
import { Edition } from '../../models/edition.model';
import { TeamPosition } from '../../../shared/models/team-postion.model';
import { TeamScoreboard } from '../../models/team-scoreboard.model';
import { SelectButtonComponent } from '../../../shared/components/select-button/select-button.component';
import { BehaviorSubject, filter, from, map, Observable, switchMap, tap, toArray } from 'rxjs';
import { CommonModule } from '@angular/common';
import { Option } from '../../../shared/models/option.model';
import { UserService } from '../../../user/services/user.service';
import { OptionsButtonComponent } from '../../../shared/components/options-button/options-button.component';
import { Status } from '../../../shared/enums/status.enum';
import { ExceptionResponse } from '../../../shared/models/exception-response.model';
import { SelectionPopupComponent } from '../../../shared/components/selection-popup/selection-popup.component';
import { calculateTeamsPositions, getPossibleStatuses, transformStatus } from '../../../shared/utils/common-utils';
import { AlertPopupComponent } from '../../../shared/components/alert-popup/alert-popup.component';

const openPopupMessage = 'Ao abrir uma nova edição, esta passará a ser a edição atual do sistema, e todas as novas inscrições e eventos adicionados serão relacionados a ela. Deseja continuar?';
const deletePopupMessage = 'Ao remover uma edição, todas as suas inscrições e os novos participantes adicionados serão excluídos. Deseja continuar?'
const updatePopupMessage = 'Alterar o status de uma edição pode resultar nas seguintes consequências: a edição poderá se tornar permanente e não poderá ser removida, alguns tipos de participantes não poderão mais se inscrever, e a inserção de novos eventos poderá ser bloqueada. Deseja continuar?';


@Component({
  selector: 'app-scoreboard',
  standalone: true,
  imports: [
    CommonModule,
    PageBodyComponent,
    LeaderboardComponent,
    SelectButtonComponent,
    OptionsButtonComponent,
    AlertPopupComponent,
    SelectionPopupComponent
  ],
  templateUrl: './scoreboard.component.html',
  styleUrls: ['./scoreboard.component.scss']
})
export class ScoreboardComponent implements OnInit {

  private editionService = inject(EditionService);
  userService = inject(UserService);

  @ViewChild('statusPopup') statusPopup!: SelectionPopupComponent;
  @ViewChild('alertPopup') alertPopup!: AlertPopupComponent;
  @ViewChild('errorPopup') errorPopup!: AlertPopupComponent;

  selectedEdition$ = new BehaviorSubject<Edition | null>(null);
  latestEdition$ = new BehaviorSubject<Edition | null>(null);
  postions$ = new BehaviorSubject<TeamPosition[]>([]);
  editions$ = new BehaviorSubject<Edition[]>([]);
  teamsData$!: Observable<TeamScoreboard[]>;

  names: string[] = [];
  options: Option[] = [];
  buttonOptions: Option[] = [];
  statusOptions: Option[] = [];
  selectedOption: number = 0;
  popupDetails = { title: '', message: '', buttonMessage: '', action: () => {} }; 
  errorMessage = '';

  ngOnInit(): void {
    this.editionService.listEditions().pipe(
      tap(editions => {
        this.setOptions(editions);
        this.setup(editions[0]);
        this.selectOption(0);
      })
    ).subscribe(editions => {
      this.editions$.next(editions);
    });

    this.selectedEdition$.pipe(
      filter(edition => !!edition),
      switchMap(edition => this.getTeamsPositions(edition!))
    ).subscribe(positions => {
      this.postions$.next(positions)
      if (window.innerWidth < 768) this.reduceNames();
    });

    this.teamsData$ = this.selectedEdition$.pipe(
      filter(edition => !!edition),
      switchMap(edition => this.getTeamScoreboard(edition!))
    );
    this.onResize();
  }

  @HostListener('window:resize', ['$event'])
  onResize(): void {
    const screenWidth = window.innerWidth;

    if (screenWidth < 768) {
      this.reduceNames();
    } else {
      this.restoreNames();
    }
  }

  getTeamsPositions(edition: Edition): Observable<TeamPosition[]> {
    return from(edition.teamsScores).pipe(
      toArray(),
      map(sortedTeams => calculateTeamsPositions(sortedTeams)),
      tap(positions => {
        this.names = positions.map(pos => pos.name);
      })
    );
  }

  getTeamScoreboard(edition: Edition): Observable<TeamScoreboard[]> {
    return from(edition.teamsScores).pipe(
      toArray(), 
      map(teamsScores => teamsScores.sort((a, b) => b.score - a.score)),
      map(sortedTeams => sortedTeams.map(teamScore => ({
        score: teamScore.score,
        tasksWon: teamScore.tasksWon,
        sportsWon: teamScore.sportsWon
      })))
    );
  }

  private reduceNames(): void {
    const reducedPositions = this.postions$.value.map(team => {
      const name = team.name.split(/[\s-]/);
      return { ...team, name: name[0] };
    });
    this.postions$.next(reducedPositions);
  }

  private restoreNames(): void {
    const restoredPositions = this.postions$.value.map((team, index) => ({
      ...team,
      name: this.names[index]
    }));
    this.postions$.next(restoredPositions);
  }

  onButtonOptionSelected(value: string | number): void {
    switch(value) {
      case 'update':
        this.popupDetails = {
          title: 'Atualizar status?', message: updatePopupMessage, buttonMessage: 'Prosseguir', action: () => this.statusPopup?.openModal()
        }
        this.statusPopup?.openModal(); 
        break;
      case 'open':
        this.popupDetails = {
          title: 'Abrir nova edição?', message: openPopupMessage, buttonMessage: 'Abrir', action: () => this.openEdition()
        }
        this.alertPopup?.openModal();
        break;
      case 'delete':
        this.popupDetails = {
          title: 'Excluir edição?', message: deletePopupMessage, buttonMessage: 'Remover', action: () => this.deleteEdition()
        }
        this.alertPopup?.openModal();
        break;
    }
  }

  openEdition(): void {
    this.editionService.openNewEdition().pipe(
      switchMap(() => this.editionService.listEditions()),
      tap(editions => {
        this.setOptions(editions);
        this.setup(editions[0]);
        this.selectOption(0);
      })
    ).subscribe({
      next: editions => {
        this.editions$.next(editions)
      },
      error: (err: ExceptionResponse) => {
        this.errorMessage = err.details;
        this.errorPopup?.openModal();
      }
    });
  }

  deleteEdition(): void {
    const id = this.latestEdition$.value?.id;

    if (!id) return;

    this.editionService.deleteEdition(id).pipe(
      switchMap(() => this.editionService.listEditions()),
      tap(editions => {
        this.setOptions(editions);
        this.setup(editions[0]);
        this.selectOption(0);
      })
    ).subscribe({
      next: editions => {
        this.editions$.next(editions)
      },
      error: (err: ExceptionResponse) => {
        this.errorMessage = err.details;
        this.errorPopup?.openModal();
      }
    });
  }

  updateEditionStatus(status: string | number): void {
    const id = this.latestEdition$.value?.id;
    const newStatus = status as Status;

    if (!id) return;

    this.editionService.updateEditionStatus(id, newStatus).subscribe({
      next: edition => {
        this.setup(edition);
      },
      error: (err: ExceptionResponse) => {
        this.errorMessage = err.details;
        this.errorPopup?.openModal();
      }
    });
  }

  private setOptions(editions: Edition[]): void {
    this.options = editions.map((edition, index) => {
      let startDate = new Date(edition.startDate);
      let year = startDate.getFullYear().toString();

      return { name: year, value: index };
    });
  }

  selectOption(value: string | number): void {
    this.editions$.subscribe(editions => {
      const selectedEdition = editions[+value];

      this.selectedOption = +value;
      this.selectedEdition$.next(selectedEdition);
    });
  }

  private setup(edition: Edition): void {
    this.latestEdition$.next(edition);
    this.setButtonOptions(edition);
    this.setStatusOptions(edition.status);
  }

  private setButtonOptions(edition: Edition): void {
    this.buttonOptions = [{ name: 'Atualizar status da edição atual', value: 'update' }];
    const status = edition.status;

    if (status == 'ENDED' || status == 'CANCELED') {
      this.buttonOptions = [{ name: 'Abrir nova edição', value: 'open' }, ...this.buttonOptions];
      return;
    }
    if (status == 'SCHEDULED') {
      this.buttonOptions = [...this.buttonOptions, { name: 'Remover edição atual', value: 'delete' }];
      return;
    }
  }

  private setStatusOptions(status: Status): void {
    this.statusOptions = getPossibleStatuses(status)
      .map(status => {
        return { name: transformStatus(status, 'a'), value: status }
      });
  }

}
