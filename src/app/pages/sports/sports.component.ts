import { Component } from '@angular/core';
import { PageBodyComponent } from "../../core/components/page-body/page-body.component";

@Component({
  selector: 'app-sports',
  standalone: true,
  imports: [PageBodyComponent],
  templateUrl: './sports.component.html',
  styleUrl: './sports.component.scss'
})
export class SportsComponent {

}
