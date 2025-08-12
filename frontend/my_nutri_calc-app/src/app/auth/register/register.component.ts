import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { UserService } from '../../core/services/user.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import type { User } from '../../core/models/user.model';

@Component({
  selector: 'app-register',
  templateUrl: './register.html',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  styleUrls: ['./register.scss']
})
export class RegisterComponent {
  username = '';
  email = '';
  firstName = '';
  lastName = '';
  password = '';
  confirmPassword = '';
  errorMessage = '';
  successMessage = '';
  loading = false;

  constructor(private userService: UserService, private router: Router) {}

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.password !== this.confirmPassword) {
      this.errorMessage = 'Les mots de passe ne correspondent pas.';
      return;
    }

    this.loading = true;

    const newUser: User = {
      username: this.username,
      email: this.email,
      firstName: this.firstName,
      lastName: this.lastName,
      password: this.password,
      role: 'USER',
    };

    this.userService.register(newUser).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = 'Compte créé avec succès ! Redirection vers la page de connexion...';

        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (err) => {
        this.loading = false;
        if (err.status === 400) {
          this.errorMessage = 'Les informations fournies sont invalides.';
        } else if (err.status === 409) {
          this.errorMessage = 'Nom d\'utilisateur ou email déjà utilisé.';
        } else {
          this.errorMessage = 'Erreur serveur, veuillez réessayer plus tard.';
        }
      }
    });
  }
}
