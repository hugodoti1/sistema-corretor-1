import { CommonBankErrorCode } from './bankErrorCodes';

export interface BankError {
  id: string;
  message: string;
  bankName: string;
  category: 'CONNECTION' | 'AUTHENTICATION' | 'VALIDATION' | 'TIMEOUT' | 'SYSTEM';
  severity: 'low' | 'medium' | 'high';
  timestamp: string;
  details?: Record<string, unknown>;
  errorCode?: string;
  commonErrorCode?: CommonBankErrorCode;
  resolvedAt: string | null;
}

export interface BankErrorResponse {
  errorCode: string;
  message: string;
  bank: string;
  details?: string;
}

export type ErrorSeverity = 'error' | 'warning' | 'info';
export type ErrorCategory = 'auth' | 'account' | 'transaction' | 'general';

export const getErrorSeverity = (code: string): ErrorSeverity => {
  if (code.startsWith('ERR-GEN-') || code.startsWith('ERR-AUTH-')) {
    return 'error';
  }
  if (code.startsWith('ERR-ACC-') || code.startsWith('ERR-TRX-')) {
    return 'warning';
  }
  return 'info';
};

export const getErrorCategory = (code: string): ErrorCategory => {
  if (code.startsWith('ERR-AUTH-')) return 'auth';
  if (code.startsWith('ERR-ACC-')) return 'account';
  if (code.startsWith('ERR-TRX-')) return 'transaction';
  return 'general';
};

export const categoryLabels: Record<ErrorCategory, string> = {
  auth: 'Autenticação',
  account: 'Conta',
  transaction: 'Transação',
  general: 'Geral',
};

export const severityLabels: Record<ErrorSeverity, string> = {
  error: 'Erro',
  warning: 'Alerta',
  info: 'Informação',
};

export const getBankName = (bank?: string): string => {
  switch (bank?.toLowerCase()) {
    case 'bb':
      return 'Banco do Brasil';
    case 'inter':
      return 'Banco Inter';
    case 'caixa':
      return 'Caixa Econômica Federal';
    default:
      return bank || 'Banco';
  }
};
