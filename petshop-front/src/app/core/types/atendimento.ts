export type TipoAtendimento = 'banho' | 'tosa' | 'banho_e_tosa';
export type AtendimentoSit = 'aguardando' | 'realizado' | 'cancelado';

export interface AtendimentoDTO {
    id?: number | null;
    petId: number;
    descricao: string;
    valor: number;
    data: string;
    tipo: TipoAtendimento;
    situacao: AtendimentoSit;
}

export interface AdminAtendimentoDTO {
    id: number;
    data: string;
    tipo: TipoAtendimento;
    valor: number;
    situacao: AtendimentoSit;

    pet: { id: number; nome: string };

    tutor: {
        id: number;
        nome: string;
        documento?: string | null;
        contato?: { tipo: string; valor: string } | null;
    };
}