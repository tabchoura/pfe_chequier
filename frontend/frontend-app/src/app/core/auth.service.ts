import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

// Interfaces pour le typage
export interface RegisterPayload {
  role: string;
  nom: string;
  prenom: string;
  email: string;
  password: string;
}

export interface RegisterResponse {
  status: number;
  message: string;
  data?: any;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export interface LoginResponse {
  status: number;
  token?: string;
  user?: any;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl; // Assure-toi que cette URL est correcte

  constructor(private http: HttpClient) {}

  /**
   * Inscription d'un nouvel utilisateur
   */
  register(payload: RegisterPayload): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${this.apiUrl}/auth/register`, payload);
  }

  /**
   * Connexion d'un utilisateur
   */
  login(payload: LoginPayload): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, payload);
  }

  /**
   * Déconnexion (optionnel)
   */
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  /**
   * Vérifier si l'utilisateur est connecté
   */
  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  /**
   * Récupérer le token
   */
  getToken(): string | null {
    return localStorage.getItem('token');
  }
}