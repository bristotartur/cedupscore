import { Component, ElementRef, inject, Input, ViewChild } from '@angular/core';
import { ControlValueAccessor, FormsModule, NgControl } from '@angular/forms';
import { NgxMaskDirective } from 'ngx-mask';

@Component({
  selector: 'app-input',
  standalone: true,
  imports: [
    FormsModule,
    NgxMaskDirective
  ],
  templateUrl: './input.component.html',
  styleUrl: './input.component.scss'
})
export class InputComponent implements ControlValueAccessor {

  private ngControl = inject(NgControl, { optional: true });

  @ViewChild('input') input!: ElementRef;

  @Input({ required: true }) type!: string;
  @Input() placeholder: string = '';
  @Input() autocomplete: string = 'off';
  @Input() isFirst: boolean = false;
  @Input() mask!: string;

  inputValue: string = '';

  protected isDisabled: boolean = false;
  protected onTouched?: () => {};
  protected onChange?: (value: string) => {};

  constructor() {
    if (this.ngControl) this.ngControl.valueAccessor = this;
  }

  writeValue(obj: string): void {
    this.inputValue = obj;
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    this.isDisabled = isDisabled;
  }

}
