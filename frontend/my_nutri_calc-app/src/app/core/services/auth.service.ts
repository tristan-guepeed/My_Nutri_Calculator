import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { UserService } from './user.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private tokenKey = 'auth_token';
  private _isLoggedIn$ = new BehaviorSubject<boolean>(!!localStorage.getItem(this.tokenKey));

  isLoggedIn$ = this._isLoggedIn$.asObservable();

  constructor(private userService: UserService) {}

  login(username: string, password: string): Observable<{ token: string }> {
    return this.userService.login({ username, password }).pipe(
      tap(response => {
        localStorage.setItem(this.tokenKey, response.token);
        this._isLoggedIn$.next(true);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    this._isLoggedIn$.next(false);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }
}
