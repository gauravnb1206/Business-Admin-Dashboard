import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {

    // 🚫 VERY IMPORTANT: do NOT attach token to login API
    if (req.url.includes('/auth/login')) {
      return next.handle(req);
    }

    const token = localStorage.getItem('AUTH_TOKEN');
    console.log('➡️ Intercepting:', req.url);
    console.log('➡️ Token in storage:', token);

    if (token) {
      const authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
            console.log('➡️ Authorization header set');

      return next.handle(authReq);
    }
        console.log('❌ No token attached');

    return next.handle(req);

  }
}
