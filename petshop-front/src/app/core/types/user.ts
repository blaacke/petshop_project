export type TipoCadastro = 'ADMIN' | 'CLIENTE';
export type TipoEndereco = 'Casa' | 'Trabalho' | 'Outro';
export type ContatoTag = 'Principal' | 'Emergência' | 'Trabalho' | 'Outro';
export type TipoContato = 'Telefone' | 'Celular' | 'E-mail' | 'Outro';

export interface UsuarioDTO {
    tipo: TipoCadastro;
    cpf: string;
    nome: string;
    password: string;
    perfil: TipoCadastro;
}

export interface EnderecoDTO {
    logradouro: string;
    numero?: string;
    complemento?: string;
    bairro?: string;
    cidade: string;
    uf: string;
    cep?: string;
    tag: TipoEndereco;
}

export interface ContatoDTO {
    tag?: ContatoTag;
    tipo: TipoContato;
    valor: string;
}

export interface PetDTO {
    nome: string;
    descricao: String;
    dob?: string;
}

export interface UsuarioCreateResponse {
    usuario: UsuarioDTO;
    required: boolean;
    link?: string;
    expiresAt?: string;
}

export interface NewUserDTO {
    usuario: UsuarioDTO;
    contato?: ContatoDTO;
    pet?: PetDTO;
}

