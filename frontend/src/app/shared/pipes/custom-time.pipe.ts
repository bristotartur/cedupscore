import { Pipe, PipeTransform } from "@angular/core";
import { Status } from "../enums/status.enum";

@Pipe({
  name: 'customTime',
  standalone: true
})
export class CustomTime implements PipeTransform {

    transform(value: Date | undefined, status?: Status): string {
        if (!value) return '--:--';

        const formattedTime = new Intl.DateTimeFormat('pt-BR', { hour: '2-digit', minute: '2-digit' }).format(new Date(value));
        
        if (!status) {
            return formattedTime
        }
        const showTime = [Status.CANCELED, Status.ENDED, Status.OPEN_FOR_EDITS].includes(status);
    
        return (showTime) ? formattedTime : '--:--';
      }

}
