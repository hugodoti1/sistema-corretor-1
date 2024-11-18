package br.com.corretor.integration;

import br.com.corretor.model.ContaBancaria;
import br.com.corretor.model.TransacaoBancaria;
import br.com.corretor.dto.SaldoBancarioDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface BancoIntegrationAPI {
    
    /**
     * Obtém o saldo atual da conta
     */
    SaldoBancarioDTO obterSaldo(ContaBancaria conta);
    
    /**
     * Busca o extrato de transações no período especificado
     */
    List<TransacaoBancaria> buscarTransacoes(ContaBancaria conta, LocalDateTime inicio, LocalDateTime fim);
    
    /**
     * Verifica se a conta está ativa
     */
    boolean verificarContaAtiva(ContaBancaria conta);
    
    /**
     * Obtém os detalhes da conta
     */
    ContaBancaria obterDetalhesConta(String agencia, String conta);
    
    /**
     * Retorna o nome do banco
     */
    String getNomeBanco();
    
    /**
     * Retorna o código do banco
     */
    String getCodigoBanco();
}
