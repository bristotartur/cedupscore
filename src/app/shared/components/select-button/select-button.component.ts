import { NgClass } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Option } from '../../models/option.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-select-button',
  standalone: true,
  imports: [
    NgClass,
    FormsModule
  ],
  templateUrl: './select-button.component.html',
  styleUrls: ['./select-button.component.scss']
})
export class SelectButtonComponent implements OnInit {

  @Input('name') selectName!: string;
  @Input() options!: Option[];
  @Input('disabled') isDisabled: boolean = false;
  @Input('simplified') isSimplified: boolean = false;
  @Input() customClass: string = '';
  @Input() selectedValue!: string | number;

  @Output() option = new EventEmitter<string | number>();

  ngOnInit(): void {
    if (!this.selectedValue) {
      this.selectedValue = '';
    }
  }

  onOptionSelected(event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    const selectedValue = selectElement.value;

    this.option.emit(isNaN(+selectedValue) ? selectedValue : +selectedValue);
  }

}
