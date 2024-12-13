import { Status } from '../../shared/enums/status.enum';
import { TeamScore } from './team-score.model';

export interface Edition {
    id: number,
    status: Status,
    startDate: Date,
    closingData: Date,
    teamsScores: TeamScore[],
    links: any[]
}