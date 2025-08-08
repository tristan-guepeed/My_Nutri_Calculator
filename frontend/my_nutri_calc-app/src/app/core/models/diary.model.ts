import { Meal } from './meal.model';
import { NutritionInfo } from './nutrition-info.model';
import { User } from './user.model';

export interface DiaryEntry {
  id?: number;
  date: string;
  user: User;
  meals: Meal[];
  totalNutrition: NutritionInfo;
}
