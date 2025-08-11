import { Routes } from '@angular/router';
import { AccueilComponent } from './pages/accueil/accueil';
import { RegisterComponent } from './pages/register/register';
import { LoginComponent } from './pages/login/login';
import { DashboardClientComponent } from './pages/dashboard-client/dashboard-client';
import { clientGuard } from './core/client.guard';
import { DashboardAgentComponent } from './pages/dashboard-agent/dashboard-agent';
import { agentGuard } from './core/agent.guard';

export const routes: Routes = [
  { path: '', component: AccueilComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  {
    path: 'dashboardclient',
    component: DashboardClientComponent,
    canActivate: [clientGuard],
    children: [
      { path: '', redirectTo: 'profile', pathMatch: 'full' },
      { path: 'profile', loadComponent: () => import('./pages/dashboard-client/profile/profile').then(m => m.ProfileComponent) },
      { path: 'demandes', loadComponent: () => import('./pages/dashboard-client/demandes/demande').then(m => m.DemandesComponent) },
      { path: 'historiques', loadComponent: () => import('./pages/dashboard-client/historiques/historique').then(m => m.HistoriquesComponent) },
    ]
  },

  { path: 'dashboardagent', component: DashboardAgentComponent, canActivate: [agentGuard] },

  { path: '**', redirectTo: '' }
];
