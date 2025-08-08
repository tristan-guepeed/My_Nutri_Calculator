import type { User } from './user.model';
import type { MealItem } from './meal-item.model';
import type { NutritionInfo } from './nutrition-info.model';

export interface Meal {
  id: number;
  name: string;
  mealItems: MealItem[];
  createdBy: User;
  totalNutrition: NutritionInfo;
  imageData?: string;
  imageType?: string;
}
