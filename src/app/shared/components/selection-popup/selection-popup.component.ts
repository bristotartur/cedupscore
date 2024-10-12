import { Component, ElementRef, EventEmitter, HostListener, Input, Output, ViewChild } from '@angular/core';
import { Option } from '../../models/option.model';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-selection-popup',
  standalone: true,
  imports: [NgClass],
  templateUrl: './selection-popup.component.html',
  styleUrl: './selection-popup.component.scss'
})
export class SelectionPopupComponent {

  @ViewChild('modal') modal!: ElementRef<HTMLDialogElement>;

  @Input({ required: true }) title!: string;
  @Input({ required: true }) options!: Option[];
  @Input({ required: true }) buttonMessage!: string;

  @Output() optionSelected = new EventEmitter<string | number>();

  isModalOpen: boolean = false;
  clicksCount: number = 0;
  selectedValue!: string | number;

  @HostListener('document:click', ['$event'])
  handleDocumentClick(event: MouseEvent) {
    const clickedElement = event.target as HTMLElement;
    const dialogElement = this.modal.nativeElement;

    if (dialogElement !== clickedElement && dialogElement.contains(clickedElement)) return;

    this.clicksCount++;

    if (this.isModalOpen && this.clicksCount > 1) {
      this.closeModal();
    }
  }

  onOptionSelected(value: string | number): void {
    this.selectedValue = value;
  }

  onClick(): void {
    if (!this.selectedValue) return;

    this.optionSelected.emit(this.selectedValue);
    this.selectedValue = '';
    this.closeModal();
  }
  
  openModal(): void {
    this.modal.nativeElement.showModal();
    this.isModalOpen = true;
    this.clicksCount++;
  }

  closeModal(): void {
    this.modal.nativeElement.close();
    this.isModalOpen = false;
    this.clicksCount = 0;
  }

}

