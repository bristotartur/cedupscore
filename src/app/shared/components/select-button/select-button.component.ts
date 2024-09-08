import { NgClass } from '@angular/common';
import { Component, ElementRef, EventEmitter, HostListener, inject, Input, Output } from '@angular/core';

@Component({
  selector: 'app-select-button',
  standalone: true,
  imports: [],
  templateUrl: './select-button.component.html',
  styleUrl: './select-button.component.scss'
})
export class SelectButtonComponent {

  @Input() options!: { name: string, value: string | number }[];

  @Output() option = new EventEmitter<string | number>();

  onOptionSelected(event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    const selectedValue = selectElement.value;

    this.option.emit(isNaN(+selectedValue) ? selectedValue : +selectedValue);
  }

}
