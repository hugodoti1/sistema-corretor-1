import { AxiosError } from 'axios';
import { BankIntegrationService, BankIntegrationConfig } from './bankIntegrationService';

/**
 * Configuração específica para o Banco do Brasil
 */
interface BBConfig extends BankIntegrationConfig {
  chaveJ: string;
  certificado?: string;
}

/**
 * Serviço de integração com o Banco do Brasil
 */
export class BancoDoBrasilService extends BankIntegrationService {
  private readonly bbConfig: BBConfig;

  constructor(config: BBConfig) {
    super('Banco do Brasil', {
      ...config,
      headers: {
        ...config.headers,
        'X-Chave-J': config.chaveJ,
        ...(config.certificado && { 'X-Certificado': config.certificado })
      }
    });

    this.bbConfig = config;
  }

  /**
   * Extrai o código de erro específico do BB
   */
  protected override extractErrorCode(error: AxiosError): string | undefined {
    if (!error.response?.data) return undefined;

    const data = error.response.data as any;
    
    // BB retorna códigos de erro em diferentes formatos
    return (
      data.codigoErro || // Formato novo
      data.error?.code || // Formato legado
      data.erro?.codigo // Formato alternativo
    );
  }

  /**
   * Extrai detalhes adicionais específicos do BB
   */
  protected override extractErrorDetails(error: AxiosError): Record<string, unknown> {
    const details = super.extractErrorDetails(error);

    if (error.response?.data) {
      const data = error.response.data as any;
      
      // Adiciona campos específicos do BB
      if (data.erro?.detalhes) {
        details.detalhesErro = data.erro.detalhes;
      }
      if (data.erro?.origem) {
        details.origemErro = data.erro.origem;
      }
      if (data.timestamp) {
        details.timestampBanco = data.timestamp;
      }
    }

    return details;
  }

  /**
   * Consulta saldo de uma conta
   */
  public async consultarSaldo(agencia: string, conta: string): Promise<number> {
    try {
      const response = await this.request<{ saldo: number }>({
        method: 'GET',
        url: `/contas/${agencia}-${conta}/saldo`
      });

      return response.saldo;
    } catch (error) {
      // O erro já será tratado pelo interceptor
      throw error;
    }
  }

  /**
   * Realiza uma transferência
   */
  public async realizarTransferencia(
    origem: { agencia: string; conta: string },
    destino: { agencia: string; conta: string },
    valor: number
  ): Promise<string> {
    try {
      const response = await this.request<{ protocolo: string }>({
        method: 'POST',
        url: '/transferencias',
        data: {
          contaOrigem: `${origem.agencia}-${origem.conta}`,
          contaDestino: `${destino.agencia}-${destino.conta}`,
          valor
        }
      });

      return response.protocolo;
    } catch (error) {
      // O erro já será tratado pelo interceptor
      throw error;
    }
  }

  /**
   * Consulta extrato do período
   */
  public async consultarExtrato(
    agencia: string,
    conta: string,
    dataInicio: Date,
    dataFim: Date
  ): Promise<Array<{ data: string; valor: number; descricao: string }>> {
    try {
      return await this.request({
        method: 'GET',
        url: `/contas/${agencia}-${conta}/extrato`,
        params: {
          dataInicio: dataInicio.toISOString().split('T')[0],
          dataFim: dataFim.toISOString().split('T')[0]
        }
      });
    } catch (error) {
      // O erro já será tratado pelo interceptor
      throw error;
    }
  }

  /**
   * Verifica status do serviço
   */
  public async verificarStatus(): Promise<boolean> {
    try {
      await this.request({
        method: 'GET',
        url: '/status'
      });
      return true;
    } catch (error) {
      if (error instanceof Error) {
        // Ignora erros de autenticação no health check
        return false;
      }
      throw error;
    }
  }
}
