import { Component, OnInit } from '@angular/core';
import { KeycloakService } from '../../service/keycloak.service';

@Component({
  selector: 'app-access-denied',
  imports: [],
  styleUrl: './accessDenied.component.css',
  templateUrl: './accessDenied.component.html'
  
})

export class AccessDeniedComponent implements OnInit {

  async ngOnInit(): Promise<void> {
  }
}
