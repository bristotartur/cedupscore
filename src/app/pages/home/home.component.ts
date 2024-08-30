import { Component } from '@angular/core';
import { PageBodyComponent } from "../../core/components/page-body/page-body.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [PageBodyComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

}
