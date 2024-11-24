import { Component, inject, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { OptionsButtonComponent } from "../../../shared/components/options-button/options-button.component";
import { User } from '../../models/user.model';
import { Router } from '@angular/router';
import { PageBodyComponent } from "../../../core/components/page-body/page-body.component";
import { UserCardComponent } from '../../ui/user-card/user-card.component';
import { map } from 'rxjs';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [
    OptionsButtonComponent,
    PageBodyComponent,
    UserCardComponent
  ],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss'
})
export class UserListComponent implements OnInit {

  private router = inject(Router);
  protected userService = inject(UserService);

  users: User[] = [];

  comeBack(): void {
    this.router.navigate(['/profile']);
  }

  ngOnInit(): void {
    this.loadUsers();
  }

  private loadUsers(): void {
    this.userService.listUsers().pipe(
      map(page => page.content)
    ).subscribe(users => {
      this.users = users
    });
  }

}
