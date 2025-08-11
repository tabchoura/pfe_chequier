import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
})
export class LoginComponent {
  form!: FormGroup;
  loading = false;
  serverError: string | null = null;
  showPass = false;
  submitted = false;
  year = new Date().getFullYear();

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      remember: [false],
    });
  }

  get emailCtrl() { return this.form.get('email'); }
  get passCtrl()  { return this.form.get('password'); }
  onSubmit() { this.submit(); }
  get emailError(): string | null {
    if (!this.submitted) return null;
    if (this.emailCtrl?.hasError('required')) return 'Email requis.';
    if (this.emailCtrl?.hasError('email'))    return 'Email invalide.';
    return null;
  }

  submit() {
    this.submitted = true;
    if (this.form.invalid) return;

    this.loading = true;
    this.serverError = null;

    this.auth.login(this.form.value).subscribe({
      next: (res: any) => {
        const role = res?.role ?? res?.user?.role ?? localStorage.getItem('auth_role');
        if (role === 'CLIENT') {
          this.router.navigateByUrl('/dashboardclient/profile');
        } else if (role === 'AGENT') {
          this.router.navigateByUrl('/dashboardagent');
        } else {
          this.router.navigateByUrl('/');
        }
      },
      error: (err: any) => {
        this.loading = false;
        this.serverError = String(err || 'Identifiants invalides.');
      },
      complete: () => this.loading = false
    });
  }
}
