import { HttpErrorResponse } from "@angular/common/http";
import { ParticipantType } from "../enums/participant-type.enum";
import { Observable, throwError } from "rxjs";
import { ExceptionResponse } from "../models/exception-response.model";
import { RoleType } from "../enums/role-type.enum";
import { Gender } from "../enums/gender.enum";
import { Status } from "../enums/status.enum";
import { TeamScore } from "../../edition/models/team-score.model";
import { TeamPosition } from "../models/team-postion.model";
import { EventScore } from "../models/event-score.model";

export function handleError(err: HttpErrorResponse): Observable<never> {
  let exceptionResponse: ExceptionResponse = {
    title: err.error.title || 'Error.',
    status: err.status,
    details: err.error.details || 'Sem detalhes disponíveis.',
    developerMessage: err.error.developerMessage || 'Sem mensagem do desenvolvedor.',
    timestamp: err.error.timestamp || new Date().toISOString()
  };
  
  if (err.error.fields) {
    exceptionResponse.fields = err.error.fields;
  }
  if (err.error.fieldsMessages) {
    exceptionResponse.fieldsMessages = err.error.fieldsMessages;
  }
  return throwError(() => exceptionResponse);
}

export function transformParticipantStatus(status: boolean | 'Ativo' | 'Inativo'): string {
  return (typeof(status) !== 'boolean') 
    ? status 
    : (status) ? 'Ativo' : 'Inativo';
}

export function transformParticipantType(value: ParticipantType): string {
  switch (value) {
    case 'STUDENT': return 'Aluno';
    case 'TEACHER': return 'Professor';
    case 'PARENT': return 'Pai';
    case 'STUDENT_PARENT': return 'Pai e aluno';
    case 'TEACHER_PARENT': return 'Pai e professor';

    default: return 'Desconhecido';
  }
}

export function transformGenderType(value: Gender): string {
  switch (value) {
    case Gender.MALE: return 'Masculino';
    case Gender.FEMALE: return 'Feminino';

    default: return 'Indefinido';
  }
}

export function reduceName(name: string, limit: number): string {
  if (name.length < limit) return name;

  let names = name.split(/\s+/);
  let firstName = names[0];
  
  if (firstName.length > Math.floor(limit / 4 * 3) && firstName.length <= limit - 3) return firstName;
  
  const conectives = new Set(['DA', 'DE', 'DO', 'DAS', 'DOS']);
  const len = names.length;
  const redecedName = (len >= 3 && conectives.has(names[len - 2]))
    ? `${firstName} ${names[len - 2]} ${names[len - 1]}`
    : `${firstName} ${names[len - 1]}`;

  return (redecedName.length < limit)
    ? redecedName
    : `${redecedName.substring(0, limit - 4).trim()}...`;
}

export function checkEmail(email: string): boolean {
  const pattern = /^[a-zA-Z0-9_+&*-]+(?:\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,7}$/;

  return pattern.test(email);
}

export function transformRoleType(value: RoleType): string {
  switch(value) {
    case RoleType.SUPER_ADMIN: return 'Super administrador';
    case RoleType.EDITION_ADMIN:return 'Administrador de edição';
    case RoleType.EVENT_ADMIN: return 'Administrador de evento';

    default: return 'Cargo desconhecido';
  }
}

export function transformStatus(status: Status, sufix: 'a' | 'o'): string {
  switch(status) {
    case Status.SCHEDULED: return `Agendad${sufix}`;
    case Status.IN_PROGRESS: return 'Em andamento';
    case Status.STOPPED: return `Parad${sufix}`;
    case Status.CANCELED: return `Cancelad${sufix}`;
    case Status.ENDED: return `Encerrad${sufix}`;
    case Status.OPEN_FOR_EDITS: return `Em edição`;

    default: return 'Status desconhecido';
  }
}

export function getPossibleStatuses(status: Status): Status[] {
  switch(status) {
    case Status.SCHEDULED: return [Status.IN_PROGRESS];
    case Status.IN_PROGRESS: return [Status.STOPPED, Status.ENDED, Status.CANCELED];
    case Status.STOPPED: return [Status.IN_PROGRESS, Status.ENDED, Status.CANCELED];
    case Status.CANCELED: return [];
    case Status.ENDED: return [Status.OPEN_FOR_EDITS];
    case Status.OPEN_FOR_EDITS: return [Status.ENDED];

    default: return [];
  }
}

export function transformDate(date: Date, type: 'full' | 'reduced'): string {
  const currentYear = new Date().getFullYear();
  const options: Intl.DateTimeFormatOptions = { day: '2-digit' };

  if (date.getFullYear() !== currentYear) {
    options.year = 'numeric';
  }
  switch (type) {
    case 'full': {
      options.month = 'short'
      return date.toLocaleDateString('pt-BR', options);
    }
    case 'reduced': {
      options.month = '2-digit'
      return date.toLocaleDateString('pt-BR', options);
    }
  }
}

export function calculateTeamsPositions(teamsScores: TeamScore[] | EventScore[]): TeamPosition[] {
  const sortedByScore = [...teamsScores].sort((a, b) => b.score - a.score);

  let currentPos = 1;
  let previousScore = teamsScores[0]?.score;
  let tiedTeamsCount = 0;

  return sortedByScore.map((score, index) => {
    if (score.score === previousScore && index !== 0) {
      tiedTeamsCount++;
    } else if (index !== 0) {
      currentPos += tiedTeamsCount + 1;
      tiedTeamsCount = 0;
    }
    previousScore = score.score;

    if ((score as TeamScore).sportsWon !== undefined) { 
      return { position: currentPos, name: score.team.name };
    }
    return { position: currentPos, name: score.team.name, score: score.score };
  });
}
