import { Gender } from "../../shared/enums/gender.enum";
import { ParticipantType } from "../../shared/enums/participant-type.enum";

export interface ParticipantRegistration {
    name: string,
    cpf: string,
    gender: Gender,
    type: ParticipantType,
    teamId: number
}