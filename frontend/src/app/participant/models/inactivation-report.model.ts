import { ParticipantWithProblem } from "./participant-with-problem.model";

export interface InactivationReport {
    total: number,
    inactivated: number,
    notInactivated: number,
    problems: number,
    participantsWithProblems: ParticipantWithProblem[]
}