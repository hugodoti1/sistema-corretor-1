package br.com.corretor.service;

import br.com.corretor.annotation.Audited;
import br.com.corretor.config.CacheConfig;
import br.com.corretor.model.Banco;
import br.com.corretor.model.Conciliacao;
import br.com.corretor.model.Transacao;
import br.com.corretor.repository.BancoRepository;
import br.com.corretor.repository.ConciliacaoRepository;
import br.com.corretor.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConciliacaoService {

    private final TransacaoRepository transacaoRepository;
    private final ConciliacaoRepository conciliacaoRepository;
    private final BancoRepository bancoRepository;
    private final AuditoriaService auditoriaService;

    @Audited(
        action = "BUSCAR_TRANSACOES",
        resourceType = "TRANSACAO",
        resourceIdParam = "empresaId"
    )
    @Cacheable(value = CacheConfig.TRANSACOES_CACHE, key = "'transacoes_' + #empresaId + '_' + #bancoId + '_' + #dataInicio + '_' + #dataFim")
    public List<Transacao> buscarTransacoesPendentes(Long empresaId, Long bancoId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return LogUtil.logOperacaoComRetorno(log, "BUSCAR_TRANSACOES", "TRANSACAO", empresaId.toString(),
            () -> transacaoRepository.findByEmpresaIdAndBancoIdAndDataTransacaoBetweenAndConciliadaFalse(
                    empresaId, bancoId, dataInicio, dataFim));
    }

    @Audited(
        action = "BUSCAR_CONCILIACOES",
        resourceType = "CONCILIACAO",
        resourceIdParam = "empresaId"
    )
    @Cacheable(value = CacheConfig.CONCILIACOES_CACHE, key = "'conciliacoes_' + #empresaId + '_' + #bancoId + '_' + #dataInicio + '_' + #dataFim")
    public List<Conciliacao> buscarConciliacoes(Long empresaId, Long bancoId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return LogUtil.logOperacaoComRetorno(log, "BUSCAR_CONCILIACOES", "CONCILIACAO", empresaId.toString(),
            () -> conciliacaoRepository.findByEmpresaIdAndBancoIdAndDataInicioBetweenAndDataFimBetween(
                    empresaId, bancoId, dataInicio, dataFim));
    }

    @Audited(
        action = "INICIAR_CONCILIACAO",
        resourceType = "CONCILIACAO",
        resourceIdParam = "empresaId"
    )
    @Transactional
    @CacheEvict(value = {CacheConfig.TRANSACOES_CACHE, CacheConfig.CONCILIACOES_CACHE}, 
                key = "'*_' + #empresaId + '_' + #bancoId + '_*'", 
                allEntries = true)
    public Conciliacao iniciarConciliacao(Long empresaId, Long bancoId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        LogUtil.logOperacao(log, "INICIAR_CONCILIACAO", "CONCILIACAO", empresaId.toString(),
            () -> {
                Banco banco = bancoRepository.findById(bancoId)
                        .orElseThrow(() -> new IllegalArgumentException("Banco não encontrado"));

                // Criar registro de conciliação
                Conciliacao conciliacao = new Conciliacao();
                conciliacao.setEmpresaId(empresaId);
                conciliacao.setBancoId(bancoId);
                conciliacao.setDataInicio(dataInicio);
                conciliacao.setDataFim(dataFim);
                conciliacao.setConcluida(false);
                
                conciliacao = conciliacaoRepository.save(conciliacao);
                
                auditoriaService.registrarAcao("CONCILIACAO", "INICIO", 
                    String.format("Iniciada conciliação para empresa %d, banco %d, período: %s a %s", 
                        empresaId, bancoId, dataInicio, dataFim));

                return conciliacao;
            });
    }

    @Audited(
        action = "PROCESSAR_CONCILIACAO",
        resourceType = "CONCILIACAO",
        resourceIdParam = "conciliacaoId"
    )
    @Transactional
    @CacheEvict(value = {CacheConfig.TRANSACOES_CACHE, CacheConfig.CONCILIACOES_CACHE}, 
                key = "'*_' + #conciliacaoId + '_*'", 
                allEntries = true)
    public void processarConciliacao(Long conciliacaoId) {
        Conciliacao conciliacao = conciliacaoRepository.findById(conciliacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Conciliação não encontrada"));

        if (conciliacao.isConcluida()) {
            throw new IllegalStateException("Conciliação já foi concluída");
        }

        LogUtil.logOperacao(log, "PROCESSAR_CONCILIACAO", "CONCILIACAO", conciliacaoId.toString(),
            () -> {
                // Buscar transações do período
                List<Transacao> transacoes = transacaoRepository.findByEmpresaIdAndBancoIdAndDataTransacaoBetween(
                        conciliacao.getEmpresaId(),
                        conciliacao.getBancoId(),
                        conciliacao.getDataInicio(),
                        conciliacao.getDataFim()
                );

                // Agrupar transações por ID da transação no banco
                Map<String, List<Transacao>> transacoesPorIdBanco = transacoes.stream()
                        .filter(t -> t.getIdTransacaoBanco() != null)
                        .collect(Collectors.groupingBy(Transacao::getIdTransacaoBanco));

                int totalTransacoes = transacoes.size();
                int transacoesConciliadas = 0;

                // Processar duplicidades
                for (List<Transacao> grupoTransacoes : transacoesPorIdBanco.values()) {
                    if (grupoTransacoes.size() > 1) {
                        // Marcar transações duplicadas
                        for (Transacao transacao : grupoTransacoes) {
                            transacao.setConciliada(true);
                            transacao.setDataConciliacao(LocalDateTime.now());
                            transacaoRepository.save(transacao);
                            transacoesConciliadas++;
                        }

                        auditoriaService.registrarAcao("CONCILIACAO", "DUPLICIDADE",
                            String.format("Encontradas %d transações duplicadas com ID banco: %s", 
                                grupoTransacoes.size(), grupoTransacoes.get(0).getIdTransacaoBanco()));
                    }
                }

                // Processar transações por valor e data próxima
                List<Transacao> transacoesNaoConciliadas = transacoes.stream()
                        .filter(t -> !t.isConciliada())
                        .collect(Collectors.toList());

                for (Transacao t1 : transacoesNaoConciliadas) {
                    if (t1.isConciliada()) continue;

                    for (Transacao t2 : transacoesNaoConciliadas) {
                        if (t1 == t2 || t2.isConciliada()) continue;

                        if (transacoesSimilares(t1, t2)) {
                            t1.setConciliada(true);
                            t2.setConciliada(true);
                            t1.setDataConciliacao(LocalDateTime.now());
                            t2.setDataConciliacao(LocalDateTime.now());
                            
                            transacaoRepository.save(t1);
                            transacaoRepository.save(t2);
                            transacoesConciliadas += 2;

                            auditoriaService.registrarAcao("CONCILIACAO", "MATCH",
                                String.format("Transações conciliadas por similaridade: %s e %s", 
                                    t1.getId(), t2.getId()));
                        }
                    }
                }

                // Atualizar status da conciliação
                conciliacao.setTotalTransacoes(totalTransacoes);
                conciliacao.setTransacoesConciliadas(transacoesConciliadas);
                conciliacao.setTransacoesPendentes(totalTransacoes - transacoesConciliadas);
                conciliacao.setConcluida(true);
                conciliacao.setDataConclusao(LocalDateTime.now());
                
                conciliacaoRepository.save(conciliacao);

                auditoriaService.registrarAcao("CONCILIACAO", "CONCLUSAO",
                    String.format("Conciliação %d concluída. Total: %d, Conciliadas: %d, Pendentes: %d",
                        conciliacaoId, totalTransacoes, transacoesConciliadas, 
                        totalTransacoes - transacoesConciliadas));
            });
    }

    @Audited(
        action = "CALCULAR_SALDO",
        resourceType = "SALDO",
        resourceIdParam = "empresaId"
    )
    @Cacheable(value = CacheConfig.SALDOS_CACHE, key = "'saldo_conciliado_' + #empresaId + '_' + #bancoId")
    public BigDecimal calcularSaldoConciliado(Long empresaId, Long bancoId) {
        return LogUtil.logOperacaoComRetorno(log, "CALCULAR_SALDO", "SALDO", empresaId.toString(),
            () -> {
                List<Transacao> transacoes = buscarTransacoesConciliadas(empresaId, bancoId, LocalDateTime.now().minusDays(30), LocalDateTime.now());
                
                return transacoes.stream()
                        .map(t -> t.getTipo().equals("CREDITO") ? t.getValor() : t.getValor().negate())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            });
    }

    private boolean transacoesSimilares(Transacao t1, Transacao t2) {
        // Verifica se as transações têm o mesmo valor
        if (!t1.getValor().equals(t2.getValor())) {
            return false;
        }

        // Verifica se as transações são do mesmo tipo (crédito/débito)
        if (!t1.getTipo().equals(t2.getTipo())) {
            return false;
        }

        // Verifica se as transações ocorreram em um intervalo de 1 dia
        return Math.abs(t1.getDataTransacao().until(t2.getDataTransacao()).toDays()) <= 1;
    }
}
