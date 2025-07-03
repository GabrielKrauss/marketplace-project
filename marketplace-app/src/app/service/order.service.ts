import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Category, Order, OrderRequest } from '../models';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private apiUrl = 'http://localhost:8080/orders';

  constructor(private http: HttpClient) {}

  findAllActive(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/activeOrders`);
  }

  findAll(): Observable<string[]> {
    return this.http.get<string[]>(this.apiUrl);
  }
  
  findById(orderId: number): Observable<void> {
    return this.http.get<void>(`${this.apiUrl}/${orderId}`);
  }

  findAllActiveByCustomerId(customerId: number): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/customer/${customerId}`);
  }

  insert(order: OrderRequest): Observable<string> {
    return this.http.post<string>(this.apiUrl, order);
  }

  delete(orderId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${orderId}`);
  }

  update( orderId: number, updatedOrder: Partial<Order>): Observable<Order> {
    return this.http.patch<Order>(`${this.apiUrl}/${orderId}`, updatedOrder);
  }

  operatorUpdate( orderId: number, updatedOrder: Partial<Order>): Observable<Order> {
    return this.http.patch<Order>(`${this.apiUrl}/operatorUpdate/${orderId}`, updatedOrder);
  }
}
