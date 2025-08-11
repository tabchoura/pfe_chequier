import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService, RegisterPayload, RegisterResponse } from '../../core/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.html',
  styleUrls: ['./register.css'],
})
export class RegisterComponent {
  form!: FormGroup;
  loading = false;
  serverError: string | null = null;
  hide = true; // toggle mot de passe
  year = new Date().getFullYear();

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private auth: AuthService
  ) {
    this.form = this.fb.group({
      role: ['CLIENT', Validators.required],
      nom: ['', [Validators.required, Validators.minLength(2)]],
      prenom: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      // Remarque : tu as commenté cgu dans le template, donc je l'enlève ici aussi
      // cgu: [false, Validators.requiredTrue],
    });
  }

  // Getters utilisés dans le template
  get role() { return this.form.get('role'); }
  get nom() { return this.form.get('nom'); }
  get prenom() { return this.form.get('prenom'); }
  get email() { return this.form.get('email'); }
  get password() { return this.form.get('password'); }

  submit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) {
      console.log('❌ Formulaire invalide');
      return;
    }

    this.loading = true;
    this.serverError = null;

    const payload: RegisterPayload = {
      role: this.role?.value,
      nom: this.nom?.value,
      prenom: this.prenom?.value,
      email: this.email?.value,
      password: this.password?.value,
    };

    console.log('➡️ POST /auth/register payload:', payload);

    this.auth.register(payload).subscribe({
      next: (res: RegisterResponse) => {
        console.log('✅ Register OK:', res);
        // Redirection vers login après inscription réussie
        this.router.navigateByUrl('/login');
      },
      error: (err: any) => {
        console.error('❌ Register ERROR:', err);
        // Gestion des erreurs plus robuste
        if (err.error && err.error.message) {
          this.serverError = err.error.message;
        } else if (err.message) {
          this.serverError = err.message;
        } else {
          this.serverError = 'Erreur lors de l\'inscription. Veuillez réessayer.';
        }
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }
}