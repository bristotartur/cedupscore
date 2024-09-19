import { Gender } from '../../shared/enums/gender.enum';
import { ParticipantType } from '../../shared/enums/participant-type.enum';

export interface SearchFilter {
    edition?: number | '',
    team: number | '',
    gender: Gender | '',
    type: ParticipantType | '',
    status: 'active' | 'inactive' | '',
    order: 'a-z' | 'z-a' | ''
}
