import { Component } from '@angular/core';
import { PageBodyComponent } from "../../core/components/page-body/page-body.component";

@Component({
  selector: 'app-scoreboard',
  standalone: true,
  imports: [PageBodyComponent],
  templateUrl: './scoreboard.component.html',
  styleUrl: './scoreboard.component.scss'
})
export class ScoreboardComponent {

}
