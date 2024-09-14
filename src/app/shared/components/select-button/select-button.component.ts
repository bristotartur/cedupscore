import { NgClass } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Option } from '../../models/option.model';

@Component({
  selector: 'app-select-button',
  standalone: true,
  imports: [NgClass],
  templateUrl: './select-button.component.html',
  styleUrls: ['./select-button.component.scss']
})
export class SelectButtonComponent {

  @Input('name') selectName!: string;
  @Input() options!: Option[];
  @Input('simplified') isSimplified: boolean = false;
  @Input() customClass: string = '';

  @Output() option = new EventEmitter<string | number>();

  onOptionSelected(event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    const selectedValue = selectElement.value;

    this.option.emit(isNaN(+selectedValue) ? selectedValue : +selectedValue);
  }

}
