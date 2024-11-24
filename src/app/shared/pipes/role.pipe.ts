import { Pipe, PipeTransform } from "@angular/core";
import { RoleType } from "../enums/role-type.enum";
import { transformRoleType } from "../utils/common-utils";

@Pipe({
  name: 'role',
  standalone: true
})
export class RolePipe implements PipeTransform {

  private breakPoint: number = 768;

  transform(role: RoleType, screenWidth: number, breakPoint?: number): string {
    const transformedRole = transformRoleType(role);

    return (screenWidth <= (breakPoint ?? this.breakPoint))
      ? transformedRole.replace('inistrador', '')
      : transformedRole;
  }

}
