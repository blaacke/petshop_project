import { Component, inject, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { decodeJwt, getTipoFromClaims } from '../core/utils/jwt.util';
import { TokenService } from '../core/services/token.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="centered-card">
      <header style="text-align:center;margin-bottom:12px">
        <h1>🐾 Bem-vindo ao nosso Petshop</h1>
        <p>Faça login para acessar seu painel.</p>
      </header>

      <form [formGroup]="form" (ngSubmit)="submit()" style="display:flex;flex-direction:column;gap:12px">
        <label>
          CPF
          <input class="input" formControlName="cpf" placeholder="000.000.000-00" />
        </label>

        <label>
          Senha
          <input class="input" type="password" formControlName="senha" placeholder="••••••••" />
        </label>

        <button type="submit" [disabled]="form.invalid || loading">
          {{ loading ? 'Entrando...' : 'Entrar' }}
        </button>

        <p *ngIf="error" style="color:#b00020">{{ error }}</p>
      </form>

      <hr style="margin:16px 0;border:none;border-top:1px solid #eee"/>

      <p style="text-align:center">
        Novo por aqui?
        <a routerLink="/cadastro">Faça seu cadastro.</a>
      </p>
    </div>
  `
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  loading = false;
  error = '';

  form = this.fb.group({
    cpf: ['', [Validators.required]],
    senha: ['', [Validators.required]],
  });


  private tokens = inject(TokenService);
  private zone = inject(NgZone);

  submit() {
    this.error = '';
    if (this.form.invalid) return;
    this.loading = true;

    this.auth.login(this.form.value as any).subscribe({
      next: (res) => {
        console.log('Login ok, token:', res);
        this.tokens.set(res);

        const claims = decodeJwt(res);
        const tipo = getTipoFromClaims(claims);
        const destino = tipo === 'ADMIN' ? '/admin' : '/cliente';

        this.loading = false;
        this.zone.run(() => {
          this.router.navigate([destino], { replaceUrl: true });
        });
      },
      error: (err) => {
        this.loading = false;
        if (err?.status === 401) {
          this.error = 'Usuário não existe ou está inativo. Contate o administrador.';
          return;
        }
        this.error = err?.error?.error || err?.error?.message || 'Falha no login.';
      }
    });
  }
}
