import { BankError } from '../types/bankError';
import { BankException, BankPaymentException } from './BankException';
import { CommonBankErrorCode, bankErrorCodes } from '../types/bankErrorCodes';
import { toast } from 'react-toastify';

export class GlobalExceptionHandler {
  private static instance: GlobalExceptionHandler;
  private errorCallbacks: Map<string, ((error: BankError) => void)[]>;
  private errorLog: BankError[];
  private readonly maxLogSize: number = 1000;

  private constructor() {
    this.errorCallbacks = new Map();
    this.errorLog = [];
  }

  public static getInstance(): GlobalExceptionHandler {
    if (!GlobalExceptionHandler.instance) {
      GlobalExceptionHandler.instance = new GlobalExceptionHandler();
    }
    return GlobalExceptionHandler.instance;
  }

  /**
   * Registra um callback para ser chamado quando ocorrer um erro específico
   */
  public registerErrorCallback(
    category: string,
    callback: (error: BankError) => void
  ): void {
    const callbacks = this.errorCallbacks.get(category) || [];
    callbacks.push(callback);
    this.errorCallbacks.set(category, callbacks);
  }

  /**
   * Remove um callback registrado
   */
  public unregisterErrorCallback(
    category: string,
    callback: (error: BankError) => void
  ): void {
    const callbacks = this.errorCallbacks.get(category) || [];
    const index = callbacks.indexOf(callback);
    if (index > -1) {
      callbacks.splice(index, 1);
      this.errorCallbacks.set(category, callbacks);
    }
  }

  /**
   * Trata uma exceção bancária
   */
  public async handleException(error: Error | BankException): Promise<void> {
    let bankError: BankError;

    if (error instanceof BankException) {
      bankError = error.toBankError();
    } else {
      bankError = {
        message: error.message,
        category: 'UNKNOWN',
        severity: 'high',
        timestamp: new Date(),
        commonErrorCode: CommonBankErrorCode.UNKNOWN_ERROR
      };
    }

    // Registra o erro
    this.logError(bankError);

    // Notifica os callbacks registrados
    await this.notifyCallbacks(bankError);

    // Tratamento específico por categoria
    switch (bankError.category) {
      case 'AUTHENTICATION':
        await this.handleAuthenticationError(bankError);
        break;
      case 'PAYMENT':
        await this.handlePaymentError(bankError);
        break;
      case 'CONNECTION':
        await this.handleConnectionError(bankError);
        break;
      case 'VALIDATION':
        await this.handleValidationError(bankError);
        break;
      case 'SECURITY':
        await this.handleSecurityError(bankError);
        break;
      default:
        await this.handleGenericError(bankError);
    }

    // Mostra notificação baseada na severidade
    this.showErrorNotification(bankError);
  }

  /**
   * Trata erros de autenticação
   */
  private async handleAuthenticationError(error: BankError): Promise<void> {
    switch (error.commonErrorCode) {
      case CommonBankErrorCode.TOKEN_EXPIRED:
      case CommonBankErrorCode.SESSION_EXPIRED:
        // Redireciona para login
        window.location.href = '/login';
        break;
      case CommonBankErrorCode.ACCOUNT_LOCKED:
      case CommonBankErrorCode.ACCOUNT_DISABLED:
        // Mostra mensagem específica
        this.showBlockedAccountMessage(error);
        break;
      case CommonBankErrorCode.MFA_REQUIRED:
        // Redireciona para verificação MFA
        window.location.href = '/mfa';
        break;
    }
  }

  /**
   * Trata erros de pagamento
   */
  private async handlePaymentError(error: BankError): Promise<void> {
    if (error instanceof BankPaymentException) {
      // Verifica se pode retentar
      if (error.canRetry()) {
        await this.handleRetryablePayment(error);
      } else {
        // Mostra passos de resolução
        this.showResolutionSteps(error);
      }

      // Verifica se precisa confirmar status
      if (error.requiresStatusCheck()) {
        await this.scheduleStatusCheck(error);
      }
    }

    // Registra métricas específicas de pagamento
    this.trackPaymentError(error);
  }

  /**
   * Trata erros de conexão
   */
  private async handleConnectionError(error: BankError): Promise<void> {
    switch (error.commonErrorCode) {
      case CommonBankErrorCode.SERVICE_UNAVAILABLE:
      case CommonBankErrorCode.MAINTENANCE_MODE:
        this.showServiceUnavailableMessage(error);
        break;
      case CommonBankErrorCode.RATE_LIMIT_EXCEEDED:
        await this.handleRateLimitExceeded(error);
        break;
      default:
        await this.attemptReconnection(error);
    }
  }

  /**
   * Trata erros de validação
   */
  private async handleValidationError(error: BankError): Promise<void> {
    // Destaca campos com erro
    this.highlightInvalidFields(error);

    // Mostra mensagens de validação
    this.showValidationMessages(error);
  }

  /**
   * Trata erros de segurança
   */
  private async handleSecurityError(error: BankError): Promise<void> {
    switch (error.commonErrorCode) {
      case CommonBankErrorCode.SUSPICIOUS_ACTIVITY:
      case CommonBankErrorCode.FRAUD_DETECTED:
        this.showSecurityAlert(error);
        break;
      case CommonBankErrorCode.BLOCKED_REGION:
      case CommonBankErrorCode.INVALID_IP:
        this.showLocationRestrictionMessage(error);
        break;
    }

    // Registra tentativa suspeita
    this.logSecurityEvent(error);
  }

  /**
   * Trata erros genéricos
   */
  private async handleGenericError(error: BankError): Promise<void> {
    // Mostra mensagem genérica
    this.showErrorMessage(error);

    // Registra erro não categorizado
    this.logUnknownError(error);
  }

  /**
   * Trata pagamentos que podem ser retentados
   */
  private async handleRetryablePayment(error: BankPaymentException): Promise<void> {
    const maxRetries = 3;
    const retryDelay = 5000; // 5 segundos

    let retryCount = 0;
    while (retryCount < maxRetries) {
      try {
        // Tenta novamente após delay
        await new Promise(resolve => setTimeout(resolve, retryDelay));
        // Aqui você implementaria a lógica de retry
        retryCount++;
      } catch (retryError) {
        if (retryError instanceof BankPaymentException && !retryError.canRetry()) {
          break;
        }
      }
    }
  }

  /**
   * Agenda verificação de status do pagamento
   */
  private async scheduleStatusCheck(error: BankPaymentException): Promise<void> {
    const checkInterval = 30000; // 30 segundos
    const maxChecks = 5;

    let checkCount = 0;
    const statusCheck = setInterval(async () => {
      try {
        // Aqui você implementaria a verificação de status
        checkCount++;
        if (checkCount >= maxChecks) {
          clearInterval(statusCheck);
        }
      } catch (checkError) {
        clearInterval(statusCheck);
      }
    }, checkInterval);
  }

  /**
   * Trata erro de rate limit
   */
  private async handleRateLimitExceeded(error: BankError): Promise<void> {
    const retryAfter = error.details?.retryAfter || 60000; // 1 minuto padrão
    await new Promise(resolve => setTimeout(resolve, retryAfter));
    // Aqui você implementaria a retentativa após o tempo de espera
  }

  /**
   * Tenta reconexão em caso de erro de conexão
   */
  private async attemptReconnection(error: BankError): Promise<void> {
    const maxAttempts = 3;
    const reconnectDelay = 3000; // 3 segundos

    let attempts = 0;
    while (attempts < maxAttempts) {
      try {
        // Tenta reconectar
        await new Promise(resolve => setTimeout(resolve, reconnectDelay));
        // Aqui você implementaria a reconexão
        attempts++;
      } catch (reconnectError) {
        // Continua tentando
      }
    }
  }

  /**
   * Registra erro no log
   */
  private logError(error: BankError): void {
    this.errorLog.unshift(error);
    if (this.errorLog.length > this.maxLogSize) {
      this.errorLog.pop();
    }
  }

  /**
   * Notifica callbacks registrados
   */
  private async notifyCallbacks(error: BankError): Promise<void> {
    const callbacks = this.errorCallbacks.get(error.category) || [];
    await Promise.all(callbacks.map(callback => callback(error)));
  }

  /**
   * Mostra notificação de erro
   */
  private showErrorNotification(error: BankError): void {
    const options = this.getNotificationOptions(error);
    toast.error(error.message, options);
  }

  /**
   * Retorna opções de notificação baseadas na severidade
   */
  private getNotificationOptions(error: BankError): object {
    const baseOptions = {
      position: toast.POSITION.TOP_RIGHT,
      autoClose: 5000
    };

    switch (error.severity) {
      case 'high':
        return {
          ...baseOptions,
          autoClose: false,
          closeOnClick: false
        };
      case 'medium':
        return {
          ...baseOptions,
          autoClose: 7000
        };
      default:
        return baseOptions;
    }
  }

  /**
   * Mostra mensagem de conta bloqueada
   */
  private showBlockedAccountMessage(error: BankError): void {
    toast.error('Sua conta está bloqueada. Entre em contato com o suporte.', {
      autoClose: false,
      closeOnClick: false
    });
  }

  /**
   * Mostra passos de resolução
   */
  private showResolutionSteps(error: BankPaymentException): void {
    const steps = error.getResolutionSteps();
    steps.forEach(step => {
      toast.info(step, { autoClose: 7000 });
    });
  }

  /**
   * Mostra mensagem de serviço indisponível
   */
  private showServiceUnavailableMessage(error: BankError): void {
    const message = error.commonErrorCode === CommonBankErrorCode.MAINTENANCE_MODE
      ? 'Sistema em manutenção. Tente novamente mais tarde.'
      : 'Serviço temporariamente indisponível. Tente novamente em alguns minutos.';

    toast.warning(message, { autoClose: 5000 });
  }

  /**
   * Destaca campos com erro de validação
   */
  private highlightInvalidFields(error: BankError): void {
    const invalidFields = error.details?.invalidFields || [];
    // Aqui você implementaria a lógica de highlight
  }

  /**
   * Mostra mensagens de validação
   */
  private showValidationMessages(error: BankError): void {
    const messages = error.details?.validationMessages || [];
    messages.forEach(message => {
      toast.warning(message, { autoClose: 5000 });
    });
  }

  /**
   * Mostra alerta de segurança
   */
  private showSecurityAlert(error: BankError): void {
    toast.error('Detectamos uma atividade suspeita. Por segurança, algumas operações foram bloqueadas.', {
      autoClose: false,
      closeOnClick: false
    });
  }

  /**
   * Mostra mensagem de restrição de localização
   */
  private showLocationRestrictionMessage(error: BankError): void {
    toast.error('Acesso bloqueado devido a restrições de localização.', {
      autoClose: 7000
    });
  }

  /**
   * Mostra mensagem de erro genérica
   */
  private showErrorMessage(error: BankError): void {
    toast.error('Ocorreu um erro inesperado. Tente novamente ou contate o suporte.', {
      autoClose: 5000
    });
  }

  /**
   * Registra evento de segurança
   */
  private logSecurityEvent(error: BankError): void {
    // Aqui você implementaria o registro de eventos de segurança
    console.error('Security Event:', {
      timestamp: error.timestamp,
      code: error.commonErrorCode,
      details: error.details
    });
  }

  /**
   * Registra erro desconhecido
   */
  private logUnknownError(error: BankError): void {
    // Aqui você implementaria o registro de erros desconhecidos
    console.error('Unknown Error:', {
      timestamp: error.timestamp,
      message: error.message,
      details: error.details
    });
  }

  /**
   * Registra métricas de erro de pagamento
   */
  private trackPaymentError(error: BankError): void {
    // Aqui você implementaria o registro de métricas
    console.error('Payment Error Metrics:', {
      timestamp: error.timestamp,
      code: error.commonErrorCode,
      details: error.details
    });
  }

  /**
   * Retorna o log de erros
   */
  public getErrorLog(): BankError[] {
    return [...this.errorLog];
  }

  /**
   * Limpa o log de erros
   */
  public clearErrorLog(): void {
    this.errorLog = [];
  }
}
