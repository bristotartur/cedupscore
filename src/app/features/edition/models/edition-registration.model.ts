import { Team } from "../../../shared/models/team.model"

export interface EditionRegistration {
    id: number,
    editionId: number
    team: Team
}