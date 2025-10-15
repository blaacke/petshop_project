import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, NgIf, NgFor } from '@angular/common';
import { Router } from '@angular/router';
import { ClienteService } from '../../../core/services/cliente.service';
import { Page } from '../../../core/types/page';
import { ClienteFullDTO, ContatoDTO } from '../../../core/types/cliente';
import { UsuarioService } from '../../../core/services/usuario.service';
import { TokenService } from '../../../core/services/token.service';
import { AtendimentoService } from '../../../core/services/atendimento.service';
import { AdminAtendimentoDTO, AtendimentoSit } from '../../../core/types/atendimento';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, NgIf, NgFor],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent implements OnInit {
  private clienteService = inject(ClienteService);
  private userService = inject(UsuarioService);
  private token = inject(TokenService);
  private router = inject(Router);
  private atService = inject(AtendimentoService);

  tab = signal<'clientes' | 'agendamentos'>('clientes');

  private expanded = signal<Record<number, boolean>>({});

  pageIndex = signal(0);
  readonly pageSize = 5;
  total = signal(0);
  totalPages = signal(1);
  loading = signal(false);

  clientes = signal<ClienteFullDTO[]>([]);

  ngOnInit() {
    this.loadClientesPage();
    this.loadAgForDay(this.selectedDay());
  }

  private loadClientesPage(sort?: string) {
    this.loading.set(true);
    this.clienteService.getPage(this.pageIndex(), this.pageSize, sort).subscribe({
      next: (page: Page<ClienteFullDTO>) => {
        this.clientes.set(page.content || []);
        this.total.set(page.totalElements ?? 0);
        this.totalPages.set(page.totalPages ?? 1);
        this.loading.set(false);
      },
      error: () => {
        this.clientes.set([]);
        this.total.set(0);
        this.totalPages.set(1);
        this.loading.set(false);
      }
    });
  }

  reloadClientes() { this.loadClientesPage(); }
  prevClientes() {
    if (this.pageIndex() === 0) return;
    this.pageIndex.update(i => i - 1);
    this.loadClientesPage();
  }
  nextClientes() {
    if (this.pageIndex() + 1 >= this.totalPages()) return;
    this.pageIndex.update(i => i + 1);
    this.loadClientesPage();
  }
  from() { return this.pageIndex() * this.pageSize; }
  to() { return Math.min(this.from() + this.clientes().length, this.total()); }

  isExpanded(id: number) { return !!this.expanded()[id]; }
  toggleExpand(id: number) {
    const map = { ...this.expanded() };
    map[id] = !map[id];
    this.expanded.set(map);
  }

  primaryContato(c: ClienteFullDTO): ContatoDTO | null {
    if (!c.contatos?.length) return null;
    return c.contatos.find(x => (x.tag || '').toString().toLowerCase() === 'principal') ?? c.contatos[0];
  }

  onToggleSituacao(c: ClienteFullDTO) {
    const next = (c.situacao === 'ATIVO' ? 'INATIVO' : 'ATIVO');
    if (!c.documento) return;
    this.userService.toggleSituacao(c.documento, next).subscribe({
      next: () => {
        this.clientes.update(list => list.map(x => x.id === c.id ? { ...x, situacao: next } : x));
      }
    });
  }

  logout() {
    this.token.clear();
    this.router.navigate(['/login'], { replaceUrl: true });
  }
  formatBRL(v: number | null | undefined): string {
    if (v == null) return '';
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' })
      .format(v).replace(/\u00A0/g, ' ');
  }

  private static isoDate(d: Date) { return d.toISOString().slice(0, 10); }
  private static addDaysLocal(dateISO: string, days: number) {
    const d = new Date(dateISO + 'T00:00:00');
    d.setDate(d.getDate() + days);
    return AdminDashboardComponent.isoDate(d);
  }

  selectedDay = signal<string>(AdminDashboardComponent.isoDate(new Date()));
  agLoading = signal(false);
  agendamentos = signal<AdminAtendimentoDTO[]>([]);

  loadAgForDay(dayISO: string) {
    this.agLoading.set(true);
    this.atService.getAdminByDay(dayISO).subscribe({
      next: (list) => { this.agendamentos.set(list || []); this.agLoading.set(false); },
      error: () => { this.agendamentos.set([]); this.agLoading.set(false); }
    });
  }
  prevDay() {
    const d = AdminDashboardComponent.addDaysLocal(this.selectedDay(), -1);
    this.selectedDay.set(d);
    this.loadAgForDay(d);
  }
  nextDay() {
    const d = AdminDashboardComponent.addDaysLocal(this.selectedDay(), +1);
    this.selectedDay.set(d);
    this.loadAgForDay(d);
  }


  updateAtendimento(a: AdminAtendimentoDTO, sit: AtendimentoSit) {

    if (a.id == null) return;
    this.atService.updateSituacao(a.id, sit).subscribe({
      next: () => {
        this.agendamentos.update(list => list.map(x => x.id === a.id ? { ...x, situacao: sit } : x));
      }
    });
  }

  canCancel(a: AdminAtendimentoDTO): boolean {
    const sit = a.situacao;
    if (sit === 'cancelado') return false;

    return true;
  }
}
