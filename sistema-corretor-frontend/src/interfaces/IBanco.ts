export interface IContaBancaria {
    id?: number;
    banco: string;
    agencia: string;
    conta: string;
    tipo: 'CORRENTE' | 'POUPANCA';
    saldo: number;
    ultimaSincronizacao?: string;
}

export interface ITransacaoBancaria {
    id?: number;
    contaBancariaId: number;
    data: string;
    valor: number;
    tipo: 'CREDITO' | 'DEBITO';
    descricao: string;
    categoria?: string;
    conciliado: boolean;
}

export interface IExtratoResponse {
    transacoes: ITransacaoBancaria[];
    saldoAtual: number;
    dataInicio: string;
    dataFim: string;
}

export interface IConciliacaoBancaria {
    transacaoBancariaId: number;
    transacaoSistemaId: number;
    dataConciliacao: string;
    usuarioConciliacao: string;
}
