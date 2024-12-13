import { Team } from "./team.model";

export interface EventRegistration {
    id: number,
    eventId: number,
    team: Team
}
