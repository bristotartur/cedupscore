import { TeamScore } from './team-score.model';

export interface Edition {
    id: number,
    status: string,
    startDate: Date,
    closingData: Date,
    teamsScores: TeamScore[],
    links: any[]
}