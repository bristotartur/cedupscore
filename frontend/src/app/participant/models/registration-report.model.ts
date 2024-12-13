import { ParticipantWithProblem } from "./participant-with-problem.model";

export interface RegistrationReport {
    total: number,
    added: number,
    notAdded: number,
    registered: number,
    problems: number,
    rejected: number,
    notRegistered: number,
    participantsWithProblems: ParticipantWithProblem[]
}