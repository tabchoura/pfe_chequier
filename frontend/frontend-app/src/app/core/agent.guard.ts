import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const agentGuard: CanActivateFn = () => {
  const router = inject(Router);
  const token = localStorage.getItem('auth_token');
  const role  = localStorage.getItem('auth_role');
  if (token && role === 'AGENT') return true;
  router.navigateByUrl('/login');
  return false;
};
