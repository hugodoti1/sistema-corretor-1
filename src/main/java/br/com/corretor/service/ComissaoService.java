package br.com.corretor.service;

import br.com.corretor.dto.ComissaoDTO;
import br.com.corretor.dto.ComissaoPagamentoDTO;
import br.com.corretor.dto.ComissaoResumoDTO;
import br.com.corretor.model.Comissao;
import br.com.corretor.model.Venda;
import br.com.corretor.repository.ComissaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import br.com.corretor.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Service
public class ComissaoService {
    
    @Autowired
    private ComissaoRepository comissaoRepository;

    public Mono<Void> gerarComissao(Venda venda) {
        Comissao comissao = Comissao.criar(
            venda.getEmpresaId(),
            venda.getId(),
            venda.getCorretorId(),
            venda.getFabricaId(),
            venda.getDataVenda(),
            venda.getValorVenda(),
            venda.getPercentualComissao(),
            venda.getValorComissao(),
            calcularDataPrevisaoPagamento(venda.getDataVenda()),
            "PENDENTE",
            venda.getFormaPagamento(),
            venda.getNumeroNotaFiscal());
            
        return comissaoRepository.save(comissao).then();
    }

    public Mono<Void> atualizarComissao(Venda venda) {
        return comissaoRepository.findByVendaId(venda.getId())
            .flatMap(comissao -> {
                comissao.setValorVenda(venda.getValorVenda());
                comissao.setPercentualComissao(venda.getPercentualComissao());
                comissao.setValorComissao(venda.getValorComissao());
                comissao.setNumeroNotaFiscal(venda.getNumeroNotaFiscal());
                comissao.setFormaPagamento(venda.getFormaPagamento());
                return comissaoRepository.save(comissao);
            })
            .then();
    }

    public Mono<Void> excluirComissao(Venda venda) {
        return comissaoRepository.deleteByVendaId(venda.getId());
    }

    public Mono<ComissaoDTO> buscarPorId(Long id) {
        return comissaoRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Comissão não encontrada")))
            .map(this::toDTO);
    }

    public Flux<ComissaoDTO> buscarPorCorretor(Long corretorId) {
        return comissaoRepository.findByCorretorId(corretorId)
            .map(this::toDTO);
    }

    public Flux<ComissaoDTO> buscarPorFabrica(Long fabricaId) {
        return comissaoRepository.findByFabricaId(fabricaId)
            .map(this::toDTO);
    }

    public Flux<ComissaoDTO> buscarPorPeriodo(Long empresaId, LocalDate dataInicio, LocalDate dataFim) {
        return comissaoRepository.findByPeriodo(empresaId, dataInicio, dataFim)
            .map(this::toDTO);
    }

    public Flux<ComissaoDTO> buscarPorCorretorEPeriodo(Long empresaId, Long corretorId, LocalDate dataInicio, LocalDate dataFim) {
        return comissaoRepository.findByCorretorEPeriodo(empresaId, corretorId, dataInicio, dataFim)
            .map(this::toDTO);
    }

    public Flux<ComissaoDTO> buscarPendentes() {
        return comissaoRepository.findByStatus("PENDENTE")
            .map(this::toDTO);
    }

    public Flux<ComissaoDTO> buscarPagas() {
        return comissaoRepository.findByStatus("PAGO")
            .map(this::toDTO);
    }

    public Mono<Void> registrarPagamento(Long id, String formaPagamento, String observacoes) {
        return comissaoRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Comissão não encontrada")))
            .flatMap(comissao -> {
                if ("CANCELADO".equals(comissao.getStatus())) {
                    return Mono.error(new IllegalStateException("Não é possível pagar uma comissão cancelada"));
                }
                if ("PAGO".equals(comissao.getStatus())) {
                    return Mono.error(new IllegalStateException("Comissão já está paga"));
                }
                comissao.setStatus("PAGO");
                comissao.setDataPagamento(LocalDate.now());
                comissao.setFormaPagamento(formaPagamento);
                comissao.setObservacoes(observacoes);
                return comissaoRepository.save(comissao);
            })
            .then();
    }

    public Mono<ComissaoDTO> cancelar(Long id, String motivo) {
        return comissaoRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Comissão não encontrada")))
            .flatMap(comissao -> {
                if ("PAGO".equals(comissao.getStatus())) {
                    return Mono.error(new IllegalStateException("Não é possível cancelar uma comissão paga"));
                }
                comissao.setStatus("CANCELADO");
                comissao.setObservacoes(motivo);
                return comissaoRepository.save(comissao);
            })
            .map(this::toDTO);
    }

    public Flux<ComissaoDTO> listarComFiltros(
            Long empresaId,
            Long corretorId,
            Long fabricaId,
            LocalDate dataInicio,
            LocalDate dataFim,
            String status) {
        
        return comissaoRepository.findAll()
            .filter(comissao -> {
                boolean match = true;
                
                if (empresaId != null) {
                    match = match && comissao.getEmpresaId().equals(empresaId);
                }
                
                if (corretorId != null) {
                    match = match && comissao.getCorretorId().equals(corretorId);
                }
                
                if (fabricaId != null) {
                    match = match && comissao.getFabricaId().equals(fabricaId);
                }
                
                if (dataInicio != null) {
                    match = match && !comissao.getDataVenda().isBefore(dataInicio);
                }
                
                if (dataFim != null) {
                    match = match && !comissao.getDataVenda().isAfter(dataFim);
                }
                
                if (status != null && !status.isEmpty()) {
                    match = match && comissao.getStatus().equalsIgnoreCase(status);
                }
                
                return match;
            })
            .map(this::toDTO);
    }

    public Flux<ComissaoDTO> buscarVencidas(Long empresaId) {
        return comissaoRepository.findByEmpresaId(empresaId)
            .defaultIfEmpty(new Comissao())
            .filter(comissao -> 
                "PENDENTE".equals(comissao.getStatus()) && 
                comissao.getDataPrevisaoPagamento().isBefore(LocalDate.now()))
            .map(this::toDTO);
    }

 // No ComissaoService.java
    public Flux<ComissaoDTO> buscarAVencer(Long empresaId) {
        return comissaoRepository.findByEmpresaId(empresaId)
            .defaultIfEmpty(new Comissao())
            .filter(comissao -> 
                "PENDENTE".equals(comissao.getStatus()) && 
                comissao.getDataPrevisaoPagamento().isAfter(LocalDate.now()))
            .map(this::toDTO);
    }

    

    public Flux<ComissaoDTO> buscarPorPeriodoVenda(Long empresaId, LocalDate dataInicio, LocalDate dataFim) {
        return comissaoRepository.findAll()
            .filter(comissao -> 
                comissao.getEmpresaId().equals(empresaId) &&
                !comissao.getDataVenda().isBefore(dataInicio) &&
                !comissao.getDataVenda().isAfter(dataFim)
            )
            .map(this::toDTO);
    }

    public Flux<ComissaoDTO> buscarPorPeriodoVencimento(Long empresaId, LocalDate dataInicio, LocalDate dataFim) {
        return comissaoRepository.findAll()
            .filter(comissao -> 
                comissao.getEmpresaId().equals(empresaId) &&
                !comissao.getDataPrevisaoPagamento().isBefore(dataInicio) &&
                !comissao.getDataPrevisaoPagamento().isAfter(dataFim)
            )
            .map(this::toDTO);
    }

    public Flux<ComissaoDTO> buscarPorPeriodoPagamento(Long empresaId, LocalDate dataInicio, LocalDate dataFim) {
        return comissaoRepository.findAll()
            .filter(comissao -> 
                comissao.getEmpresaId().equals(empresaId) &&
                comissao.getDataPagamento() != null &&
                !comissao.getDataPagamento().isBefore(dataInicio) &&
                !comissao.getDataPagamento().isAfter(dataFim)
            )
            .map(this::toDTO);
    }

    public Mono<ComissaoDTO> registrarPagamento(Long id, ComissaoPagamentoDTO pagamentoDTO) {
        return comissaoRepository.findById(id)
            .flatMap(comissao -> {
                comissao.setStatus("PAGO");
                comissao.setDataPagamento(LocalDate.now());
                comissao.setFormaPagamento(pagamentoDTO.getFormaPagamento());
                comissao.setObservacoes(pagamentoDTO.getObservacoes());
                return comissaoRepository.save(comissao);
            })
            .map(this::toDTO);
    }

    public Mono<Void> cancelar(Long id) {
        return comissaoRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Comissão não encontrada")))
            .flatMap(comissao -> {
                if ("PAGO".equals(comissao.getStatus())) {
                    return Mono.error(new IllegalStateException("Não é possível cancelar uma comissão paga"));
                }
                comissao.setStatus("CANCELADO");
                return comissaoRepository.save(comissao);
            })
            .then();
    }
    
    public Flux<ComissaoDTO> buscarPorStatus(Long empresaId, String status) {
        return comissaoRepository.findAll()
            .filter(comissao -> 
                comissao.getEmpresaId().equals(empresaId) &&
                comissao.getStatus().equalsIgnoreCase(status)
            )
            .map(this::toDTO);
    }

    public Mono<ComissaoResumoDTO> obterResumo(Long empresaId, LocalDate dataInicio, LocalDate dataFim) {
        return comissaoRepository.findAll()
            .filter(comissao -> {
                boolean match = comissao.getEmpresaId().equals(empresaId);
                
                if (dataInicio != null) {
                    match = match && !comissao.getDataVenda().isBefore(dataInicio);
                }
                
                if (dataFim != null) {
                    match = match && !comissao.getDataVenda().isAfter(dataFim);
                }
                
                return match;
            })
            .collectList()
            .map(comissoes -> {
                ComissaoResumoDTO resumo = new ComissaoResumoDTO();
                resumo.setTotalComissoes((long) comissoes.size());
                resumo.setTotalPendente(comissoes.stream()
                    .filter(c -> "PENDENTE".equals(c.getStatus()))
                    .count());
                resumo.setTotalPago(comissoes.stream()
                    .filter(c -> "PAGO".equals(c.getStatus()))
                    .count());
                resumo.setTotalCancelado(comissoes.stream()
                    .filter(c -> "CANCELADO".equals(c.getStatus()))
                    .count());
                resumo.setValorTotalComissoes(comissoes.stream()
                    .map(Comissao::getValorComissao)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
                resumo.setValorTotalPendente(comissoes.stream()
                    .filter(c -> "PENDENTE".equals(c.getStatus()))
                    .map(Comissao::getValorComissao)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
                resumo.setValorTotalPago(comissoes.stream()
                    .filter(c -> "PAGO".equals(c.getStatus()))
                    .map(Comissao::getValorComissao)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
                return resumo;
            });
    }

    private LocalDate calcularDataPrevisaoPagamento(LocalDate dataVenda) {
        // Por padrão, a previsão de pagamento é no último dia do mês seguinte
        return dataVenda.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
    }

    private ComissaoDTO toDTO(Comissao comissao) {
        ComissaoDTO dto = new ComissaoDTO();
        dto.setId(comissao.getId());
        dto.setEmpresaId(comissao.getEmpresaId());
        dto.setVendaId(comissao.getVendaId());
        dto.setCorretorId(comissao.getCorretorId());
        dto.setFabricaId(comissao.getFabricaId());
        dto.setDataVenda(comissao.getDataVenda());
        dto.setValorVenda(comissao.getValorVenda());
        dto.setPercentualComissao(comissao.getPercentualComissao());
        dto.setValorComissao(comissao.getValorComissao());
        dto.setDataPrevisaoPagamento(comissao.getDataPrevisaoPagamento());
        dto.setDataPagamento(comissao.getDataPagamento());
        dto.setStatus(comissao.getStatus());
        dto.setFormaPagamento(comissao.getFormaPagamento());
        dto.setNumeroNotaFiscal(comissao.getNumeroNotaFiscal());
        dto.setObservacoes(comissao.getObservacoes());
        return dto;
    }
}
