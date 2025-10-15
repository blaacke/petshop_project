import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login.component';
import { CadastroComponent } from './pages/cadastro/cadastro.component';
import { AdminApprovalComponent } from './pages/admin-approval/admin-approval.component';
import { PostCreateComponent } from './pages/post-create/post-create.component';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
    {
        path: 'login', loadComponent: () => import('./auth/login.component')
            .then(m => m.LoginComponent)
    },
    { path: 'cadastro', component: CadastroComponent },
    { path: 'post-create', component: PostCreateComponent },
    { path: 'admin-approval', component: AdminApprovalComponent },
    { path: 'admin', canActivate: [adminGuard], loadComponent: () => import('./pages/admin/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent) },
    { path: 'cliente', loadComponent: () => import('./pages/cliente/cliente-dashboard/cliente-dashboard.component').then(m => m.ClienteDashboardComponent) },
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    { path: '**', redirectTo: 'login' },
];
