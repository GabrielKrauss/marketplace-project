import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { KeycloakService } from '../service/keycloak.service'; // Ajuste o caminho do serviço conforme sua estrutura

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  constructor(private keycloakService: KeycloakService, private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {

    // Verifica se o usuário está autenticado
    if (this.keycloakService.isAuthenticated()) {

      // Obtém a instância do Keycloak e verifica se o usuário tem a role de 'admin'
      const keycloakInstance = this.keycloakService.getKeycloak();
      const hasRoleAdmin = keycloakInstance.realmAccess?.roles.includes('Admin');
      
      if (hasRoleAdmin) {
        return true; // Permite o acesso à rota
      } else {
        // Se não tiver a role de admin, redireciona para uma página de "Acesso Negado"
        if (state.url !== '/access-denied') {  // Evita redirecionamento repetido para a mesma página
          console.log('Não tem permissão para acessar esta página');
          this.router.navigate(['/access-denied']);
        }
        return false;
      }

    } else {
      // Se o usuário não estiver autenticado, redireciona para a página de login
      if (state.url !== '/login') {  // Evita redirecionamento repetido para a mesma página
        console.log('Usuário não autenticado');
        this.router.navigate(['/login']);
      }
      return false;
    }
  }
}
