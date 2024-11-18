import { BankError, CommonBankErrorCode, getBankErrorDetails, getCommonErrorCode } from '../types/bankError';

/**
 * Classe base para exceções bancárias
 */
export class BankException extends Error {
  public readonly bankName: string;
  public readonly category: BankError['category'];
  public readonly severity: BankError['severity'];
  public readonly timestamp: Date;
  public readonly details?: Record<string, unknown>;
  public readonly errorCode?: string;
  public readonly commonErrorCode?: CommonBankErrorCode;

  constructor(
    message: string,
    bankName: string,
    category: BankError['category'],
    severity: BankError['severity'],
    details?: Record<string, unknown>,
    errorCode?: string
  ) {
    // Se tiver código de erro específico, usa a mensagem mapeada
    const errorDetails = errorCode ? getBankErrorDetails(bankName, errorCode) : undefined;
    const finalMessage = errorDetails?.message || message || 'Unknown bank error';
    
    super(finalMessage);
    
    this.name = 'BankException';
    this.bankName = bankName;
    this.category = category;
    this.severity = severity;
    this.timestamp = new Date();
    this.details = details;
    this.errorCode = errorCode;
    this.commonErrorCode = errorCode ? getCommonErrorCode(bankName, errorCode) : undefined;

    // Necessário para que instanceof funcione corretamente
    Object.setPrototypeOf(this, BankException.prototype);
  }

  /**
   * Converte a exceção em um objeto BankError
   */
  public toBankError(): BankError {
    return {
      id: crypto.randomUUID(),
      message: this.message,
      bankName: this.bankName,
      category: this.category,
      severity: this.severity,
      timestamp: this.timestamp.toISOString(),
      details: this.details,
      errorCode: this.errorCode,
      commonErrorCode: this.commonErrorCode,
      resolvedAt: null
    };
  }

  /**
   * Retorna uma representação em string da exceção
   */
  public toString(): string {
    return `[${this.severity.toUpperCase()}] ${this.bankName} - ${this.category}: ${this.message}`;
  }
}

/**
 * Exceção para erros de conexão com o banco
 */
export class BankConnectionException extends BankException {
  constructor(
    bankName: string,
    message: string = 'Erro de conexão com o banco',
    details?: Record<string, unknown>,
    errorCode?: string
  ) {
    super(message, bankName, 'CONNECTION', 'high', details, errorCode);
    this.name = 'BankConnectionException';
  }
}

/**
 * Exceção para erros de autenticação
 */
export class BankAuthenticationException extends BankException {
  constructor(
    bankName: string,
    message: string = 'Erro de autenticação',
    details?: Record<string, unknown>,
    errorCode?: string
  ) {
    super(message, bankName, 'AUTHENTICATION', 'high', details, errorCode);
    this.name = 'BankAuthenticationException';
  }
}

/**
 * Exceção para erros de validação
 */
export class BankValidationException extends BankException {
  constructor(
    bankName: string,
    message: string = 'Erro de validação',
    details?: Record<string, unknown>,
    errorCode?: string
  ) {
    super(message, bankName, 'VALIDATION', 'medium', details, errorCode);
    this.name = 'BankValidationException';
  }
}

/**
 * Exceção para erros de timeout
 */
export class BankTimeoutException extends BankException {
  constructor(
    bankName: string,
    message: string = 'Operação excedeu o tempo limite',
    details?: Record<string, unknown>,
    errorCode?: string
  ) {
    super(message, bankName, 'TIMEOUT', 'high', details, errorCode);
    this.name = 'BankTimeoutException';
  }
}

/**
 * Exceção para erros de rate limit
 */
export class BankRateLimitException extends BankException {
  constructor(
    bankName: string,
    message: string = 'Taxa de requisições excedida',
    details?: Record<string, unknown>,
    errorCode?: string
  ) {
    super(message, bankName, 'TIMEOUT', 'medium', details, errorCode);
    this.name = 'BankRateLimitException';
  }

  /**
   * Retorna o tempo de espera recomendado em milissegundos
   */
  public getRetryAfter(): number {
    return this.details?.retryAfter as number || 60000; // Padrão: 1 minuto
  }
}

/**
 * Exceção para erros de dados inválidos
 */
export class BankDataException extends BankException {
  constructor(
    bankName: string,
    message: string = 'Dados inválidos ou mal formatados',
    details?: Record<string, unknown>,
    errorCode?: string
  ) {
    super(message, bankName, 'VALIDATION', 'medium', details, errorCode);
    this.name = 'BankDataException';
  }

  /**
   * Retorna os campos com erro
   */
  public getInvalidFields(): string[] {
    return this.details?.invalidFields as string[] || [];
  }
}

/**
 * Exceção para erros de permissão
 */
export class BankPermissionException extends BankException {
  constructor(
    bankName: string,
    message: string = 'Permissão negada',
    details?: Record<string, unknown>,
    errorCode?: string
  ) {
    super(message, bankName, 'AUTHENTICATION', 'high', details, errorCode);
    this.name = 'BankPermissionException';
  }

  /**
   * Retorna as permissões necessárias
   */
  public getRequiredPermissions(): string[] {
    return this.details?.requiredPermissions as string[] || [];
  }
}

/**
 * Exceção para erros de serviço indisponível
 */
export class BankServiceUnavailableException extends BankException {
  constructor(
    bankName: string,
    message: string = 'Serviço temporariamente indisponível',
    details?: Record<string, unknown>,
    errorCode?: string
  ) {
    super(message, bankName, 'CONNECTION', 'high', details, errorCode);
    this.name = 'BankServiceUnavailableException';
  }

  /**
   * Retorna o tempo estimado de indisponibilidade em minutos
   */
  public getEstimatedDowntime(): number {
    return this.details?.estimatedDowntime as number || 30; // Padrão: 30 minutos
  }
}

/**
 * Exceção para erros de processamento de pagamento
 */
export class BankPaymentException extends BankException {
  constructor(
    bankName: string,
    message: string = 'Erro no processamento do pagamento',
    details?: Record<string, unknown>,
    errorCode?: string
  ) {
    super(message, bankName, 'PAYMENT', 'high', details, errorCode);
    this.name = 'BankPaymentException';
  }

  /**
   * Retorna o ID da transação se disponível
   */
  public getTransactionId(): string | undefined {
    return this.details?.transactionId as string;
  }

  /**
   * Retorna o status do pagamento se disponível
   */
  public getPaymentStatus(): string | undefined {
    return this.details?.paymentStatus as string;
  }

  /**
   * Retorna o motivo da falha se disponível
   */
  public getFailureReason(): string | undefined {
    return this.details?.failureReason as string;
  }

  /**
   * Verifica se o pagamento pode ser retentado
   */
  public canRetry(): boolean {
    // Pagamentos podem ser retentados se:
    // 1. Não houver ID de transação (não foi processado)
    // 2. Status indica falha temporária
    // 3. Não for um erro de validação/dados
    const nonRetryableStatuses = ['REJECTED', 'INVALID', 'DUPLICATE'];
    const status = this.getPaymentStatus();
    
    return !this.getTransactionId() || 
           (status && !nonRetryableStatuses.includes(status));
  }

  /**
   * Verifica se o pagamento foi processado pelo banco
   */
  public wasProcessed(): boolean {
    return !!this.getTransactionId();
  }

  /**
   * Verifica se é necessário confirmar o status com o banco
   */
  public requiresStatusCheck(): boolean {
    const status = this.getPaymentStatus();
    return status === 'PENDING' || status === 'PROCESSING';
  }

  /**
   * Retorna instruções para resolução baseadas no erro
   */
  public getResolutionSteps(): string[] {
    const steps: string[] = [];
    const status = this.getPaymentStatus();
    const reason = this.getFailureReason();

    if (!this.wasProcessed()) {
      steps.push('Tentar processar o pagamento novamente');
    }

    if (status === 'REJECTED') {
      steps.push('Verificar os dados do pagamento');
      if (reason) {
        steps.push(`Corrigir o problema: ${reason}`);
      }
    }

    if (status === 'PENDING') {
      steps.push('Aguardar processamento pelo banco');
      steps.push('Verificar status em alguns minutos');
    }

    if (this.requiresStatusCheck()) {
      steps.push('Consultar status atual no banco');
    }

    if (steps.length === 0) {
      steps.push('Entrar em contato com o suporte do banco');
    }

    return steps;
  }
}
