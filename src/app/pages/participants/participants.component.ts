import { Component } from '@angular/core';
import { PageBodyComponent } from "../../core/components/page-body/page-body.component";

@Component({
  selector: 'app-participants',
  standalone: true,
  imports: [PageBodyComponent],
  templateUrl: './participants.component.html',
  styleUrl: './participants.component.scss'
})
export class ParticipantsComponent {

}
