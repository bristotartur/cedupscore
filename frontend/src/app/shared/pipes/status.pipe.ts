import { Pipe, PipeTransform } from "@angular/core";
import { transformStatus } from "../utils/common-utils";
import { Status } from "../enums/status.enum";

@Pipe({
  name: 'status',
  standalone: true,
})
export class StatusPipe implements PipeTransform {

  transform(value: Status | undefined, suffix: 'a' | 'o'): string {
    return (value) ? transformStatus(value, suffix) : '';
  }

}