import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FoodService } from '../../core/services/food.service';
import { UserService } from '../../core/services/user.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { User } from '../../core/models/user.model';
import { Food } from '../../core/models/food.model';
import { AuthService } from '../../core/services/auth.service';
import { AddFoodDialogComponent } from '../food-form/add-food-dialog.component';

@Component({
  selector: 'app-food-list',
  templateUrl: './food-list.html',
  styleUrls: ['./food-list.scss'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatMenuModule,
    MatSnackBarModule,
    MatDialogModule
  ]
})
export class FoodListComponent implements OnInit {
  user?: User;
  foods: Food[] = [];
  filteredFoods: Food[] = [];
  searchQuery: string = '';

  constructor(
    private foodService: FoodService,
    private userService: UserService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    public router: Router
  ) {}

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe(u => {
      this.user = u;
      this.loadFoods();
    });
  }

  loadFoods(): void {
    if (!this.user) return;
    this.foodService.getVisibleFoods(this.user.id!).subscribe(f => {
      this.foods = f;
      this.filteredFoods = f;
    });
  }

  applyFilter(): void {
    const query = this.searchQuery.toLowerCase();
    this.filteredFoods = this.foods.filter(food =>
      food.name.toLowerCase().includes(query)
    );
  }

  openAddDialog(): void {
    const dialogRef = this.dialog.open(AddFoodDialogComponent, {
      width: '500px',
      data: { user: this.user }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === 'refresh') {
        this.loadFoods();
      }
    });
  }

  openEditDialog(food: Food): void {
  const dialogRef = this.dialog.open(AddFoodDialogComponent, {
    width: '500px',
    data: { food }
  });

  dialogRef.afterClosed().subscribe(result => {
    if (result === 'refresh') {
      this.loadFoods();
    }
  });
}


  deleteFood(id: number): void {
    if (!confirm('Êtes-vous sûr de vouloir supprimer cet aliment ?')) return;
    this.foodService.deleteFood(id).subscribe(() => {
      this.snackBar.open('Aliment supprimé ✅', 'Fermer', { duration: 3000 });
      this.loadFoods();
    });
  }

  getImage(food: Food): string {
    if (food.imageData && food.imageType) {
      return `data:${food.imageType};base64,${food.imageData}`;
    } else {
      return 'assets/images/no-image.jpg';
    }
  }

  goToProfile(): void {
    this.router.navigate(['/profile']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
