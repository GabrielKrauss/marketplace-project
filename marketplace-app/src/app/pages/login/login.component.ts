import { Component, OnInit } from '@angular/core';
import { KeycloakService } from '../../service/keycloak.service';

@Component({
  selector: 'app-login',
  imports: [],
  styleUrl: './login.component.css',
  templateUrl: './login.component.html'
  
})

export class LoginComponent implements OnInit {

  constructor(private keycloakService: KeycloakService){};

  async ngOnInit(): Promise<void> {
    const redirectUri = window.location.href;
    await this.keycloakService.init()
    await this.keycloakService.login(redirectUri);
  }
}
