import type { User } from './user.model';
import type { NutritionInfo } from './nutrition-info.model';

export interface Food {
  id: number;
  name: string;
  ni: NutritionInfo;
  createdBy: User;
  imageData?: string;
  imageType?: string;
}
