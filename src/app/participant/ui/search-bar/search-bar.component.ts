import { Component, ElementRef, EventEmitter, OnInit, Output, ViewChild } from '@angular/core';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import { SelectButtonComponent } from "../../../shared/components/select-button/select-button.component";
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { debounceTime } from 'rxjs';

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [
    SelectButtonComponent,
    ReactiveFormsModule,
    NgxMaskDirective
  ],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.scss',
  providers: [provideNgxMask()]
})
export class SearchBarComponent implements OnInit {
  
  @ViewChild('searchBar') inputField!: ElementRef;

  @Output('valueChange') inputValueChange = new EventEmitter<string>();
  @Output() searchTypeChange = new EventEmitter<'name' | 'cpf'>();
  
  placeholder = 'Pesquisar por nome';
  inputValue = new FormControl<string>('');
  mask: string | null = null;
  
  options = [
    { name: 'Nome', value: 'name' },
    { name: 'CPF', value: 'cpf' }
  ];

  constructor() {
    this.inputValue.valueChanges.pipe(
      debounceTime(500)
    ).subscribe(value => {
      this.inputValueChange.emit(this.formatValue(value ?? ''));
    });
  }
  
  ngOnInit(): void {
    setTimeout(() => {
      this.inputField.nativeElement.focus();
    }, 100);
  }

  selectOption(value: string | number): void {
    switch(value) {
      case 'name':
        this.placeholder = 'Pesquisar por nome';
        this.mask = null;
        this.emitSearchChange(value);
        break;
      case 'cpf':
        this.placeholder = 'Pesquisar por CPF';
        this.mask = '000.000.000-00';
        this.emitSearchChange(value);
        break;
    }
    this.inputValue.setValue('');
    this.inputField.nativeElement.focus();
  }

  emitSearchChange(value: 'name' | 'cpf') {
    this.searchTypeChange.emit(value);
  }

  private formatValue(value: string): string {
    if (this.mask === '000.000.000-00') {
      return value.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
    }
    return value;
  }

}
