import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private router: Router) {}

  logout(): void {
    // 1️⃣ Remove JWT
    localStorage.removeItem('token');
    localStorage.removeItem('user'); // optional if stored

    // 2️⃣ Redirect to login
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }
}
