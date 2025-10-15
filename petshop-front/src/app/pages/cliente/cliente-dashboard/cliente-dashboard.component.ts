import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { ClienteService } from '../../../core/services/cliente.service';
import { TokenService } from '../../../core/services/token.service';
import { AtendimentoService } from '../../../core/services/atendimento.service';
import { ClienteFullDTO, PetFullDTO } from '../../../core/types/cliente';
import { AtendimentoDTO, TipoAtendimento } from '../../../core/types/atendimento';
import { map } from 'rxjs';
import { PetDTO } from '../../../core/types/user';
import { HttpParams } from '@angular/common/http';
import { PetService } from '../../../core/services/pet.service';

const PRICE_BY_TIPO: Record<TipoAtendimento, number> = {
  banho: 50,
  tosa: 80,
  banho_e_tosa: 110
};

function todayISO(): string {
  const d = new Date();
  return d.toISOString().slice(0, 10); // YYYY-MM-DD
}
function toHHMM(d: Date) {
  return d.toTimeString().slice(0, 5); // 'HH:MM'
}

@Component({
  selector: 'app-cliente-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  styleUrls: ['./cliente-dashboard.component.scss'],
  templateUrl: './cliente-dashboard.component.html'
})
export class ClienteDashboardComponent implements OnInit {
  private clienteSrv = inject(ClienteService);
  private atSrv = inject(AtendimentoService);
  private fb = inject(FormBuilder);
  private tokens = inject(TokenService);
  private router = inject(Router);

  //atendimentos
  bookedTimes = signal<string[]>([]);
  selectedDate = signal<string>(todayISO());
  selectedTime = signal<string | null>(null);
  today = new Date();

  loading = signal(false);
  cliente = signal<ClienteFullDTO | null>(null);
  userName = signal<string>('Cliente');

  novoOpen = signal(false);
  novoValor = signal<number>(PRICE_BY_TIPO['banho']);

  novoForm = this.fb.group({
    petId: [null as number | null, Validators.required],
    date: [todayISO(), Validators.required],
    time: [null as string | null, Validators.required],
    tipo: ['banho' as TipoAtendimento, Validators.required],
    observacao: ['']
  });

  agendamentos = computed(() => {
    const c = this.cliente();
    if (!c?.pets?.length) return [] as { pet: PetFullDTO; item: AtendimentoDTO }[];
    const list = c.pets.flatMap(p => (p.atendimentos ?? []).map(a => ({ pet: p, item: a })));
    return list.sort((a, b) => {
      const ta = Date.parse(a.item.data ?? '');
      const tb = Date.parse(b.item.data ?? '');
      return (isNaN(tb) ? 0 : tb) - (isNaN(ta) ? 0 : ta);
    });
  });

  ngOnInit() {

    this.loadCliente();
    const c = this.cliente();

    this.novoForm.get('date')!.valueChanges.subscribe(date => {
      this.selectedDate.set(date || '');
      this.selectedTime.set(null);
      if (date && !this.isWeekend(date)) {
        this.fetchBooked(date);
      } else {
        this.bookedTimes.set([]);
      }
    });

    this.fetchBooked(this.selectedDate());

    this.novoForm.get('tipo')!.valueChanges.subscribe((tipo) => {
      if (!tipo) return;
      this.novoValor.set(PRICE_BY_TIPO[tipo as TipoAtendimento]);
    });
  }

  fetchBooked(dateISO: string) {
    this.atSrv.getBookedForDay(dateISO).pipe(
      map(list => (list || []).map(dtIso => toHHMM(new Date(dtIso))))
    ).subscribe(times => this.bookedTimes.set(times));
  }

  loadCliente() {
    this.loading.set(true);
    this.clienteSrv.getMyFull().subscribe({
      next: (c) => { this.cliente.set(c); this.userName.set(c?.nome || 'Pessoa ;)'); this.loading.set(false); },
      error: () => { this.cliente.set(null); this.loading.set(false); }
    });
  }

  openNovoAtend() {
    this.novoForm.reset({
      petId: null,
      date: todayISO(),
      time: null,
      tipo: 'banho',
      observacao: ''
    });
    const c = this.cliente();
    if (c?.pets?.length === 1) this.novoForm.patchValue({ petId: c.pets[0].id });
    this.novoValor.set(PRICE_BY_TIPO['banho']);
    this.selectedDate.set(this.novoForm.value.date!);
    this.selectedTime.set(null);
    this.fetchBooked(this.selectedDate());
    this.novoOpen.set(true);
  }

  closeNovo() {
    this.novoOpen.set(false);
  }

  private toLocalDateTime(dateISO: string, hhmm: string): string {
    return `${dateISO}T${hhmm}:00`;
  }

  submitNovo() {
    if (this.novoForm.invalid) { this.novoForm.markAllAsTouched(); return; }

    const { petId, date, time, tipo, observacao } = this.novoForm.value;
    const valor = PRICE_BY_TIPO[(tipo as TipoAtendimento)];

    if (!date || !time || this.isWeekend(date)) return;
    if (this.isPastSlot(date, time)) return;
    if (this.isBooked(time)) return;

    const body: AtendimentoDTO = {
      petId: petId!,
      data: this.toLocalDateTime(date, time),
      tipo: tipo as any,
      descricao: (observacao ?? '').trim(),
      valor,
      situacao: 'aguardando'
    };

    this.loading.set(true);
    this.atSrv.create(body).subscribe({
      next: () => {
        this.novoOpen.set(false);
        this.loadCliente();
      },
      error: () => { this.loading.set(false); }
    });
  }


  formatBRL(v: number | null | undefined): string {
    if (v == null) return '';
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' })
      .format(v)
  }

  logout() {
    this.tokens.clear();
    this.router.navigate(['/login'], { replaceUrl: true });
  }

  slotsFor(dateISO: string): string[] {
    if (!dateISO || this.isWeekend(dateISO)) return [];
    const [y, m, d] = dateISO.split('-').map(Number);
    const start = new Date(y, m - 1, d, 8, 0, 0, 0);
    const end = new Date(y, m - 1, d, 18, 0, 0, 0);

    const out: string[] = [];
    for (let t = new Date(start); t < end; t.setMinutes(t.getMinutes() + 30)) {
      out.push(toHHMM(t));
    }
    return out;
  }

  isWeekend(dateISO: string): boolean {
    const d = new Date(dateISO + 'T00:00:00');
    const dow = d.getDay(); // 0=Dom,6=Sáb
    return dow === 0 || dow === 6;
  }

  isPastSlot(dateISO: string, hhmm: string): boolean {
    const now = new Date();
    const slot = new Date(`${dateISO}T${hhmm}:00`);
    return slot < now;
  }

  isBooked(hhmm: string): boolean {
    return this.bookedTimes().includes(hhmm);
  }

  selectTime(hhmm: string) {
    if (this.isBooked(hhmm)) return;
    this.selectedTime.set(hhmm);
    this.novoForm.patchValue({ time: hhmm });
  }

  prettyTipo(tipo: string): string {
    switch (tipo) {
      case 'banho': return 'Banho';
      case 'tosa': return 'Tosa';
      case 'banho_e_tosa': return 'Banho e Tosa';
      default: return tipo;
    }
  }

  isPastDateTime(dtIso: string): boolean {
    return new Date(dtIso) < new Date();
  }

  canCancel(a: AtendimentoDTO): boolean {
    //só cancela se aguardando e ainda não passou
    if (!a) return false;
    const sit = (a.situacao || 'aguardando');
    return sit === 'aguardando' && !this.isPastDateTime(a.data);
  }

  onCancel(ag: { pet: any; item: AtendimentoDTO }) {
    if (!ag?.item?.id) return;
    if (!confirm(`Cancelar o agendamento de "${ag.pet?.nome}" em ${new Date(ag.item.data).toLocaleString()}?`)) return;

    this.loading.set(true);
    this.atSrv.updateSituacao(ag.item.id, 'cancelado').subscribe({
      next: () => {
        this.loadCliente();
      },
      error: () => { this.loading.set(false); }
    });
  }

  //add new pet
  novoPetOpen = signal(false);
  private petService = inject(PetService);

  novoPetForm = this.fb.group({
    nome: ['', [Validators.required, Validators.minLength(2)]],
    descricao: ['', [Validators.required, Validators.minLength(2)]],
    dob: ['']
  });

  openNovoPet() {
    this.novoPetForm.reset({ nome: '', descricao: '', dob: '' });
    this.novoPetOpen.set(true);
  }
  closeNovoPet() { this.novoPetOpen.set(false); }

  private toISODate(val?: string | null): string | undefined {
    if (!val) return undefined;
    return /^\d{4}-\d{2}-\d{2}$/.test(val) ? val : undefined;
  }

  submitNovoPet() {
    if (this.novoPetForm.invalid || !this.cliente()) {
      this.novoPetForm.markAllAsTouched();
      return;
    }
    const body: PetDTO = {
      nome: this.novoPetForm.value.nome!.trim(),
      descricao: this.novoPetForm.value.descricao!.trim(),
      dob: this.toISODate(this.novoPetForm.value.dob || '') // opcional
    };

    const params = new HttpParams().set('clienteId', String(this.cliente()!.id));

    this.loading.set(true);
    this.petService.create(this.cliente()!.id, body).subscribe({
      next: () => {
        this.novoPetOpen.set(false);
        this.loadCliente();
      },
      error: () => this.loading.set(false)
    });

  }


}
