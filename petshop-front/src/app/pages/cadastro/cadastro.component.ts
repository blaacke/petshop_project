import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';

import {
  UsuarioDTO,
  TipoCadastro,
  TipoContato,
  ContatoTag,
  UsuarioCreateResponse,
  ContatoDTO,
  PetDTO,
  NewUserDTO,
} from '../../core/types/user';


@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="centered-card">
      <header style="text-align:center;margin-bottom:12px">
        <h2>Crie sua conta</h2>
        <p>Selecione o tipo de cadastro e preencha seus dados.</p>
      </header>

      <div style="display:flex; gap:16px; align-items:center; margin-bottom:12px;">
        <label style="display:flex;align-items:center;gap:6px;">
          <input type="radio" [value]="'CLIENTE'" [formControl]="tipoCtrl" />
          Cliente
        </label>
        <label style="display:flex;align-items:center;gap:6px;">
          <input type="radio" [value]="'ADMIN'" [formControl]="tipoCtrl" />
          Administrador
        </label>
      </div>

      <div *ngIf="tipoCtrl.value === 'ADMIN'"
           style="background:#fff3cd;color:#664d03;border:1px solid #ffecb5;border-radius:10px;padding:12px;margin-bottom:16px">
        <strong>Atenção:</strong> cadastros de <b>Administrador</b> dependem de aprovação.
        Você receberá um e-mail quando o acesso for liberado.
      </div>

      <form [formGroup]="form" (ngSubmit)="submit()" style="display:flex;flex-direction:column;gap:12px">
        <label> CPF
          <input
            class="input"
            formControlName="cpf"
            placeholder="000.000.000-00"
            (input)="formatarCpf($event)"
            inputmode="numeric"
          />
        </label>
        <small *ngIf="touched && form.get('cpf')?.hasError('required')" style="color:#b00020">Campo obrigatório</small>
        <div class="error" *ngIf="form.get('cpf')?.touched && form.get('cpf')?.errors">
          <small *ngIf="form.get('cpf')?.hasError('cpfTaken')" style="color:#b00020">
            {{ form.get('cpf')?.getError('serverMessage') || 'CPF já cadastrado' }}
          </small>
        </div>
        <label> Nome
          <input class="input" formControlName="nome" placeholder="Seu nome completo" />
        </label>
        <small *ngIf="touched && form.get('nome')?.hasError('required')" style="color:#b00020">Campo obrigatório</small>

<label> Senha
  <div class="input-wrap">
    <input
      [type]="showSenha ? 'text' : 'password'"
      class="input"
      formControlName="senha"
      placeholder="Mínimo 8 caracteres"
    />
    <button type="button" class="eye-btn" (click)="showSenha = !showSenha" [attr.aria-label]="showSenha ? 'Ocultar senha' : 'Visualizar senha'">
      <span *ngIf="!showSenha">👁️</span>
      <span *ngIf="showSenha">🙈</span>
    </button>
  </div>
</label>
<small *ngIf="touched && form.get('senha')?.hasError('required')" style="color:#b00020">
  Campo obrigatório
</small>
<small *ngIf="touched && form.get('senha')?.hasError('minlength')" style="color:#b00020">
  A senha deve ter pelo menos 8 caracteres
</small>

<label> Confirmar senha
  <div class="input-wrap">
    <input
      [type]="showSenhaConfirm ? 'text' : 'password'"
      class="input"
      formControlName="senhaConfirm"
      placeholder="Repita a senha"
    />
    <button type="button" class="eye-btn" (click)="showSenhaConfirm = !showSenhaConfirm" [attr.aria-label]="showSenhaConfirm ? 'Ocultar confirmação de senha' : 'Visualizar confirmação de senha'">
      <span *ngIf="!showSenhaConfirm">👁️</span>
      <span *ngIf="showSenhaConfirm">🙈</span>
    </button>
  </div>
</label>
<small *ngIf="touched && form.get('senhaConfirm')?.hasError('required')" style="color:#b00020">
  Campo obrigatório
</small>
<small *ngIf="touched && form.hasError('senhasDiferentes')" style="color:#b00020">
  As senhas não coincidem
</small>



        <!-- Cliente: bloco de Contato -->
        <fieldset *ngIf="tipoCtrl.value === 'CLIENTE'" style="border:1px solid #eee;border-radius:12px;padding:12px">
          <legend style="padding:0 8px;color:#555">Contato</legend>

          <div style="display:grid;gap:12px;grid-template-columns: 1fr 1fr">
            <label> Tipo
              <select class="input" formControlName="contatoTipo" (change)="onTipoContatoChange()">
                <option *ngFor="let t of tipoContatoOptions" [value]="t">{{t}}</option>
              </select>
            </label>

            <label> Tag
              <select class="input" formControlName="contatoTag">
                <option *ngFor="let t of contatoTagOptions" [value]="t">{{t}}</option>
              </select>
            </label>

            <label style="grid-column:1 / -1"> Valor
              <input
                class="input"
                formControlName="contatoValor"
                [placeholder]="placeholderContato"
                (input)="maskContato($event)"
                [attr.inputmode]="inputmodeContato"
              />
            </label>
            <small *ngIf="touched && form.get('contatoValor')?.invalid" style="color:#b00020">
              {{ erroContatoValor }}
            </small>
          </div>
        </fieldset>

        <!-- Cliente: bloco de Pet-->
        <fieldset *ngIf="tipoCtrl.value === 'CLIENTE'" style="border:1px solid #eee;border-radius:12px;padding:12px">
          <legend style="padding:0 8px;color:#555">Pet</legend>

          <div style="display:grid;gap:12px;grid-template-columns: 1fr 1fr">
            <label> Nome
              <input class="input" formControlName="petNome" />
            </label>

            <label> Raça
              <input class="input" formControlName="petRacaDescricao" placeholder="Ex.: Poodle, Salsicha" />
            </label>

            <label style="grid-column:1 / -1"> Nascimento
              <input class="input" formControlName="petDob" type="date" />
            </label>
          </div>
        </fieldset>

        <div style="display:flex; gap:8px; justify-content:flex-end; margin-top:8px">
          <button type="submit">{{ tipoCtrl.value === 'ADMIN' ? 'Solicitar acesso' : 'Criar conta' }}</button>
        </div>

        <p *ngIf="erro" style="color:#b00020">{{ erro }}</p>
      </form>

      <div style="text-align:center;margin-top:12px">
        <a routerLink="/login">Já tem conta? Entrar</a>
      </div>
    </div>
  `
})
export class CadastroComponent {
  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private router = inject(Router);

  showSenha = false;
  showSenhaConfirm = false;

  tipoCtrl = this.fb.control<TipoCadastro>('CLIENTE', { nonNullable: true });

  readonly tipoContatoOptions: TipoContato[] = ['Telefone', 'Celular', 'E-mail', 'Outro'];
  readonly contatoTagOptions: ContatoTag[] = ['Principal', 'Emergência', 'Trabalho', 'Outro'];

  form = this.fb.group({
    cpf: ['', Validators.required],
    nome: ['', Validators.required],
    senha: ['', [Validators.required, Validators.minLength(8)]],
    senhaConfirm: ['', [Validators.required]],

    // contato
    contatoTipo: 'Celular',
    contatoTag: 'Principal',
    contatoValor: [''],

    // pet
    petNome: ['', Validators.required],
    petRacaDescricao: ['', Validators.required],
    petDob: [''],
  }, { validators: [this.senhasIguaisValidator()] });

  senhasIguaisValidator() {
    return (group: AbstractControl): ValidationErrors | null => {
      const s = group.get('senha')?.value ?? '';
      const c = group.get('senhaConfirm')?.value ?? '';
      if (!s || !c) return null;
      return s === c ? null : { senhasDiferentes: true };
    };
  }



  touched = false;
  erro = '';

  placeholderContato = 'Digite o telefone';
  inputmodeContato: 'text' | 'numeric' | 'email' = 'numeric';

  constructor() {
    this.setValidadoresContato(this.form.value.contatoTipo as TipoContato);
  }

  formatarCpf(event: Event) {
    const input = event.target as HTMLInputElement;
    let v = input.value.replace(/\D/g, '').slice(0, 11);
    const p1 = v.substring(0, 3);
    const p2 = v.substring(3, 6);
    const p3 = v.substring(6, 9);
    const p4 = v.substring(9, 11);
    let masked = p1;
    if (p2) masked += '.' + p2;
    if (p3) masked += '.' + p3;
    if (p4) masked += '-' + p4;
    input.value = masked;
    this.form.get('cpf')?.setValue(masked, { emitEvent: false });
  }

  private maskTel(value: string) {
    // Tel fixo: (00) 0000-0000 (10 digitos)
    let v = value.replace(/\D/g, '').slice(0, 10);
    const ddd = v.substring(0, 2);
    const p1 = v.substring(2, 6);
    const p2 = v.substring(6, 10);
    if (!ddd) return '';
    if (!p1) return `(${ddd}`;
    if (!p2) return `(${ddd}) ${p1}`;
    return `(${ddd}) ${p1}-${p2}`;
  }

  private maskCel(value: string) {
    // Cel: (00) 00000-0000 (11 digitos)
    let v = value.replace(/\D/g, '').slice(0, 11);
    const ddd = v.substring(0, 2);
    const p1 = v.substring(2, 7);
    const p2 = v.substring(7, 11);
    if (!ddd) return '';
    if (!p1) return `(${ddd}`;
    if (!p2) return `(${ddd}) ${p1}`;
    return `(${ddd}) ${p1}-${p2}`;
  }

  maskContato(e: Event) {
    const input = e.target as HTMLInputElement;
    const tipo = this.form.value.contatoTipo as TipoContato;
    let val = input.value;

    if (tipo === 'Telefone') {
      input.value = this.maskTel(val);
    } else if (tipo === 'Celular') {
      input.value = this.maskCel(val);
    } else if (tipo === 'E-mail') {
      input.value = val.trim();
    } else {
      input.value = val;
    }
    this.form.get('contatoValor')?.setValue(input.value, { emitEvent: false });
  }

  onTipoContatoChange() {
    const tipo = this.form.value.contatoTipo as TipoContato;
    this.setValidadoresContato(tipo);
    this.form.get('contatoValor')?.setValue('');
  }

  private setValidadoresContato(tipo: TipoContato) {
    const ctrl = this.form.get('contatoValor')!;
    if (this.tipoCtrl.value === 'ADMIN') {
      ctrl.clearValidators();
      this.placeholderContato = '';
      this.inputmodeContato = 'text';
      ctrl.updateValueAndValidity();
      return;
    }

    if (tipo === 'Telefone') {
      ctrl.setValidators([Validators.required, Validators.minLength(14)]); // (00) 0000-0000
      this.placeholderContato = '(00) 0000-0000';
      this.inputmodeContato = 'numeric';
    } else if (tipo === 'Celular') {
      ctrl.setValidators([Validators.required, Validators.minLength(15)]); // (00) 00000-0000
      this.placeholderContato = '(00) 00000-0000';
      this.inputmodeContato = 'numeric';
    } else if (tipo === 'E-mail') {
      ctrl.setValidators([Validators.required, Validators.email]);
      this.placeholderContato = 'email@dominio.com';
      this.inputmodeContato = 'email';
    } else {
      ctrl.setValidators([Validators.required]);
      this.placeholderContato = 'Descreva o contato';
      this.inputmodeContato = 'text';
    }
    ctrl.updateValueAndValidity();
  }

  get erroContatoValor(): string {
    const c = this.form.get('contatoValor');
    if (!c || !this.touched) return '';
    const tipo = this.form.value.contatoTipo as TipoContato;
    if (c.hasError('required')) return 'Campo obrigatório';
    if (tipo === 'E-mail' && c.hasError('email')) return 'E-mail inválido';
    if (tipo === 'Telefone' && c.value && (c.value as string).replace(/\D/g, '').length < 10) return 'Telefone incompleto';
    if (tipo === 'Celular' && c.value && (c.value as string).replace(/\D/g, '').length < 11) return 'Celular incompleto';
    return 'Valor inválido';
  }

  private limparCpf(cpf: string) {
    return cpf.replace(/\D/g, '');
  }

  private extractTokenFromLink(link?: string | null): string | null {
    if (!link) return null;
    try {
      const url = new URL(link);
      return url.searchParams.get('token');
    } catch {
      return link;
    }
  }

  submit() {
    this.erro = '';
    this.touched = true;

    if (this.form.get('cpf')?.invalid || this.form.get('nome')?.invalid || this.form.get('senha')?.invalid) {
      return;
    }

    const tipo = this.tipoCtrl.value;
    const isAdmin = tipo === 'ADMIN';

    const usuario: UsuarioDTO = {
      tipo,
      cpf: this.limparCpf(this.form.value.cpf!),
      nome: this.form.value.nome!,
      password: this.form.value.senha!,
      perfil: this.tipoCtrl.value,
    };

    const payload: NewUserDTO = {
      usuario: usuario,
    };

    if (!isAdmin) {
      const contatoTipo = this.form.value.contatoTipo as TipoContato;
      this.setValidadoresContato(contatoTipo);
      if (this.form.get('contatoValor')?.invalid) return;

      payload.contato = {
        tag: this.form.value.contatoTag as ContatoTag,
        tipo: contatoTipo,
        valor: (this.form.value.contatoValor || '').toString().trim()
      };

      payload.pet = {
        nome: (this.form.value.petNome || '').toString().trim(),
        descricao: (this.form.value.petRacaDescricao || '').toString().trim() || 'RND',
        dob: (this.form.value.petDob || '').toString() || undefined,
      };
    }

    this.http.post<UsuarioCreateResponse>('http://localhost:8080/api/cadastro', payload).subscribe({
      next: (res) => {
        if (res.required) {
          const token = this.extractTokenFromLink(res.link ?? null);
          this.router.navigate(['/admin-approval'], {
            queryParams: { token },
            state: { usuario: res.usuario }
          });
        } else {
          this.router.navigate(['/post-success'], {
            state: { usuario: res.usuario }
          });
        }
      },
      error: (err: import('@angular/common/http').HttpErrorResponse) => {
        if (err.status === 409) {
          const msg = err.error?.errors?.cpf
            || err.error?.message
            || 'CPF já cadastrado.';
          const cpfCtrl = this.form.get('cpf');
          cpfCtrl?.setErrors({ cpfTaken: true, serverMessage: msg });
          cpfCtrl?.markAsTouched();
          return;
        }
        this.erro = err?.error?.message || 'Falha ao criar cadastro. Verifique os dados.';
      }
    });
  }
}
