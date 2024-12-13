import { Pipe, PipeTransform } from "@angular/core";
import { transformDate } from "../utils/common-utils";
import { Status } from "../enums/status.enum";

@Pipe({
  name: 'customDate',
  standalone: true
})
export class CustomDatePipe implements PipeTransform {

  transform(value: Date | undefined, type: 'full' | 'reduced', status?: Status): string {
    return (value) ? transformDate(new Date(value), type, status) : ''; 
  }

}
