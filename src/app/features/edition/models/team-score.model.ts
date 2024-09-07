import { Team } from "../../../shared/models/team.model";

export interface TeamScore {
    id: number,
    score: number,
    tasksWon: number,
    sportsWon: number,
    team: Team
}