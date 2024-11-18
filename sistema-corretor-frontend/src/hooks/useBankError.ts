import { useState, useCallback, useEffect } from 'react';
import { AxiosError } from 'axios';
import { BankError } from '../types/bankError';
import { bankErrorStorage, StoredBankError } from '../services/bankErrorStorage';

const extractErrorFromResponse = (error: AxiosError): BankError => {
  if (error.response?.data) {
    const data = error.response.data;
    return {
      code: data.errorCode,
      message: data.message,
      bank: data.bank,
      details: data.details,
    };
  }

  // Fallback para erros de rede ou outros erros não estruturados
  return {
    code: 'ERR-GEN-003',
    message: 'Erro de conexão com o banco',
    details: error.message,
  };
};

export const useBankError = () => {
  const [errors, setErrors] = useState<StoredBankError[]>([]);
  const [expandedErrors, setExpandedErrors] = useState<Set<string>>(new Set());
  const [stats, setStats] = useState(bankErrorStorage.getErrorStats());

  // Carrega os erros do localStorage ao montar o componente
  useEffect(() => {
    const storedErrors = bankErrorStorage.getStoredErrors();
    setErrors(storedErrors);
  }, []);

  const addError = useCallback((error: BankError | AxiosError) => {
    const bankError = error instanceof Error ? extractErrorFromResponse(error as AxiosError) : error;
    const storedError = bankErrorStorage.storeError(bankError);
    setErrors(prev => [storedError, ...prev]);
    setStats(bankErrorStorage.getErrorStats());
  }, []);

  const removeError = useCallback((errorId: string) => {
    bankErrorStorage.removeError(errorId);
    setErrors(prev => prev.filter(error => error.id !== errorId));
    setExpandedErrors(prev => {
      const newExpanded = new Set(prev);
      newExpanded.delete(errorId);
      return newExpanded;
    });
    setStats(bankErrorStorage.getErrorStats());
  }, []);

  const clearErrors = useCallback(() => {
    bankErrorStorage.clearErrors();
    setErrors([]);
    setExpandedErrors(new Set());
    setStats(bankErrorStorage.getErrorStats());
  }, []);

  const toggleErrorExpansion = useCallback((errorId: string) => {
    setExpandedErrors(prev => {
      const newExpanded = new Set(prev);
      if (newExpanded.has(errorId)) {
        newExpanded.delete(errorId);
      } else {
        newExpanded.add(errorId);
      }
      return newExpanded;
    });
  }, []);

  const isErrorExpanded = useCallback(
    (errorId: string) => expandedErrors.has(errorId),
    [expandedErrors]
  );

  return {
    errors,
    stats,
    addError,
    removeError,
    clearErrors,
    toggleErrorExpansion,
    isErrorExpanded,
  };
};
