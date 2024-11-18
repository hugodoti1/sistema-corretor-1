import { BankError } from '../types/bankError';
import { metricsService } from './metricsService';
import { notificationService } from './notificationService';

const STORAGE_KEY = '@SistemaCorretor:bankErrors';
const MAX_STORED_ERRORS = 100; // Limite máximo de erros armazenados

export interface StoredBankError extends BankError {
  timestamp: number;
  id: string;
}

export const bankErrorStorage = {
  getStoredErrors(): StoredBankError[] {
    try {
      const storedData = localStorage.getItem(STORAGE_KEY);
      if (!storedData) return [];
      
      const errors = JSON.parse(storedData) as StoredBankError[];
      return errors.sort((a, b) => b.timestamp - a.timestamp);
    } catch (error) {
      console.error('Erro ao recuperar erros do localStorage:', error);
      return [];
    }
  },

  addError(error: BankError): void {
    const storedErrors = this.getStoredErrors();
    const newError: StoredBankError = {
      ...error,
      timestamp: Date.now(),
      id: crypto.randomUUID(),
    };

    // Adiciona o novo erro no início da lista
    storedErrors.unshift(newError);

    // Mantém apenas os últimos MAX_STORED_ERRORS erros
    const trimmedErrors = storedErrors.slice(0, MAX_STORED_ERRORS);

    localStorage.setItem(STORAGE_KEY, JSON.stringify(trimmedErrors));
    notificationService.notifyBankError(newError);
    metricsService.trackError(newError);
  },

  removeError(errorId: string): void {
    try {
      const storedErrors = this.getStoredErrors();
      const filteredErrors = storedErrors.filter(error => error.id !== errorId);
      localStorage.setItem(STORAGE_KEY, JSON.stringify(filteredErrors));
    } catch (error) {
      console.error('Erro ao remover erro do localStorage:', error);
      throw error;
    }
  },

  clearErrors(): void {
    try {
      localStorage.removeItem(STORAGE_KEY);
    } catch (error) {
      console.error('Erro ao limpar erros do localStorage:', error);
      throw error;
    }
  },

  /**
   * Retorna estatísticas sobre os erros armazenados
   */
  getErrorStats() {
    const errors = this.getStoredErrors();
    const stats = {
      total: errors.length,
      byBank: {} as Record<string, number>,
      byCategory: {} as Record<string, number>,
      bySeverity: {} as Record<string, number>,
      lastError: errors[0]?.timestamp || null,
      firstError: errors[errors.length - 1]?.timestamp || null,
    };

    errors.forEach(error => {
      // Contagem por banco
      const bank = error.bank || 'unknown';
      stats.byBank[bank] = (stats.byBank[bank] || 0) + 1;

      // Contagem por categoria
      const category = error.code.split('-')[1] || 'unknown';
      stats.byCategory[category] = (stats.byCategory[category] || 0) + 1;

      // Contagem por severidade
      const severity = error.code.startsWith('ERR-GEN-') || error.code.startsWith('ERR-AUTH-')
        ? 'error'
        : error.code.startsWith('ERR-ACC-') || error.code.startsWith('ERR-TRX-')
        ? 'warning'
        : 'info';
      stats.bySeverity[severity] = (stats.bySeverity[severity] || 0) + 1;
    });

    return stats;
  },
};
