import { Gender } from '../../shared/enums/gender.enum';
import { ParticipantType } from '../../shared/enums/participant-type.enum';
import { EditionRegistration } from '../../edition/models/edition-registration.model';
import { EventRegistration } from '../../shared/models/event-registration.model';

export interface Participant {
    id: number,
    name: string,
    cpf?: string,
    type: ParticipantType,
    gender: Gender,
    isActive: boolean,
    eventRegistration?: EventRegistration,
    editionRegistrations: EditionRegistration[]
}