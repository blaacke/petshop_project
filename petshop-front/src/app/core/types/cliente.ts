import { AtendimentoDTO } from './atendimento';
import { ContatoTag, TipoContato, TipoEndereco } from './user';

export type SituacaoUsuario = 'ATIVO' | 'INATIVO';

export interface ClienteFullDTO {
    id: number;
    usuarioId?: number | null;
    nome: string;
    documento?: string | null;
    situacao?: SituacaoUsuario | null;
    dataCadastro?: string | null;

    enderecos: EnderecoDTO[];
    contatos: ContatoDTO[];
    pets: PetFullDTO[];
}

export interface EnderecoDTO {
    logradouro?: string;
    numero?: string;
    cep?: string;
    cidade?: string;
    bairro?: string;
    complemento?: string;
    tag?: TipoEndereco | string;
}

export interface ContatoDTO {
    tag: ContatoTag;
    tipo: TipoContato;
    valor: string;
}

export interface PetFullDTO {
    id: number;
    nome: string;
    dob?: string;
    raca?: RacaDTO;
    atendimentos?: AtendimentoDTO[];
}


export interface RacaDTO {
    descricao: string;
}
