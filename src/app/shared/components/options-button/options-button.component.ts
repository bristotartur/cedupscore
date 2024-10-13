import { Component, ElementRef, EventEmitter, HostListener, Input, Output, ViewChild } from '@angular/core';
import { Option } from '../../models/option.model';
import { RouterLink } from '@angular/router';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-options-button',
  standalone: true,
  imports: [
    RouterLink,
    NgClass
  ],
  templateUrl: './options-button.component.html',
  styleUrl: './options-button.component.scss'
})
export class OptionsButtonComponent {

  @ViewChild('container') container!: ElementRef;

  @Input({ required: true }) options!: Option[];
  @Input({ required: true }) iconClass!: string;
  @Input('disabled') isDisabled: boolean = false; 

  @Output() optionSelected = new EventEmitter<string | number>();

  isOptionsVisible: boolean = false;
  clicksCount: number = 0;

  @HostListener('document:click', ['$event'])
  handleDocumentClick(event: MouseEvent) {
    const clickedElement = event.target as HTMLElement;
    const container = this.container.nativeElement;

    if (container !== clickedElement && container.contains(clickedElement)) return;

    this.clicksCount++;

    if (this.isOptionsVisible && this.clicksCount > 1) {
      this.showOptions();
    }
  }

  showOptions(): void {
    if (this.isDisabled) return;

    this.isOptionsVisible = !this.isOptionsVisible;
    this.clicksCount = 0;
  }

  onClick(event?: Event, value?: string | number): void {
    if (event) event.stopPropagation();
    if (this.isDisabled) return;

    this.isOptionsVisible = false;
    this.optionSelected.emit(value);
    
    document.documentElement.scrollTop = 0;
  }

}
