import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { User } from '../../core/models/user.model';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.html',
  styleUrls: ['./profile.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule
  ]
})
export class ProfileComponent implements OnInit {
  user?: User;
  profileForm!: FormGroup;
  isEditing = false;
  confirmDelete = false; // première confirmation

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private fb: FormBuilder,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadUser();
  }

  loadUser(): void {
    this.userService.getCurrentUser().subscribe(u => {
      this.user = u;
      this.profileForm = this.fb.group({
        firstName: [u.firstName, Validators.required],
        lastName: [u.lastName, Validators.required],
        username: [u.username, Validators.required],
        email: [u.email, [Validators.required, Validators.email]],
      });
    });
  }

  enableEdit(): void {
    this.isEditing = true;
  }

  saveChanges(): void {
    if (this.profileForm.valid && this.user) {
      const formValues = this.profileForm.value;

      // créer un objet avec seulement les champs qui ont changé
      const updatedUser: Partial<User> = {};
      Object.keys(formValues).forEach(key => {
        if (formValues[key] !== (this.user as any)[key]) {
          (updatedUser as any)[key] = formValues[key];
        }
      });

      this.userService.updateUser(updatedUser as User).subscribe(u => {
        this.user = u;
        this.isEditing = false;
        this.snackBar.open('Profil mis à jour avec succès ✅', 'Fermer', { duration: 3000 });
      });
    }
  }

  cancelEdit(): void {
    this.isEditing = false;
    this.profileForm.patchValue(this.user!);
  }

  deleteAccount(): void {
    if (!this.confirmDelete) {
      this.confirmDelete = true; // première confirmation
      this.snackBar.open('Cliquez encore pour confirmer la suppression ⚠️', 'Fermer', { duration: 3000 });
      setTimeout(() => this.confirmDelete = false, 5000); // reset au bout de 5s
      return;
    }

    this.userService.deleteUser().subscribe(() => {
      this.authService.logout();
      this.snackBar.open('Compte supprimé avec succès ✅', 'Fermer', { duration: 3000 });
      this.router.navigate(['/login']); // redirection login
    });
  }
}
