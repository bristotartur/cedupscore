import { Pipe, PipeTransform } from "@angular/core";
import { TaskType } from "../enums/task-type.enum";
import { SportType } from "../enums/sport-type.enum";
import { transformEventType } from "../utils/common-utils";

@Pipe({
  name: 'eventType',
  standalone: true
})
export class EventTypePipe implements PipeTransform {

  transform(value: TaskType | SportType | undefined) {
    return (value) ? transformEventType(value) : '';
  }

}
