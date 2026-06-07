import { Injectable } from '@angular/core';
import { CustomerService } from './customer.service';
import Keycloak from 'keycloak-js';
import { Customer, CustomerType } from '../models';
import { jwtDecode } from 'jwt-decode';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

interface KeycloakJwtPayload {
  sub: string;
  realm_access?: { roles?: string[] };
  completeName: string;
  phoneNumber: string;
  email: string;
  documentNumber: string;
}

@Injectable({
  providedIn: 'root',
})
export class KeycloakService {
  private keycloakInstance;

  private readonly keycloakUrl = 'http://localhost:8081';
  private realm = 'GamerHeaven';
  private clientId = 'GamerHeaven-app';

  constructor(
    private customerService: CustomerService,
    private http: HttpClient
  ) {
    this.keycloakInstance = new Keycloak({
      url: this.keycloakUrl,
      realm: this.realm,
      clientId: this.clientId,
    });
  }

  async init(): Promise<any> {
    try {
      const authenticated = await this.keycloakInstance.init({
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri:
          window.location.origin + '/silent-check-sso.html',
        checkLoginIframe: false,
        pkceMethod: 'S256',
        flow: 'standard',
      });
      if (authenticated) {
        const token = this.keycloakInstance.token;
        if (token) {
          console.log('Token:', token);
          const decoded: any = jwtDecode(token);
          const keycloakId = decoded.sub;
          const roles = decoded.resource_access?.['GamerHeaven-app']?.roles || [];
          const name = decoded.completeName;
          const phone = decoded.phoneNumber;
          const documentNumber = decoded.documentNumber;
          const email = decoded.email;

          if (!roles.includes('Admin')) {
            this.findOrCreateCustomer(
              keycloakId,
              name,
              email,
              phone,
              documentNumber
            );
          }
        }

        if (authenticated) {
          console.log('Usuário autenticado com sucesso');
        }
      }
    } catch (err) {
      console.log('Erro ao inicializar o Keycloak', err);
    }
  }

  private findOrCreateCustomer(
    keycloakId: string,
    name: string,
    email: string,
    phone: string,
    documentNumber: string
  ): void {
    const sanitizeDocument = (doc: string) => doc.replace(/\D/g, '');

    const isCpf = (doc: string) => /^\d{11}$/.test(doc);
    const isCnpj = (doc: string) => /^\d{14}$/.test(doc);

    const cleanedDocument = sanitizeDocument(documentNumber);

    const customerType = isCpf(cleanedDocument)
      ? CustomerType.NATURAL_PERSON
      : isCnpj(cleanedDocument)
      ? CustomerType.LEGAL_PERSON
      : null;

    if (!customerType) {
      console.error('Documento inválido. Não é CPF nem CNPJ:', documentNumber);
      return;
    }

    const createNewCustomer = () => {
      const newCustomer: Customer = {
        id: 0,
        name,
        email,
        phone,
        documentNumber: cleanedDocument,
        creditScore: '',
        isDeleted: false,
        customerType,
        addresses: [],
        library: [],
        keycloakId,
      };
      this.customerService.insert(newCustomer).subscribe({
        next: (response: Customer) => {
          localStorage.setItem('customerId', JSON.stringify(response.id));
          console.log('Novo customer criado, ID salvo:', response.id);
        },
        error: (error) => console.error('Erro ao criar customer:', error),
      });
    };

    const storedCustomerId = localStorage.getItem('customerId');
    if (storedCustomerId) {
      // Se há um customerId salvo, busca o customer por esse ID
      this.customerService.findById(Number(storedCustomerId)).subscribe({
        next: (customer: Customer) => {
          if (customer.keycloakId === keycloakId) {
            // Se o customer salvo corresponde, apenas garante o ID no localStorage
            localStorage.setItem('customerId', JSON.stringify(customer.id));
            console.log('Customer existente confirmado, ID:', customer.id);
          } else {
            // Se o customer salvo não corresponde, busca pelo keycloakId
            this.customerService.findByKeycloakId(keycloakId).subscribe({
              next: (existingCustomer: Customer | null) => {
                if (existingCustomer) {
                  localStorage.setItem(
                    'customerId',
                    JSON.stringify(existingCustomer.id)
                  );
                  console.log(
                    'Customer encontrado pelo keycloakId, ID salvo:',
                    existingCustomer.id
                  );
                } else {
                  createNewCustomer();
                }
              },
              error: () => createNewCustomer(),
            });
          }
        },
        error: () => {
          // Se houver erro ao buscar pelo ID salvo, busca pelo keycloakId
          this.customerService.findByKeycloakId(keycloakId).subscribe({
            next: (existingCustomer: Customer | null) => {
              if (existingCustomer) {
                localStorage.setItem(
                  'customerId',
                  JSON.stringify(existingCustomer.id)
                );
                console.log(
                  'Customer encontrado pelo keycloakId após erro, ID salvo:',
                  existingCustomer.id
                );
              } else {
                createNewCustomer();
              }
            },
            error: () => createNewCustomer(),
          });
        },
      });
    } else {
      // Se não há customerId salvo, busca pelo keycloakId
      this.customerService.findByKeycloakId(keycloakId).subscribe({
        next: (existingCustomer: Customer | null) => {
          if (existingCustomer) {
            localStorage.setItem(
              'customerId',
              JSON.stringify(existingCustomer.id)
            );
            console.log(
              'Customer encontrado pelo keycloakId, ID salvo:',
              existingCustomer.id
            );
          } else {
            createNewCustomer();
          }
        },
        error: () => createNewCustomer(),
      });
    }
  }

  getKeycloak(): Keycloak {
    return this.keycloakInstance;
  }

  login(redirectUri: string): void {
    this.keycloakInstance.login({
      redirectUri: redirectUri,
    });
  }

  logout(redirectUri: string): void {
    localStorage.removeItem('customerId');
    this.keycloakInstance.logout({
      redirectUri: redirectUri,
    });
  }

  getToken(): string | null {
    return this.keycloakInstance?.token ?? null;
  }

  isAuthenticated(): boolean {
    return this.keycloakInstance.authenticated ?? false;
  }

  getCustomerId(): number {
    return Number(localStorage.getItem('customerId'));
  }

  deleteUser(userId: string) {
    const url = `${this.keycloakUrl}/admin/realms/GamerHeaven/users/${userId}`;
    return this.http.delete<void>(url);
  }
}
