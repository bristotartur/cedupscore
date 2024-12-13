import { RoleType } from "../../shared/enums/role-type.enum";

export interface User {
    id?: number,
    name: string,
    email: string,
    password?: string,
    role: RoleType
}