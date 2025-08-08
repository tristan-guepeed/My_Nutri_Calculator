import { Routes } from '@angular/router';
import { FoodListComponent } from './foods/food-list/food-list.component';
import { MealListComponent } from './meals/meal-list/meal-list.component';
import { DiaryListComponent } from './diary/diary-list/diary-list.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AuthGuard } from './core/guards/auth.guard';
import { ProfileComponent } from './users/profile/profile.component';

export const routes: Routes = [
    { path: 'foods', component: FoodListComponent, canActivate: [AuthGuard] },
    { path: 'meals', component: MealListComponent, canActivate: [AuthGuard] },
    { path: 'diary', component: DiaryListComponent, canActivate: [AuthGuard] },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
    { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
    { path: '**', redirectTo: 'dashboard' }
];
