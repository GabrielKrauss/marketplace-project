import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Address } from '../models';

@Injectable({
  providedIn: 'root',
})
export class AddressService {
  private apiUrl = 'http://localhost:8080/addresses';
  private deliveryAddressId: number = 0;
  
  constructor(private http: HttpClient) {}

  findAll(): Observable<string[]> {
    return this.http.get<string[]>(this.apiUrl);
  }

  findById(addressId: number): Observable<Address>{
    return this.http.get<Address>(`${this.apiUrl}/${addressId}`)
  }

  insert(address: Address, customerId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}?customerId=${customerId}`, address);
  }

  delete(addressId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${addressId}`);
  }

  update(addressId: number, customerId: number, updatedAddress: Partial<Address>): Observable<Address> {
    return this.http.patch<Address>(`${this.apiUrl}/${addressId}?customerId=${customerId}`, updatedAddress);
  }

  getDeliveryAddressId(): number {
    return this.deliveryAddressId;
  }

  setDeliveryAddressId(id: number): void {
    this.deliveryAddressId = id;
  }

}
