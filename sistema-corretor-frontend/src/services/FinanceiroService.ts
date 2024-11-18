import axios from 'axios';
import { ITransacao, IBalanco } from '../interfaces/IFinanceiro';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export const FinanceiroService = {
    listarTransacoes: async (): Promise<ITransacao[]> => {
        const response = await axios.get(`${API_URL}/financeiro/transacoes`);
        return response.data;
    },

    criarTransacao: async (transacao: Omit<ITransacao, 'id'>): Promise<ITransacao> => {
        const response = await axios.post(`${API_URL}/financeiro/transacoes`, transacao);
        return response.data;
    },

    atualizarTransacao: async (id: number, transacao: ITransacao): Promise<ITransacao> => {
        const response = await axios.put(`${API_URL}/financeiro/transacoes/${id}`, transacao);
        return response.data;
    },

    deletarTransacao: async (id: number): Promise<void> => {
        await axios.delete(`${API_URL}/financeiro/transacoes/${id}`);
    },

    obterBalanco: async (): Promise<IBalanco> => {
        const response = await axios.get(`${API_URL}/financeiro/balanco`);
        return response.data;
    }
};
