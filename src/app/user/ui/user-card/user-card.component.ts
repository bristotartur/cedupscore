import { Component, HostListener, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { User } from '../../models/user.model';
import { RouterLink } from '@angular/router';
import { RolePipe } from '../../../shared/pipes/role.pipe';

@Component({
  selector: 'app-user-card',
  standalone: true,
  imports: [
    RouterLink,
    RolePipe
  ],
  templateUrl: './user-card.component.html',
  styleUrl: './user-card.component.scss'
})
export class UserCardComponent implements OnInit, OnChanges {

  @Input({ required: true }) user!: User;

  screenWidth!: number;
  userLink: string = '/';

  ngOnInit(): void {
    this.onResize();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['user']) {
      this.userLink = `/users/${this.user.id}`;
    }
  }

  @HostListener('window:resize', ['$event'])
  onResize(): void {
    this.screenWidth = window.innerWidth
  }

}
