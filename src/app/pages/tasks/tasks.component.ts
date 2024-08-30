import { Component } from '@angular/core';
import { PageBodyComponent } from "../../core/components/page-body/page-body.component";

@Component({
  selector: 'app-tasks',
  standalone: true,
  imports: [PageBodyComponent],
  templateUrl: './tasks.component.html',
  styleUrl: './tasks.component.scss'
})
export class TasksComponent {

}
