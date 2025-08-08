import type { Meal } from './meal.model';
import type { Food } from './food.model';

export interface MealItem {
  id: number;
  meal?: Meal;
  food: Food;
  quantity: number;
}
