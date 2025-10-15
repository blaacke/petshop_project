import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { UsuarioDTO } from '../../core/types/user';
import { UsuarioService } from '../../core/services/usuario.service';

@Component({
  selector: 'app-admin-approval',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="centered-card">
      <h2>Aprovação de Administrador</h2>

      <section *ngIf="usuario" class="user-box">
        <p><strong>Nome:</strong> {{ usuario.nome }}</p>
        <p><strong>CPF:</strong> {{ usuario.cpf }}</p>
      </section>

      <form [formGroup]="form" (ngSubmit)="approve(true)">
        <label class="label">Senha de aprovação</label>
        <div class="password-box">
          <input [type]="hide ? 'password':'text'" formControlName="secret" placeholder="Digite a senha"/>
          <button type="button" class="icon-btn" (click)="hide = !hide" aria-label="mostrar/ocultar senha">
            {{ hide ? '👁️' : '🙈' }}
          </button>
        </div>
        <div class="actions">
          <button class="btn primary" type="submit" [disabled]="form.invalid || busy">Aprovar</button>
          <button class="btn danger" type="button" (click)="approve(false)" [disabled]="form.invalid || busy">Negar</button>
        </div>
      </form>

      <p *ngIf="msg" class="msg">{{ msg }}</p>
    </div>
  `,
  styles: [`
    .centered-card{max-width:520px;margin:48px auto;padding:24px;border:1px solid #e5e7eb;border-radius:12px}
    .user-box{background:#fafafa;border:1px solid #eee;border-radius:8px;padding:12px;margin:12px 0}
    .label{display:block;margin:12px 0 8px}
    .password-box{display:flex;gap:8px;align-items:center}
    .password-box input{flex:1;padding:10px;border:1px solid #ddd;border-radius:8px}
    .icon-btn{border:1px solid #ddd;border-radius:8px;padding:8px;background:#fff;cursor:pointer}
    .actions{display:flex;gap:8px;margin-top:16px}
    .btn{padding:10px 14px;border-radius:8px;border:1px solid #ddd;background:#fff;cursor:pointer}
    .btn.primary{background:#2563eb;color:#fff;border-color:#2563eb}
    .btn.danger{background:#ef4444;color:#fff;border-color:#ef4444}
    .msg{margin-top:12px}
  `]
})
export class AdminApprovalComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private usuarioService = inject(UsuarioService);

  hide = true;
  busy = false;
  msg = '';
  token: string | null = null;
  usuario?: UsuarioDTO;

  form = this.fb.group({
    secret: ['', [Validators.required, Validators.minLength(8)]],
  });

  constructor() {
    this.usuario = this.router.getCurrentNavigation()?.extras?.state?.['usuario'];
    this.token = this.route.snapshot.queryParamMap.get('token');
  }

  approve(approve: boolean) {
    if (!this.token) {
      this.msg = 'Token ausente na URL.';
      return;
    }
    this.busy = true;
    const secret = this.form.value.secret as string;

    this.usuarioService.decidir({ token: this.token, secret, approve })
      .subscribe({
        next: (res) => {
          this.msg = res.message || (approve ? 'Aprovado' : 'Negado');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          this.msg = err?.error?.message || 'Falha ao decidir.';
          this.busy = false;
        }
      });
  }
}
