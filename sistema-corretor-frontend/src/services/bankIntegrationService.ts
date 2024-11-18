import axios, { AxiosError, AxiosInstance, AxiosRequestConfig } from 'axios';
import { GlobalExceptionHandler } from '../exceptions/GlobalExceptionHandler';
import {
  BankAuthenticationException,
  BankConnectionException,
  BankDataException,
  BankPermissionException,
  BankRateLimitException,
  BankServiceUnavailableException,
  BankTimeoutException,
  BankValidationException
} from '../exceptions/BankException';
import { hasBankErrorCode } from '../types/bankErrorCodes';

/**
 * Interface para configuração da integração bancária
 */
export interface BankIntegrationConfig {
  baseURL: string;
  timeout?: number;
  headers?: Record<string, string>;
  retryAttempts?: number;
  retryDelay?: number;
}

/**
 * Serviço base para integração com bancos
 */
export abstract class BankIntegrationService {
  protected readonly bankName: string;
  protected readonly axios: AxiosInstance;
  protected readonly exceptionHandler: GlobalExceptionHandler;
  protected readonly config: BankIntegrationConfig;

  constructor(bankName: string, config: BankIntegrationConfig) {
    this.bankName = bankName;
    this.config = {
      timeout: 30000,
      retryAttempts: 3,
      retryDelay: 1000,
      ...config
    };

    this.axios = axios.create({
      baseURL: config.baseURL,
      timeout: this.config.timeout,
      headers: {
        'Content-Type': 'application/json',
        ...config.headers
      }
    });

    this.exceptionHandler = GlobalExceptionHandler.getInstance();

    // Adiciona interceptor para tratamento de erros
    this.axios.interceptors.response.use(
      response => response,
      error => this.handleAxiosError(error)
    );
  }

  /**
   * Realiza uma requisição HTTP com retry automático
   */
  protected async request<T>(config: AxiosRequestConfig): Promise<T> {
    let lastError: Error | null = null;
    let attempt = 0;

    while (attempt < (this.config.retryAttempts || 1)) {
      try {
        const response = await this.axios.request<T>(config);
        return response.data;
      } catch (error) {
        lastError = error as Error;
        
        // Se for um erro que não deve ser retentado, lança imediatamente
        if (!this.shouldRetry(error)) {
          throw error;
        }

        attempt++;
        if (attempt < (this.config.retryAttempts || 1)) {
          await this.delay(this.getRetryDelay(attempt));
        }
      }
    }

    throw lastError;
  }

  /**
   * Trata erros do Axios convertendo para exceções bancárias apropriadas
   */
  private async handleAxiosError(error: AxiosError): Promise<never> {
    const bankError = await this.mapToBankException(error);
    await this.exceptionHandler.handleException(bankError);
    throw bankError;
  }

  /**
   * Mapeia erros do Axios para exceções bancárias
   */
  private async mapToBankException(error: AxiosError): Promise<BankConnectionException> {
    // Extrai código de erro da resposta, se existir
    const errorCode = this.extractErrorCode(error);
    const errorDetails = this.extractErrorDetails(error);

    // Se for um código de erro conhecido do banco, usa ele
    if (errorCode && hasBankErrorCode(this.bankName, errorCode)) {
      return this.createBankException(error, errorCode, errorDetails);
    }

    // Caso contrário, mapeia baseado no erro HTTP
    if (!error.response) {
      return new BankConnectionException(
        this.bankName,
        'Erro de conexão com o banco',
        { originalError: error.message, ...errorDetails }
      );
    }

    switch (error.response.status) {
      case 401:
      case 403:
        return new BankAuthenticationException(
          this.bankName,
          'Erro de autenticação',
          { statusCode: error.response.status, ...errorDetails }
        );

      case 404:
        return new BankDataException(
          this.bankName,
          'Recurso não encontrado',
          { path: error.config?.url, ...errorDetails }
        );

      case 422:
        return new BankValidationException(
          this.bankName,
          'Dados inválidos',
          { validationErrors: error.response.data, ...errorDetails }
        );

      case 429:
        return new BankRateLimitException(
          this.bankName,
          'Limite de requisições excedido',
          {
            retryAfter: error.response.headers['retry-after'],
            ...errorDetails
          }
        );

      case 408:
        return new BankTimeoutException(
          this.bankName,
          'Tempo limite excedido',
          { timeout: this.config.timeout, ...errorDetails }
        );

      case 503:
        return new BankServiceUnavailableException(
          this.bankName,
          'Serviço temporariamente indisponível',
          errorDetails
        );

      default:
        return new BankConnectionException(
          this.bankName,
          'Erro na comunicação com o banco',
          {
            statusCode: error.response.status,
            statusText: error.response.statusText,
            ...errorDetails
          }
        );
    }
  }

  /**
   * Cria a exceção bancária apropriada baseada no código de erro
   */
  private createBankException(
    error: AxiosError,
    errorCode: string,
    details: Record<string, unknown>
  ): BankConnectionException {
    // A mensagem será obtida do mapeamento de códigos de erro
    return new BankConnectionException(
      this.bankName,
      undefined,
      details,
      errorCode
    );
  }

  /**
   * Extrai o código de erro da resposta
   */
  protected extractErrorCode(error: AxiosError): string | undefined {
    if (!error.response?.data) return undefined;

    // Implementação base - pode ser sobrescrita por bancos específicos
    const data = error.response.data as any;
    return data.errorCode || data.code || data.error_code;
  }

  /**
   * Extrai detalhes adicionais do erro
   */
  protected extractErrorDetails(error: AxiosError): Record<string, unknown> {
    const details: Record<string, unknown> = {};

    if (error.config) {
      details.method = error.config.method;
      details.url = error.config.url;
    }

    if (error.response?.data) {
      details.responseData = error.response.data;
    }

    return details;
  }

  /**
   * Verifica se deve tentar novamente após um erro
   */
  protected shouldRetry(error: unknown): boolean {
    if (error instanceof BankConnectionException) {
      // Não retenta erros de autenticação, validação, etc
      return !(
        error instanceof BankAuthenticationException ||
        error instanceof BankValidationException ||
        error instanceof BankPermissionException
      );
    }
    return true;
  }

  /**
   * Calcula o delay para retry com backoff exponencial
   */
  private getRetryDelay(attempt: number): number {
    const baseDelay = this.config.retryDelay || 1000;
    return baseDelay * Math.pow(2, attempt - 1);
  }

  /**
   * Utilitário para aguardar um tempo
   */
  private delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }
}
