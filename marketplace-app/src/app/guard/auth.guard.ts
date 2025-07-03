import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { KeycloakService } from '../service/keycloak.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  
  constructor(private keycloakService: KeycloakService, private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    return this.keycloakService.init().then(() => {
      // Agora, podemos verificar se o Keycloak foi inicializado e se o usuário está autenticado
      if (this.keycloakService.isAuthenticated()) {
        return true;
      }

      // Se não estiver autenticado, redireciona para o login
      const redirectUri = window.location.href;
      this.keycloakService.login(redirectUri);
      return false;
    });
  }
}
