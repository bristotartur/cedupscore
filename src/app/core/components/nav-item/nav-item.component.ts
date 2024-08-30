import { NgClass } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-nav-item',
  standalone: true,
  imports: [
    NgClass,
    RouterLink
  ],
  templateUrl: './nav-item.component.html',
  styleUrl: './nav-item.component.scss'
})
export class NavItemComponent {

  @Input() itemName!: string;
  @Input() link!: string;
  @Input() iconClass!: string;
  @Input() isSelected!: boolean;

  @Output() select = new EventEmitter<void>();

  onClick(): void {
    this.select.emit();
  }

}
