import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Coupon } from '../models';

@Injectable({
  providedIn: 'root',
})
export class CouponService {
private apiUrl = 'http://localhost:8080/coupons';

  constructor(private http: HttpClient) {}

  findAll(): Observable<string[]> {
    return this.http.get<string[]>(this.apiUrl);
  }

  findAllActiveCoupons(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/activeCoupons`);
  }

  findById(couponId: number): Observable<Coupon>{
    return this.http.get<Coupon>(`${this.apiUrl}/id/${couponId}`)
  }

  findByCode(couponCode: string): Observable<Coupon>{
    return this.http.get<Coupon>(`${this.apiUrl}/code/${couponCode}`)
  }
  
  insert(coupon: Coupon): Observable<string> {
    return this.http.post<string>(this.apiUrl, coupon);
  }

  delete(couponId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${couponId}`);
  }

  update(couponId: number, updatedProduct: Partial<Coupon>): Observable<Coupon> {
    return this.http.patch<Coupon>(`${this.apiUrl}/${couponId}`, updatedProduct);
  }
}