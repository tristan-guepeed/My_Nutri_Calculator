import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import type { DiaryEntry } from '../models/diary.model';
import { environment } from '../../environnements/environnement';

@Injectable({
  providedIn: 'root',
})
export class DiaryService {
  private apiUrl = environment.apiBaseUrl + '/diary';

  constructor(private http: HttpClient) {}

  createEntry(entry: DiaryEntry): Observable<DiaryEntry> {
    return this.http.post<DiaryEntry>(`${this.apiUrl}/create`, entry);
  }

  updateEntry(id: number, entry: DiaryEntry): Observable<DiaryEntry> {
    return this.http.put<DiaryEntry>(`${this.apiUrl}/update/${id}`, entry);
  }

  deleteEntry(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
  }

  getAllEntries(userId: string): Observable<DiaryEntry[]> {
    return this.http.get<DiaryEntry[]>(`${this.apiUrl}/all/${userId}`);
  }

  getEntryById(id: number): Observable<DiaryEntry> {
    return this.http.get<DiaryEntry>(`${this.apiUrl}/${id}`);
  }

  getEntriesByDate(date: string): Observable<DiaryEntry[]> {
    return this.http.get<DiaryEntry[]>(`${this.apiUrl}/by-date?date=${date}`);
  }
}
