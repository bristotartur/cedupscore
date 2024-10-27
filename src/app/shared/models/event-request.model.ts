import { Modality } from "../enums/modality.enum";
import { ParticipantType } from "../enums/participant-type.enum";
import { SportType } from "../enums/sport-type.enum";
import { TaskType } from "../enums/task-type.enum";

export interface EventRequest {
    type: 'task' | 'sport',
    name: string,
    allowedParticipantType: ParticipantType,
    modality: Modality
    minParticipantsPerTeam: number,
    maxParticipantsPerTeam: number,
    sportType?: SportType,
    taskType?: TaskType,
    description?: string,
    editionId: number,
    responsibleUserId: number
}