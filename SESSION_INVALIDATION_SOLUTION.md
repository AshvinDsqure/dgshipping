# Session Invalidation Handling - Complete Solution

## Backend Changes (Completed)

### 1. SessionInvalidatedException.java
- Added `@ResponseStatus(HttpStatus.UNAUTHORIZED)` annotation
- Returns HTTP 401 with proper message

### 2. DSpaceApiExceptionControllerAdvice.java
- Changed status code from SC_NOT_ACCEPTABLE (406) to SC_UNAUTHORIZED (401)
- Updated message to: "Your session has expired because your account was logged in from another device."

---

## Angular Frontend Changes

### 1. AuthInterceptor (auth.interceptor.ts)

```typescript
import { Injectable } from '@angular/core';
import { 
  HttpRequest, 
  HttpHandler, 
  HttpEvent, 
  HttpInterceptor, 
  HttpResponse,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError, from } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  
  private isRefreshing = false;
  
  constructor(
    private router: Router,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Add authorization header if token exists
    const token = this.authService.getToken();
    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        // Handle 401 Unauthorized - Session Invalidated
        if (error.status === 401) {
          const errorMessage = error.error?.message || error.message;
          
          // Check if it's a session invalidation error
          if (errorMessage.includes('logged in from another device') || 
              errorMessage.includes('session has expired')) {
            this.handleSessionInvalidation();
          } else {
            // Regular 401 - token expired or invalid
            this.handleUnauthorized();
          }
        }
        
        return throwError(() => error);
      })
    );
  }

  private handleSessionInvalidation(): void {
    // Prevent multiple redirects
    if (this.isRefreshing) {
      return;
    }
    
    this.isRefreshing = true;
    
    // Clear authentication data
    this.authService.clearAuthData();
    
    // Show session invalidation message
    this.snackBar.open(
      'Your session has expired because your account was logged in from another device.',
      'OK',
      {
        duration: 5000,
        horizontalPosition: 'center',
        verticalPosition: 'top',
        panelClass: ['session-invalidated-snackbar']
      }
    );
    
    // Navigate to login page
    this.router.navigate(['/login']);
    
    // Reset flag after navigation
    setTimeout(() => {
      this.isRefreshing = false;
    }, 1000);
  }

  private handleUnauthorized(): void {
    // Clear authentication data
    this.authService.clearAuthData();
    
    // Navigate to login page
    this.router.navigate(['/login']);
  }
}
```

### 2. AuthService (auth.service.ts)

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, from } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  
  private tokenKey = 'auth_token';
  private userKey = 'auth_user';
  
  private currentUserSubject = new BehaviorSubject<any>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.loadUserFromStorage();
  }

  // Get token from localStorage
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  // Get current user
  getCurrentUser(): any {
    return this.currentUserSubject.value;
  }

  // Load user from localStorage on app init
  private loadUserFromStorage(): void {
    const user = localStorage.getItem(this.userKey);
    if (user) {
      this.currentUserSubject.next(JSON.parse(user));
    }
  }

  // Login method
  login(credentials: any): Observable<any> {
    return this.http.post('/api/authn/login', credentials).pipe(
      tap(response => {
        const token = response['token'];
        const user = response['user'];
        
        if (token) {
          this.setToken(token);
          this.setUser(user);
        }
      })
    );
  }

  // Set token in localStorage
  private setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  // Set user in localStorage and subject
  private setUser(user: any): void {
    localStorage.setItem(this.userKey, JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  // Clear all authentication data
  clearAuthData(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.currentUserSubject.next(null);
  }

  // Logout method
  logout(): void {
    this.clearAuthData();
    this.router.navigate(['/login']);
  }

  // Check if user is authenticated
  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
```

### 3. Global Error Handler (error-handler.ts)

```typescript
import { ErrorHandler, Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  
  constructor(
    private router: Router,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  handleError(error: any): void {
    // Handle HTTP errors
    if (error instanceof HttpErrorResponse) {
      if (error.status === 401) {
        const errorMessage = error.error?.message || error.message;
        
        // Check if it's session invalidation
        if (errorMessage.includes('logged in from another device')) {
          this.handleSessionInvalidation();
          return;
        }
      }
    }
    
    // Log other errors
    console.error('Global Error Handler:', error);
  }

  private handleSessionInvalidation(): void {
    // Clear auth data
    this.authService.clearAuthData();
    
    // Show message
    this.snackBar.open(
      'Your session has expired because your account was logged in from another device.',
      'OK',
      {
        duration: 5000,
        horizontalPosition: 'center',
        verticalPosition: 'top'
      }
    );
    
    // Navigate to login
    this.router.navigate(['/login']);
  }
}
```

### 4. App Module Configuration (app.module.ts)

```typescript
import { HTTP_INTERCEPTORS, ErrorHandler } from '@angular/common/http';
import { AuthInterceptor } from './auth.interceptor';
import { GlobalErrorHandler } from './error-handler';

@NgModule({
  // ... other imports
  providers: [
    // ... other providers
    
    // HTTP Interceptor
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    
    // Global Error Handler
    {
      provide: ErrorHandler,
      useClass: GlobalErrorHandler
    }
  ]
})
export class AppModule { }
```

### 5. CSS for Snackbar (styles.scss)

```scss
.session-invalidated-snackbar {
  background-color: #f44336 !important;
  color: white !important;
  font-weight: bold;
  
  .mat-simple-snackbar-action {
    color: white !important;
  }
}
```

### 6. Alternative: Dialog Component (session-invalidated-dialog.component.ts)

```typescript
import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-session-invalidated-dialog',
  template: `
    <h2 mat-dialog-title>Session Expired</h2>
    <mat-dialog-content>
      <p>Your session has expired because your account was logged in from another device.</p>
      <p>Please log in again to continue.</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onLogin()">Login</button>
    </mat-dialog-actions>
  `
})
export class SessionInvalidatedDialogComponent {
  
  constructor(
    public dialogRef: MatDialogRef<SessionInvalidatedDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private router: Router,
    private authService: AuthService
  ) {}

  onLogin(): void {
    this.dialogRef.close();
    this.authService.clearAuthData();
    this.router.navigate(['/login']);
  }
}
```

### 7. Updated AuthInterceptor with Dialog

```typescript
import { MatDialog } from '@angular/material/dialog';
import { SessionInvalidatedDialogComponent } from './session-invalidated-dialog.component';

// In AuthInterceptor class:

constructor(
  private router: Router,
  private authService: AuthService,
  private snackBar: MatSnackBar,
  private dialog: MatDialog
) {}

private handleSessionInvalidation(): void {
  if (this.isRefreshing) {
    return;
  }
  
  this.isRefreshing = true;
  
  // Clear authentication data
  this.authService.clearAuthData();
  
  // Open dialog instead of snackbar
  this.dialog.open(SessionInvalidatedDialogComponent, {
    width: '400px',
    disableClose: true,
    hasBackdrop: true
  });
  
  // Reset flag
  setTimeout(() => {
    this.isRefreshing = false;
  }, 1000);
}
```

---

## Testing Checklist

### Backend Testing
- [x] SessionInvalidatedException returns 401 status
- [x] Error message is clear and user-friendly
- [x] ControllerAdvice handles exception correctly

### Frontend Testing
- [ ] Test login from Browser A
- [ ] Test login from Browser B (same user)
- [ ] Verify Browser A receives 401 on next request
- [ ] Verify Angular shows session invalidation message
- [ ] Verify Angular clears auth data
- [ ] Verify Angular redirects to login page
- [ ] Verify no blank page
- [ ] Verify no infinite request loop
- [ ] Verify no console errors

---

## Summary

### Backend Changes:
1. **SessionInvalidatedException**: Added `@ResponseStatus(HttpStatus.UNAUTHORIZED)`
2. **DSpaceApiExceptionControllerAdvice**: Changed status code to 401 with proper message

### Frontend Changes:
1. **AuthInterceptor**: Catches 401 errors, handles session invalidation
2. **AuthService**: Added `clearAuthData()` method
3. **GlobalErrorHandler**: Additional error handling layer
4. **App Module**: Registered interceptor and error handler
5. **Snackbar/Dialog**: User-friendly session invalidation message

The solution ensures:
- Single-session login behavior is maintained
- Proper HTTP 401 response for session invalidation
- Angular automatically detects and handles 401 responses
- User sees clear message about session invalidation
- Automatic logout and redirect to login page
- No blank pages or infinite loops
