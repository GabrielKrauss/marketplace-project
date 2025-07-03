import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Category } from '../models';
  

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  private apiUrl = 'http://localhost:8080/categories';

  constructor(private http: HttpClient) {}

  findAll(): Observable<string[]> {
    return this.http.get<string[]>(this.apiUrl);
  }
  
  findById(categoryId: number): Observable<void> {
    return this.http.get<void>(`${this.apiUrl}/${categoryId}`);
  }

  insert(categoryName: Category): Observable<string> {
    return this.http.post<string>(this.apiUrl, categoryName);
  }

  delete(categoryId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${categoryId}`);
  }

  update(categoryId: number, updatedCategory: Partial<Category>): Observable<Category> {
      return this.http.patch<Category>(`${this.apiUrl}/${categoryId}`, updatedCategory);
    }

}
