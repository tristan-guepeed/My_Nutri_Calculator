import { Component, OnInit } from '@angular/core';
import { AuthService } from '../core/services/auth.service';
import { UserService } from '../core/services/user.service';
import { DiaryService } from '../core/services/diary.service';
import { Router } from '@angular/router';
import { User } from '../core/models/user.model';
import { DiaryEntry } from '../core/models/diary.model';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss'],
  standalone: true,
  imports: [DecimalPipe, CommonModule, MatIconModule, MatMenuModule, MatButtonModule]
})
export class DashboardComponent implements OnInit {
  user?: User;
  todayEntries: DiaryEntry[] = [];
  totalMacros = { protein: 0, carbs: 0, fat: 0, calories: 0 };
  macrosPercent = { protein: 0, carbs: 0, fat: 0 };

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private diaryService: DiaryService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUser();
    this.loadTodayEntries();
  }

  loadUser(): void {
    this.userService.getCurrentUser().subscribe(u => {
      this.user = u;
    });
  }

  loadTodayEntries(): void {
    const today = new Date().toISOString().split('T')[0];
    this.diaryService.getEntriesByDate(today).subscribe(entries => {
      this.todayEntries = entries;
      this.calculateMacros();
    });
  }

  calculateMacros(): void {
    this.totalMacros = { protein: 0, carbs: 0, fat: 0, calories: 0 };
    this.todayEntries.forEach(entry => {
      entry.meals?.forEach(meal => {
        this.totalMacros.protein += meal.totalNutrition.proteins || 0;
        this.totalMacros.carbs += meal.totalNutrition.carbs || 0;
        this.totalMacros.fat += meal.totalNutrition.fats || 0;
        this.totalMacros.calories += meal.totalNutrition.calories || 0;
      });
    });

    const totalMacroGrams = this.totalMacros.protein + this.totalMacros.carbs + this.totalMacros.fat;
    if (totalMacroGrams > 0) {
      this.macrosPercent.protein = Math.round((this.totalMacros.protein / totalMacroGrams) * 100);
      this.macrosPercent.carbs = Math.round((this.totalMacros.carbs / totalMacroGrams) * 100);
      this.macrosPercent.fat = 100 - this.macrosPercent.protein - this.macrosPercent.carbs; // pour garder total à 100%
    } else {
      this.macrosPercent = { protein: 0, carbs: 0, fat: 0 };
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  goToProfile(): void {
    this.router.navigate(['/profile']);
  }
}

