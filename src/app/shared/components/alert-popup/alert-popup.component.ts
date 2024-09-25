import { Component, ElementRef, EventEmitter, HostListener, Input, Output, ViewChild } from '@angular/core';

@Component({
  selector: 'app-alert-popup',
  standalone: true,
  imports: [],
  templateUrl: './alert-popup.component.html',
  styleUrl: './alert-popup.component.scss'
})
export class AlertPopupComponent {

  @ViewChild('modal') modal!: ElementRef<HTMLDialogElement>;

  @Input({ required: true }) title!: string;
  @Input({ required: true }) message!: string;
  @Input({ required: true }) buttonMessage!: string;

  @Output() accepted = new EventEmitter();

  isModalOpen: boolean = false;
  clicksCount: number = 0;

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
  
  onClick(accepted?: boolean): void {
    this.closeModal();
    
    if (accepted) this.accepted.emit();
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
