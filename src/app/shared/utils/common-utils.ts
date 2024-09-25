import { HttpErrorResponse } from "@angular/common/http";
import { ParticipantType } from "../enums/participant-type.enum";
import { Observable, throwError } from "rxjs";
import { ExceptionResponse } from "../models/exception-response.model";

export function handleError(err: HttpErrorResponse): Observable<never> {
  let exceptionResponse: ExceptionResponse = {
    title: err.error.title || 'Server Error.',
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

export function transformParticipantStatus(status: boolean | 'Ativo' | 'Inativo') {
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
