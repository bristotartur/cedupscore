import { NgClass } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
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
  styleUrl: './select-button.component.scss'
})
export class SelectButtonComponent implements OnInit, OnChanges {

  @Input('name') selectName!: string;
  @Input({ required: true }) options!: Option[];
  @Input('disabled') isDisabled: boolean = false;
  @Input('simplified') isSimplified: boolean = false;
  @Input() customClass: string = '';
  @Input() selectedValue!: string | number;
  @Input() hasOutline: boolean = true;

  @Output() option = new EventEmitter<string | number>();

  ngOnInit(): void {
    if (!this.selectName) {
      this.selectName = this.options[0].name;
    }
    if (!this.selectedValue) {
      this.selectedValue = '';
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['options']) this.selectedValue = '';
  }

  onOptionSelected(event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    const selectedValue = selectElement.value;

    this.option.emit(isNaN(+selectedValue) ? selectedValue : +selectedValue);
  }

}
