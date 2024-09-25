import { RoleType } from "../../shared/enums/role-type.enum";

export interface User {
    id: number,
    name: number,
    email: string,
    role: RoleType
}