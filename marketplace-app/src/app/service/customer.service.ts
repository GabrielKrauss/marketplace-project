import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Customer } from '../models';

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  private apiUrl = 'http://localhost:8080/customers';

  private customerId: number = 0;

  constructor(private http: HttpClient) {}

  findAllCustomers(): Observable<string[]> {
    return this.http.get<string[]>(this.apiUrl);
  }
  
  findAllActiveCustomers(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/activeCustomers`);
  }

  findById(customerId: number): Observable<Customer>{
    return this.http.get<Customer>(`${this.apiUrl}/${customerId}`)
  }

  findByKeycloakId(keycloakId: string): Observable<Customer | null> {
    return this.http.get<Customer | null>(`${this.apiUrl}/keycloak/${keycloakId}`);
  }

  insert(customer: Customer): Observable<Customer> {
    return this.http.post<Customer>(this.apiUrl, customer);
  }

  delete(customerId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${customerId}`);
  }

  update(customerId: number, updatedCustomer: Partial<Customer>): Observable<Customer> {
    return this.http.patch<Customer>(`${this.apiUrl}/${customerId}`, updatedCustomer);
  }

  getCustomerId(): number {
    return this.customerId;
  }

  setCustomerId(id: number): void {
    this.customerId = id;
  }
  

}
