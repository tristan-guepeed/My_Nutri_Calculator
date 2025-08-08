import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import type { Meal } from '../models/meal.model';

@Injectable({
  providedIn: 'root',
})
export class MealService {
  private apiUrl = 'http://localhost:8080/api/meals';

  constructor(private http: HttpClient) {}

  getVisibleMeals(userId: string): Observable<Meal[]> {
    return this.http.get<Meal[]>(`${this.apiUrl}/visible/${userId}`);
  }

  createMeal(meal: Meal): Observable<Meal> {
    return this.http.post<Meal>(`${this.apiUrl}/create`, meal);
  }

  updateMeal(id: number, meal: Meal): Observable<Meal> {
    return this.http.put<Meal>(`${this.apiUrl}/update/${id}`, meal);
  }

  deleteMeal(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
  }

  uploadImage(id: number, image: File): Observable<void> {
    const formData = new FormData();
    formData.append('image', image);
    return this.http.post<void>(`${this.apiUrl}/${id}/image`, formData);
  }

  deleteImage(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}/image`);
  }
}
