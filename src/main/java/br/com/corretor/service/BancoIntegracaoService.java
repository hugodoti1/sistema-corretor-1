package br.com.corretor.service;

import br.com.corretor.dto.SaldoBancarioDTO;
import br.com.corretor.enums.TipoBanco;
import br.com.corretor.exception.BancoIntegracaoException;
import br.com.corretor.exception.banco.BBException;
import br.com.corretor.exception.banco.ItauException;
import br.com.corretor.model.ContaBancaria;
import br.com.corretor.model.TransacaoBancaria;
import br.com.corretor.repository.ContaBancariaRepository;
import br.com.corretor.repository.TransacaoBancariaRepository;
import br.com.corretor.service.banco.IntegracaoBancariaBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BancoIntegracaoService {

    private final Map<TipoBanco, IntegracaoBancariaBase> integracoesBancarias;
    private final ContaBancariaRepository contaBancariaRepository;
    private final TransacaoBancariaRepository transacaoBancariaRepository;

    @Transactional
    public List<TransacaoBancaria> sincronizarTransacoes(ContaBancaria conta) {
        try {
            IntegracaoBancariaBase integracao = obterIntegracaoBancaria(conta);
            validarEAtualizarToken(conta, integracao);

            LocalDateTime dataInicio = conta.getUltimaSincronizacao() != null ? 
                conta.getUltimaSincronizacao() : 
                LocalDateTime.now().minusDays(30);

            List<TransacaoBancaria> transacoes = integracao.obterTransacoes(
                conta,
                dataInicio,
                LocalDateTime.now()
            );

            conta.setUltimaSincronizacao(LocalDateTime.now());
            contaBancariaRepository.save(conta);

            // Salva as novas transações
            transacoes.forEach(t -> {
                t.setContaBancaria(conta);
                transacaoBancariaRepository.save(t);
            });

            return transacoes;
        } catch (Exception e) {
            log.error("Erro ao sincronizar transações da conta {}: {}", conta.getConta(), e.getMessage());
            throw traduzirExcecao(e, conta.getBanco());
        }
    }

    @Transactional
    public void registrarWebhook(ContaBancaria conta, String webhookUrl) {
        try {
            IntegracaoBancariaBase integracao = obterIntegracaoBancaria(conta);
            validarEAtualizarToken(conta, integracao);
            integracao.registrarWebhook(conta, webhookUrl);
        } catch (Exception e) {
            log.error("Erro ao registrar webhook para a conta {}: {}", conta.getConta(), e.getMessage());
            throw traduzirExcecao(e, conta.getBanco());
        }
    }

    @Transactional
    public SaldoBancarioDTO atualizarSaldo(ContaBancaria conta) {
        try {
            IntegracaoBancariaBase integracao = obterIntegracaoBancaria(conta);
            validarEAtualizarToken(conta, integracao);

            SaldoBancarioDTO saldo = integracao.obterSaldo(conta);
            conta.setSaldo(saldo.getSaldo());
            conta.setUltimaAtualizacaoSaldo(LocalDateTime.now());
            contaBancariaRepository.save(conta);

            return saldo;
        } catch (Exception e) {
            log.error("Erro ao atualizar saldo da conta {}: {}", conta.getConta(), e.getMessage());
            throw traduzirExcecao(e, conta.getBanco());
        }
    }

    @Transactional
    public void processarWebhook(String bancoId, Map<String, Object> payload) {
        try {
            TipoBanco tipoBanco = TipoBanco.fromCodigo(bancoId);
            IntegracaoBancariaBase integracao = integracoesBancarias.get(tipoBanco);
            
            if (integracao == null) {
                throw new BancoIntegracaoException(
                    "Integração não implementada para o banco: " + tipoBanco,
                    "BANCO_NAO_SUPORTADO",
                    bancoId,
                    ErrorType.BANCO_NAO_SUPORTADO
                );
            }

            integracao.processarWebhook(payload);
        } catch (Exception e) {
            log.error("Erro ao processar webhook do banco {}: {}", bancoId, e.getMessage());
            throw traduzirExcecao(e, bancoId);
        }
    }

    @Transactional
    public void conciliarTransacao(Long transacaoBancariaId, Long transacaoSistemaId) {
        try {
            TransacaoBancaria transacaoBancaria = transacaoBancariaRepository.findById(transacaoBancariaId)
                .orElseThrow(() -> new BancoIntegracaoException(
                    "Transação bancária não encontrada",
                    "TRANSACAO_NAO_ENCONTRADA",
                    null,
                    ErrorType.DADOS_INVALIDOS
                ));

            transacaoBancaria.setTransacaoSistemaId(transacaoSistemaId);
            transacaoBancaria.setDataConciliacao(LocalDateTime.now());
            transacaoBancariaRepository.save(transacaoBancaria);
        } catch (Exception e) {
            log.error("Erro ao conciliar transação {}: {}", transacaoBancariaId, e.getMessage());
            throw traduzirExcecao(e, null);
        }
    }

    @Transactional
    public void desconciliarTransacao(Long transacaoBancariaId) {
        try {
            TransacaoBancaria transacaoBancaria = transacaoBancariaRepository.findById(transacaoBancariaId)
                .orElseThrow(() -> new BancoIntegracaoException(
                    "Transação bancária não encontrada",
                    "TRANSACAO_NAO_ENCONTRADA",
                    null,
                    ErrorType.DADOS_INVALIDOS
                ));

            transacaoBancaria.setTransacaoSistemaId(null);
            transacaoBancaria.setDataConciliacao(null);
            transacaoBancariaRepository.save(transacaoBancaria);
        } catch (Exception e) {
            log.error("Erro ao desconciliar transação {}: {}", transacaoBancariaId, e.getMessage());
            throw traduzirExcecao(e, null);
        }
    }

    public boolean verificarStatusConta(ContaBancaria conta) {
        try {
            IntegracaoBancariaBase integracao = obterIntegracaoBancaria(conta);
            validarEAtualizarToken(conta, integracao);
            return integracao.verificarContaAtiva(conta);
        } catch (Exception e) {
            log.error("Erro ao verificar status da conta {}: {}", conta.getConta(), e.getMessage());
            throw traduzirExcecao(e, conta.getBanco());
        }
    }

    public ContaBancaria obterDetalhesConta(ContaBancaria conta) {
        try {
            IntegracaoBancariaBase integracao = obterIntegracaoBancaria(conta);
            validarEAtualizarToken(conta, integracao);
            
            ContaBancaria detalhes = integracao.obterDetalhesConta(conta.getAgencia(), conta.getConta());
            detalhes.setId(conta.getId()); // Mantém o ID original
            
            return detalhes;
        } catch (Exception e) {
            log.error("Erro ao obter detalhes da conta {}: {}", conta.getConta(), e.getMessage());
            throw traduzirExcecao(e, conta.getBanco());
        }
    }

    private void validarEAtualizarToken(ContaBancaria conta, IntegracaoBancariaBase integracao) {
        if (!integracao.validarToken(conta)) {
            String novoToken = integracao.obterNovoToken(conta);
            conta.setTokenAcesso(novoToken);
            contaBancariaRepository.save(conta);
        }
    }

    private IntegracaoBancariaBase obterIntegracaoBancaria(ContaBancaria conta) {
        TipoBanco tipoBanco = TipoBanco.fromCodigo(conta.getBanco());
        IntegracaoBancariaBase integracao = integracoesBancarias.get(tipoBanco);
        
        if (integracao == null) {
            throw new BancoIntegracaoException(
                "Integração não implementada para o banco: " + tipoBanco,
                "BANCO_NAO_SUPORTADO",
                conta.getBanco(),
                ErrorType.BANCO_NAO_SUPORTADO
            );
        }
        
        return integracao;
    }

    private RuntimeException traduzirExcecao(Exception e, String bancoId) {
        if (e instanceof BBException || e instanceof ItauException || e instanceof BancoIntegracaoException) {
            return (RuntimeException) e;
        }

        // Traduz exceções genéricas para exceções específicas do banco
        if ("001".equals(bancoId)) {
            return new BBException(
                "Erro interno ao processar transação BB: " + e.getMessage(),
                BBException.BBErrorCode.BB999,
                bancoId
            );
        } else if ("341".equals(bancoId)) {
            return new ItauException(
                "Erro interno ao processar transação Itaú: " + e.getMessage(),
                ItauException.ItauErrorCode.ITAU999,
                bancoId
            );
        }

        return new BancoIntegracaoException(
            "Erro não esperado: " + e.getMessage(),
            "ERRO_INTERNO",
            bancoId,
            ErrorType.ERRO_INTERNO
        );
    }
}
