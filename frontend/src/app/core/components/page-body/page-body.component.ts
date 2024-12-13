import { Component, ContentChild, TemplateRef } from '@angular/core';

@Component({
  selector: 'app-page-body',
  standalone: true,
  imports: [],
  templateUrl: './page-body.component.html',
  styleUrl: './page-body.component.scss'
})
export class PageBodyComponent {

  @ContentChild(TemplateRef) content!: TemplateRef<any>;

  isContentEmpty = false;

  ngAfterContentInit(): void {
    this.isContentEmpty  = !this.content;  
  }

}
