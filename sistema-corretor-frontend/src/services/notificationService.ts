import { BankError } from '../types/bankError';

export type NotificationOptions = {
  autoHideDuration?: number;
  variant?: 'success' | 'error' | 'warning' | 'info';
};

export type NotificationPayload = {
  message: string;
  options?: NotificationOptions;
};

type NotificationListener = (notification: NotificationPayload) => void;

class NotificationService {
  private listeners: Set<NotificationListener> = new Set();

  subscribe(listener: NotificationListener): () => void {
    this.listeners.add(listener);
    return () => this.listeners.delete(listener);
  }

  notify(message: string, options?: NotificationOptions): void {
    const payload: NotificationPayload = { message, options };
    this.listeners.forEach(listener => listener(payload));
  }

  notifyBankError(error: BankError): void {
    const severity = error.severity.toLowerCase();
    const variant = severity === 'high' ? 'error' : 
                   severity === 'medium' ? 'warning' : 'info';
    
    const severityText = severity === 'high' ? 'Crítico' : 
                        severity === 'medium' ? 'Atenção' : 'Informação';
    
    this.notify(
      `${severityText} - ${error.bankName}: ${error.message}`,
      { variant, autoHideDuration: 6000 }
    );
  }

  // Métodos de conveniência para diferentes tipos de notificação
  notifySuccess(message: string, duration?: number): void {
    this.notify(message, { variant: 'success', autoHideDuration: duration });
  }

  notifyError(message: string, duration?: number): void {
    this.notify(message, { variant: 'error', autoHideDuration: duration });
  }

  notifyWarning(message: string, duration?: number): void {
    this.notify(message, { variant: 'warning', autoHideDuration: duration });
  }

  notifyInfo(message: string, duration?: number): void {
    this.notify(message, { variant: 'info', autoHideDuration: duration });
  }
}

export const notificationService = new NotificationService();
