package br.com.corretor.service.banco;

import br.com.corretor.dto.SaldoBancarioDTO;
import br.com.corretor.model.ContaBancaria;
import br.com.corretor.model.TransacaoBancaria;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Interface base para integração com diferentes bancos.
 * Implementa os métodos básicos necessários para comunicação com APIs bancárias.
 */
public interface IntegracaoBancariaBase {
    
    /**
     * Obtém um novo token de acesso para a conta bancária.
     * @param conta Conta bancária para obter o token
     * @return Novo token de acesso
     */
    String obterNovoToken(ContaBancaria conta);

    /**
     * Obtém o saldo atual da conta bancária.
     * @param conta Conta bancária para consultar
     * @return DTO com informações do saldo
     */
    SaldoBancarioDTO obterSaldo(ContaBancaria conta);

    /**
     * Obtém as transações da conta em um período específico.
     * @param conta Conta bancária para consultar
     * @param dataInicio Data inicial do período
     * @param dataFim Data final do período
     * @return Lista de transações bancárias
     */
    List<TransacaoBancaria> obterTransacoes(ContaBancaria conta, LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Registra uma URL de webhook para receber notificações do banco.
     * @param conta Conta bancária para registrar o webhook
     * @param webhookUrl URL que receberá as notificações
     */
    void registrarWebhook(ContaBancaria conta, String webhookUrl);

    /**
     * Valida se o token de acesso atual da conta ainda é válido.
     * @param conta Conta bancária para validar
     * @return true se o token é válido, false caso contrário
     */
    boolean validarToken(ContaBancaria conta);

    /**
     * Processa notificações recebidas via webhook.
     * @param payload Dados da notificação recebida
     */
    void processarWebhook(Map<String, Object> payload);

    /**
     * Verifica se uma conta bancária está ativa.
     * @param conta Conta bancária para verificar
     * @return true se a conta está ativa, false caso contrário
     */
    boolean verificarContaAtiva(ContaBancaria conta);

    /**
     * Obtém detalhes atualizados de uma conta bancária.
     * @param agencia Número da agência
     * @param conta Número da conta
     * @return Objeto ContaBancaria com detalhes atualizados
     */
    ContaBancaria obterDetalhesConta(String agencia, String conta);

    /**
     * Obtém o código do banco (exemplo: 001 para Banco do Brasil, 341 para Itaú).
     * @return Código do banco
     */
    String getCodigoBanco();

    /**
     * Obtém o nome do banco.
     * @return Nome do banco
     */
    String getNomeBanco();

    /**
     * Obtém o tipo de autenticação utilizado pelo banco.
     * @return Tipo de autenticação
     */
    TipoAutenticacao getTipoAutenticacao();

    /**
     * Obtém os recursos suportados por esta integração bancária.
     * @return Lista de recursos suportados
     */
    List<RecursoBancario> getRecursosSuportados();

    /**
     * Enum que define os tipos de autenticação suportados.
     */
    enum TipoAutenticacao {
        OAUTH2,
        CERTIFICADO_DIGITAL,
        TOKEN_ESTATICO,
        CHAVE_API
    }

    /**
     * Enum que define os recursos que podem ser suportados por um banco.
     */
    enum RecursoBancario {
        SALDO,
        EXTRATO,
        WEBHOOK,
        PAGAMENTO,
        TRANSFERENCIA,
        PIX,
        BOLETO
    }
}
