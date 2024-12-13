import { Modality } from "../enums/modality.enum";
import { ParticipantType } from "../enums/participant-type.enum";
import { SportType } from "../enums/sport-type.enum";
import { Status } from "../enums/status.enum";
import { TaskType } from "../enums/task-type.enum";
import { EventScore } from "./event-score.model";

export interface EventModel {
    id: number,
    name: string,
    status: Status,
    allowedParticipantType: ParticipantType,
    modality: Modality
    minParticipantsPerTeam: number,
    maxParticipantsPerTeam: number,
    startedAt: Date,
    endedAt: Date,
    sportType?: SportType,
    taskType?: TaskType,
    description?: string,
    editionId: number,
    responsibleUserId: number,
    scores: EventScore[]
}
