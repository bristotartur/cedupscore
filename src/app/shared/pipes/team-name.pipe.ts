import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: 'teamName',
  standalone: true
})
export class TeamNamePipe implements PipeTransform {

  private breakPoint: number = 768;

  transform(value: string, screenWidth: number, breakPoint?: number): string {
    this.breakPoint = breakPoint ?? this.breakPoint;

    return (screenWidth <= this.breakPoint)
      ? value.split(/[\s-]/)[0]
      : value;
  }

}
