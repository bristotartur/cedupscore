import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { HeaderComponent } from "./core/components/header/header.component";
import { filter } from 'rxjs';
import { NgClass } from '@angular/common';
import { UserService } from './user/services/user.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    NgClass,
    HeaderComponent,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {

  private userService = inject(UserService);
  private router = inject(Router);
  private changeDetector = inject(ChangeDetectorRef);

  showHeader: boolean = true;
  noHeaderRoutes: string[] = ['/login', '/not-found'];

  ngOnInit(): void {
    this.handleNavigationEvents();
    this.registerPopStateListener();
    this.loginOnInit();
  }

  private handleNavigationEvents(): void {
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.showHeader = !this.noHeaderRoutes.includes(event.urlAfterRedirects || event.url);
        this.changeDetector.detectChanges();
      });
  }

  private registerPopStateListener(): void {
    window.addEventListener('popstate', () => {
      if (this.router.url === '/login') {
        this.router.navigate(['/']);
      }
    });
  }

  private loginOnInit() {
    const token = localStorage.getItem('accessToken');

    if (!token) return;

    const tokenPayload = JSON.parse(atob(token.split('.')[1]));
    const expirationDate = new Date(tokenPayload.exp * 1000);
    const currentDate = new Date();
    
    if (currentDate > expirationDate) {
      localStorage.removeItem('accessToken');
    } else {
      const userId = tokenPayload.sub;
      this.userService.findUser(userId)
        .subscribe((user) => {
          this.userService.currentUserSignal.set(user);
        });
    }
  }

}
