import axios from 'axios';
import { IContaBancaria, ITransacaoBancaria, IExtratoResponse } from '../interfaces/IBanco';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export const BancoService = {
    // Gerenciamento de Contas Bancárias
    listarContas: async (): Promise<IContaBancaria[]> => {
        const response = await axios.get(`${API_URL}/banco/contas`);
        return response.data;
    },

    adicionarConta: async (conta: Omit<IContaBancaria, 'id'>): Promise<IContaBancaria> => {
        const response = await axios.post(`${API_URL}/banco/contas`, conta);
        return response.data;
    },

    atualizarConta: async (id: number, conta: IContaBancaria): Promise<IContaBancaria> => {
        const response = await axios.put(`${API_URL}/banco/contas/${id}`, conta);
        return response.data;
    },

    removerConta: async (id: number): Promise<void> => {
        await axios.delete(`${API_URL}/banco/contas/${id}`);
    },

    // Integração Bancária
    sincronizarConta: async (contaId: number): Promise<void> => {
        await axios.post(`${API_URL}/banco/contas/${contaId}/sincronizar`);
    },

    obterExtrato: async (contaId: number, dataInicio: string, dataFim: string): Promise<IExtratoResponse> => {
        const response = await axios.get(`${API_URL}/banco/contas/${contaId}/extrato`, {
            params: { dataInicio, dataFim }
        });
        return response.data;
    },

    // Conciliação Bancária
    conciliarTransacao: async (transacaoBancariaId: number, transacaoSistemaId: number): Promise<void> => {
        await axios.post(`${API_URL}/banco/conciliacao`, {
            transacaoBancariaId,
            transacaoSistemaId
        });
    },

    desconciliarTransacao: async (transacaoBancariaId: number): Promise<void> => {
        await axios.delete(`${API_URL}/banco/conciliacao/${transacaoBancariaId}`);
    },

    // Webhooks para notificações bancárias
    registrarWebhook: async (contaId: number, url: string): Promise<void> => {
        await axios.post(`${API_URL}/banco/webhooks`, {
            contaId,
            url
        });
    }
};
