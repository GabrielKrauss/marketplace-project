import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Product } from '../models';
import { PaginatedResponse } from '../models';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private apiUrl = 'http://localhost:8080/products';

  constructor(private http: HttpClient) {}

  findAllActive(
    page: number,
    size: number,
    searchTerm: string,
    categoryIds: number[],
    
  ): Observable<PaginatedResponse<Product>> {
    let params = new HttpParams();
    params = params.set('size', size);
    if (page !== undefined && page !== null) {
      params = params.set('page', page.toString());
    }
    if (searchTerm && searchTerm.trim() !== '') {
      params = params.set('searchTerm', searchTerm);
    }
    if (categoryIds && categoryIds.length > 0) {
      // Para cada ID, adicionamos o mesmo parâmetro "categoryIds"
      categoryIds.forEach((id) => {
        params = params.append('categoryIds', id.toString());
      });
    }
    params = params.append('sort', 'id,asc');
    return this.http.get<PaginatedResponse<Product>>(
      `${this.apiUrl}/activeProducts`,
      { params }
    );
  }

  findAll(
    page: number = 0,
    searchTerm: string = '',
    category: string = ''
  ): Observable<PaginatedResponse<Product>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', '12')
      .set('searchTerm', searchTerm)
      .set('category', category)
      .set('sort', 'id,asc');

    return this.http.get<PaginatedResponse<Product>>(this.apiUrl, { params });
  }

  findById(productId: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${productId}`);
  }

  insert(product: Product): Observable<string> {
    return this.http.post<string>(this.apiUrl, product);
  }

  delete(productId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${productId}`);
  }

  update(
    productId: number,
    updatedProduct: Partial<Product>
  ): Observable<Product> {
    return this.http.patch<Product>(
      `${this.apiUrl}/${productId}`,
      updatedProduct
    );
  }
}
