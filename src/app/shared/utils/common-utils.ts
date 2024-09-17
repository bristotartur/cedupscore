import { ParticipantType } from "../enums/participant-type.enum";

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
    
    if (firstName.length > 15 && firstName.length <= limit - 3) return firstName;
    
    const conectives = new Set(['DA', 'DE', 'DO', 'DAS', 'DOS']);
    const len = names.length;
    const redecedName = (len >= 3 && conectives.has(names[len - 2]))
        ? `${firstName} ${names[len - 2]} ${names[len - 1]}`
        : `${firstName} ${names[len - 1]}`;

    return (redecedName.length < limit)
        ? redecedName
        : `${redecedName.substring(0, limit - 4).trim()}...`;
}
