import { Component, ElementRef, HostListener, inject, OnInit, ViewChild } from '@angular/core';
import { NavItemComponent } from '../nav-item/nav-item.component';
import { Router, RouterLink } from '@angular/router';
import { NgClass } from '@angular/common';
import { UserService } from '../../../user/services/user.service';
import { AlertPopupComponent } from '../../../shared/components/alert-popup/alert-popup.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    NgClass,
    RouterLink,
    NavItemComponent,
    AlertPopupComponent
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit {

  private router = inject(Router);
  userService = inject(UserService);

  @ViewChild('userDetails', { static: true }) userDetails!: ElementRef<HTMLDetailsElement>;
  @ViewChild(AlertPopupComponent) alertPopup!: AlertPopupComponent;

  title = 'Cedupscore';
  isMenuOpen = false;
  screenWidth!: number;
  currentItem!: { name: string, iconClass: string };

  navItems = [
    { name: 'Geral', iconClass: 'fa-solid fa-house', link: '/', isSelected: true },
    { name: 'Placar', iconClass: 'fa-solid fa-medal', link: '/scoreboards', isSelected: false },
    { name: 'Tarefas', iconClass: 'fa-solid fa-list-check', link: '/tasks', isSelected: false },
    { name: 'Esportes', iconClass: 'fa-solid fa-volleyball', link: '/sports', isSelected: false },
    { name: 'Membros', iconClass: 'fa-solid fa-person', link: '/participants', isSelected: false },
    { name: 'Recursos', iconClass: 'fa-solid fa-exclamation', link: '/punishments', isSelected: false }
  ];

  ngOnInit(): void {
    this.screenWidth = window.innerWidth;
    this.router.events.subscribe(() => {
      this.updateSelectedItem(this.router.url);
    });
  }

  @HostListener('document:click', ['$event'])
  handleClick(event: MouseEvent) {
    const clickedInside = this.userDetails.nativeElement.contains(event.target as Node);

    if (!clickedInside && this.userDetails.nativeElement.open) {
      this.userDetails.nativeElement.open = false;
    }
  }

  @HostListener('window:resize', ['$event'])
  onResize(): void {
    this.screenWidth = window.innerWidth;

    if (this.screenWidth > 768 && this.isMenuOpen) {
      this.isMenuOpen = false;
    }
  }

  closeDetails(): void {
    if (this.userDetails.nativeElement.hasAttribute('open')) {
      this.userDetails.nativeElement.removeAttribute('open');
    }
  }

  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen;
  }

  selectItem(index: number): void {
    this.navItems.forEach((item, i) => {
      item.isSelected = (i == index);
      
      if (item.isSelected) {
        this.currentItem = { name: item.name, iconClass: item.iconClass };
      }
      this.isMenuOpen = false;
    });
  }

  updateSelectedItem(url: string): void {
    const index = this.navItems
      .findIndex(item => item.link == url);

    this.selectItem(index != -1 ? index : 0);
  }

  openChildModal(event: MouseEvent): void {
    event.stopPropagation();

    this.alertPopup.openModal();
    this.closeDetails();
  }

  handleLogoutAccepted() {
    localStorage.removeItem('accessToken');
    this.userService.currentUserSignal.set(null);
    this.router.navigate(['/']);
  }

}
