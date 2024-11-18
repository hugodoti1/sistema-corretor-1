export interface ITransacao {
    id?: number;
    descricao: string;
    valor: number;
    data: string;
    tipo: 'RECEITA' | 'DESPESA';
    categoria: string;
    status: 'PENDENTE' | 'PAGO' | 'CANCELADO';
}

export interface IBalanco {
    totalReceitas: number;
    totalDespesas: number;
    saldoAtual: number;
}
