import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogContent, MatDialogActions, MatDialogModule } from '@angular/material/dialog';
import { FoodService } from '../../core/services/food.service';
import { Food } from '../../core/models/food.model';
import { User } from '../../core/models/user.model';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-add-food-dialog',
  templateUrl: './add-food-dialog.html',
  styleUrls: ['./add-food-dialog.scss'],
  imports: [
    ReactiveFormsModule,
    FormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
})
export class AddFoodDialogComponent {
  foodForm: FormGroup;
  isEditing: boolean = false;
  selectedFile: File | null = null;

  constructor(
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    private foodService: FoodService,
    private dialogRef: MatDialogRef<AddFoodDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { food?: Food }
  ) {
    this.isEditing = !!data?.food;

    this.foodForm = this.fb.group({
      name: [data?.food?.name || '', Validators.required],
      proteins: [data?.food?.ni?.proteins || 0, Validators.min(0)],
      carbs: [data?.food?.ni?.carbs || 0, Validators.min(0)],
      fats: [data?.food?.ni?.fats || 0, Validators.min(0)],
      calories: [data?.food?.ni?.calories || 0, Validators.min(0)],
    });
  }

  onFileSelected(event: any) {
    if (event.target.files && event.target.files.length > 0) {
      this.selectedFile = event.target.files[0];
    }
  }

  save() {
      if (this.foodForm.invalid) return;

      const food: Partial<Food> = {
        name: this.foodForm.value.name,
        ni: {
          proteins: this.foodForm.value.proteins,
          carbs: this.foodForm.value.carbs,
          fats: this.foodForm.value.fats,
          calories: this.foodForm.value.calories,
        },
      };

      if (this.isEditing) {
        // UPDATE
        this.foodService.updateFood(this.data.food!.id, food as Food).subscribe((updated: Food) => {
          const finish = () => {
            this.snackBar.open('Aliment modifié avec succès ✅', 'Fermer', { duration: 3000 });
            this.dialogRef.close('refresh');
          };

          if (this.selectedFile) {
            this.foodService.uploadImage(updated.id, this.selectedFile).subscribe(() => finish());
          } else {
            finish();
          }
        });
      } else {
        // CREATE
        this.foodService.createFood(food as Food).subscribe((created: Food) => {
          const finish = () => {
            this.snackBar.open('Aliment ajouté avec succès ✅', 'Fermer', { duration: 3000 });
            this.dialogRef.close('refresh');
          };

          if (this.selectedFile) {
            this.foodService.uploadImage(created.id, this.selectedFile).subscribe(() => finish());
          } else {
            finish();
          }
        });
      }
    }

  cancel() {
    this.dialogRef.close();
  }
}
