import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-post-create',
  imports: [CommonModule, RouterLink],
  template: `
  <div class="card">
    <h2>Cadastro realizado</h2>

    <ng-container *ngIf="required; else clienteOk">
      <p>Sua conta <b>ADMIN</b> foi criada como <i>pendente</i>. Você pode abrir a página de aprovação pelo link abaixo ou ir direto pelo botão:</p>

      <div class="row">
        <input class="input" [value]="link" readonly (click)="copy()" />
        <button (click)="copy()" [disabled]="!link">{{copied ? 'Copiado!' : 'Copiar'}}</button>
      </div>

      <p *ngIf="expiresAt">Expira em: {{ expiresAt | date:'short' }}</p>

      <div class="actions">
        <a class="btn" [class.disabled]="!tokenFromLink" [routerLink]="['/admin-approval']" [queryParams]="{ token: tokenFromLink }">
          Ir para aprovação
        </a>
        <a class="btn ghost" [routerLink]="['/login']">Voltar ao login</a>
      </div>

      <p *ngIf="!link" class="warn">
        Não encontramos o link de aprovação. Verifique seu e-mail ou tente novamente o cadastro.
      </p>
    </ng-container>

    <ng-template #clienteOk>
      <p>Seu cadastro foi concluído com sucesso. Faça login para começar.</p>
      <div class="actions">
        <a class="btn" [routerLink]="['/login']">Fazer login</a>
      </div>
    </ng-template>
  </div>
  `,
  styles: [`
    .card{max-width:620px;margin:40px auto;padding:20px;border:1px solid #ddd;border-radius:12px;background:#fff}
    .row{display:flex;gap:8px;align-items:center;margin:12px 0}
    .input{flex:1;padding:10px 12px;border:1px solid #ccc;border-radius:8px}
    .btn{display:inline-block;padding:10px 14px;border:1px solid #111;border-radius:10px;text-decoration:none;background:#111;color:#fff}
    .btn.ghost{background:#fff;color:#111}
    .btn.disabled{pointer-events:none;opacity:.5}
    .actions{display:flex;gap:8px;margin-top:12px}
    .warn{margin-top:12px;color:#b00020}
  `]
})
export class PostCreateComponent {
  required = false;
  link = '';
  expiresAt?: string;
  copied = false;

  constructor(private router: Router, private route: ActivatedRoute) {
    const nav = this.router.getCurrentNavigation();
    const st: any = nav?.extras?.state ?? null;

    this.required = st?.required ?? (this.route.snapshot.queryParamMap.get('required') === 'true');
    this.link = st?.link ?? this.route.snapshot.queryParamMap.get('link') ?? '';
    this.expiresAt = st?.expiresAt ?? this.route.snapshot.queryParamMap.get('exp') ?? undefined;
  }

  get tokenFromLink(): string {
    try {
      if (!this.link) return '';
      const url = new URL(this.link);
      return url.searchParams.get('token') ?? '';
    } catch {
      //se vier só o token, retorna direto
      return this.link || '';
    }
  }

  async copy() {
    if (!this.link) return;
    try {
      await navigator.clipboard.writeText(this.link);
      this.copied = true;
      setTimeout(() => (this.copied = false), 1500);
    } catch {
      this.copied = false;
    }
  }
}
