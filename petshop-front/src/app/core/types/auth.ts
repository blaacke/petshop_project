export interface LoginRequest {
    cpf: string;
    senha: string;
}

export interface LoginResponse {
    token: string;
    expiresIn: number;
    tokenType: 'Bearer' | string;
}
