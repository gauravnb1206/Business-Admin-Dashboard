import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone:true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {

  username = '';
  password = '';
  error = '';

  constructor(
    private http: HttpClient,
    private router:Router
  ){}

  login(){
    this.http.post<any>('http://localhost:8080/auth/login',{
      username:this.username,
      password:this.password   
    }).subscribe({
      next: (res) => {
        localStorage.setItem('AUTH_TOKEN', res.token);
        this.router.navigate(['/customers'])
      },
      error: () => {
        this.error = 'Invalid username and password'
      }
    });
  }

}
