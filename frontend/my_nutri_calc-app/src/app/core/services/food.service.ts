import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import type { Food } from '../models/food.model';
import { environment } from '../../environnements/environnement';
import { map } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class FoodService {
  private apiUrl = environment.apiBaseUrl + '/foods';

  constructor(private http: HttpClient) {}

  getVisibleFoods(userId: string): Observable<Food[]> {
    return this.http.get<Food[]>(`${this.apiUrl}/visible/${userId}`);
  }

  createFood(food: Food): Observable<Food> {
    return this.http.post<Food>(`${this.apiUrl}/create`, food);
  }

  updateFood(id: number, food: Food): Observable<Food> {
    return this.http.put<Food>(`${this.apiUrl}/update/${id}`, food);
  }

  deleteFood(id: number): Observable<void> {
    return this.http.delete(`${this.apiUrl}/delete/${id}`, { responseType: 'text' }).pipe(
      map(() => undefined)
    );
  }


  uploadImage(id: number, image: File): Observable<void> {
    const formData = new FormData();
    formData.append('image', image);

    return this.http.post(`${this.apiUrl}/${id}/image`, formData, { responseType: 'text' }).pipe(
      map(() => {})
    );
  }

  deleteImage(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}/image`);
  }
}
