package br.com.corretor.service;

import br.com.corretor.dto.VendaDTO;
import br.com.corretor.model.Venda;
import br.com.corretor.repository.VendaRepository;
import br.com.corretor.repository.FabricaRepository;
import br.com.corretor.repository.ClienteRepository;
import br.com.corretor.repository.CorretorRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class VendaService {

	@Autowired
	private VendaRepository vendaRepository;
    
	@Autowired
	private FabricaRepository fabricaRepository;
    
	@Autowired
	private ClienteRepository clienteRepository;
    
	@Autowired
	private CorretorRepository corretorRepository;
    
	@Autowired
	private ComissaoService comissaoService;

    @Transactional
    public Mono<VendaDTO> criar(VendaDTO vendaDTO) {
        return Mono.zip(
                fabricaRepository.findById(vendaDTO.getFabricaId()),
                clienteRepository.findById(vendaDTO.getClienteId()),
                corretorRepository.findById(vendaDTO.getCorretorId())
            )
            .flatMap(tuple -> {
                var fabrica = tuple.getT1();
                var cliente = tuple.getT2();
                var corretor = tuple.getT3();

                // Validar e obter percentual de comissão da fábrica baseado no tipo de venda
                BigDecimal percentualComissao = fabrica.getPercentualComissaoPorTipoVenda(vendaDTO.getTipoVenda());
                
                // Calcular valor da comissão
                BigDecimal valorComissao = vendaDTO.getValorVenda()
                    .multiply(percentualComissao)
                    .divide(new BigDecimal("100"));

                Venda venda = new Venda();
                venda.setEmpresaId(vendaDTO.getEmpresaId());
                venda.setDataVenda(vendaDTO.getDataVenda() != null ? vendaDTO.getDataVenda() : LocalDate.now());
                venda.setFabricaId(fabrica.getId());
                venda.setClienteId(cliente.getId());
                venda.setCorretorId(corretor.getId());
                venda.setTipoVenda(vendaDTO.getTipoVenda());
                venda.setEvento(vendaDTO.getEvento());
                venda.setValorVenda(vendaDTO.getValorVenda());
                venda.setNumeroNotaFiscal(vendaDTO.getNumeroNotaFiscal());
                venda.setPercentualComissao(percentualComissao);
                venda.setValorComissao(valorComissao);
                venda.setFormaPagamento(vendaDTO.getFormaPagamento());
                venda.setQuantidadeParcelas(vendaDTO.getQuantidadeParcelas());

                return vendaRepository.save(venda)
                    .flatMap(vendaSalva -> {
                        // Gerar comissão a receber
                        return comissaoService.gerarComissao(vendaSalva)
                            .thenReturn(toDTO(vendaSalva));
                    });
            });
    }

    @Transactional
    public Mono<VendaDTO> atualizar(Long id, VendaDTO vendaDTO) {
        return vendaRepository.findById(id)
            .flatMap(venda -> {
                return Mono.zip(
                    fabricaRepository.findById(vendaDTO.getFabricaId()),
                    clienteRepository.findById(vendaDTO.getClienteId()),
                    corretorRepository.findById(vendaDTO.getCorretorId())
                )
                .flatMap(tuple -> {
                    var fabrica = tuple.getT1();
                    
                    // Atualizar campos
                    BigDecimal percentualComissao = fabrica.getPercentualComissaoPorTipoVenda(vendaDTO.getTipoVenda());
                    BigDecimal valorComissao = vendaDTO.getValorVenda()
                        .multiply(percentualComissao)
                        .divide(new BigDecimal("100"));

                    venda.setDataVenda(vendaDTO.getDataVenda());
                    venda.setFabricaId(vendaDTO.getFabricaId());
                    venda.setClienteId(vendaDTO.getClienteId());
                    venda.setCorretorId(vendaDTO.getCorretorId());
                    venda.setTipoVenda(vendaDTO.getTipoVenda());
                    venda.setEvento(vendaDTO.getEvento());
                    venda.setValorVenda(vendaDTO.getValorVenda());
                    venda.setNumeroNotaFiscal(vendaDTO.getNumeroNotaFiscal());
                    venda.setPercentualComissao(percentualComissao);
                    venda.setValorComissao(valorComissao);
                    venda.setFormaPagamento(vendaDTO.getFormaPagamento());
                    venda.setQuantidadeParcelas(vendaDTO.getQuantidadeParcelas());

                    return vendaRepository.save(venda)
                        .flatMap(vendaAtualizada -> {
                            // Atualizar comissão
                            return comissaoService.atualizarComissao(vendaAtualizada)
                                .thenReturn(toDTO(vendaAtualizada));
                        });
                });
            });
    }

    @Transactional
    public Mono<Void> excluir(Long id) {
        return vendaRepository.findById(id)
            .flatMap(venda -> {
                return comissaoService.excluirComissao(venda)
                    .then(vendaRepository.delete(venda));
            });
    }

    public Mono<VendaDTO> buscarPorId(Long id) {
        return vendaRepository.findById(id)
            .map(this::toDTO);
    }

    public Flux<VendaDTO> listarComFiltros(
            Long empresaId, Long clienteId, Long fabricaId, String cnpj,
            String cidade, String estado, LocalDate dataInicio, LocalDate dataFim,
            BigDecimal valorMinimo, BigDecimal valorMaximo, String numeroNotaFiscal) {
        
        return vendaRepository.findByEmpresaId(empresaId)
            .filterWhen(venda -> {
                // Verificar filtros básicos primeiro
                if (clienteId != null && !venda.getClienteId().equals(clienteId)) {
                    return Mono.just(false);
                }
                if (fabricaId != null && !venda.getFabricaId().equals(fabricaId)) {
                    return Mono.just(false);
                }
                if (dataInicio != null && venda.getDataVenda().isBefore(dataInicio)) {
                    return Mono.just(false);
                }
                if (dataFim != null && venda.getDataVenda().isAfter(dataFim)) {
                    return Mono.just(false);
                }
                if (valorMinimo != null && venda.getValorVenda().compareTo(valorMinimo) < 0) {
                    return Mono.just(false);
                }
                if (valorMaximo != null && venda.getValorVenda().compareTo(valorMaximo) > 0) {
                    return Mono.just(false);
                }
                if (numeroNotaFiscal != null && !numeroNotaFiscal.equals(venda.getNumeroNotaFiscal())) {
                    return Mono.just(false);
                }

                // Se não precisar verificar dados do cliente, retorna true
                if (cnpj == null && cidade == null && estado == null) {
                    return Mono.just(true);
                }

                // Verificar filtros que precisam consultar o cliente
                return clienteRepository.findById(venda.getClienteId())
                    .map(cliente -> {
                        if (cnpj != null && !cnpj.equals(cliente.getCnpj())) {
                            return false;
                        }
                        if (cidade != null && !cidade.equals(cliente.getCidade())) {
                            return false;
                        }
                        if (estado != null && !estado.equals(cliente.getEstado())) {
                            return false;
                        }
                        return true;
                    })
                    .defaultIfEmpty(false);
            })
            .map(this::toDTO);
    }

    public Flux<VendaDTO> buscarPorCliente(Long empresaId, Long clienteId) {
        return vendaRepository.findByEmpresaIdAndClienteId(empresaId, clienteId)
            .map(this::toDTO);
    }

    public Flux<VendaDTO> buscarPorFabrica(Long empresaId, Long fabricaId) {
        return vendaRepository.findByEmpresaIdAndFabricaId(empresaId, fabricaId)
            .map(this::toDTO);
    }

    public Flux<VendaDTO> buscarPorCorretor(Long empresaId, Long corretorId) {
        return vendaRepository.findByEmpresaIdAndCorretorId(empresaId, corretorId)
            .map(this::toDTO);
    }

    public Flux<VendaDTO> buscarPorTipoVenda(Long empresaId, String tipoVenda) {
        return vendaRepository.findByTipoVenda(empresaId, tipoVenda)
            .map(this::toDTO);
    }

    public Flux<VendaDTO> buscarPorEvento(Long empresaId, String evento) {
        return vendaRepository.findByEvento(empresaId, evento)
            .map(this::toDTO);
    }

    public Flux<VendaDTO> buscarPorFormaPagamento(Long empresaId, String formaPagamento) {
        return vendaRepository.findByFormaPagamento(empresaId, formaPagamento)
            .map(this::toDTO);
    }

    public Flux<VendaDTO> buscarFaturadas(Long empresaId) {
        return vendaRepository.findFaturadas(empresaId)
            .map(this::toDTO);
    }

    public Flux<VendaDTO> buscarNaoFaturadas(Long empresaId) {
        return vendaRepository.findNaoFaturadas(empresaId)
            .map(this::toDTO);
    }

    private VendaDTO toDTO(Venda venda) {
        return VendaDTO.builder()
            .id(venda.getId())
            .empresaId(venda.getEmpresaId())
            .dataVenda(venda.getDataVenda())
            .fabricaId(venda.getFabricaId())
            .clienteId(venda.getClienteId())
            .corretorId(venda.getCorretorId())
            .tipoVenda(venda.getTipoVenda())
            .evento(venda.getEvento())
            .valorVenda(venda.getValorVenda())
            .numeroNotaFiscal(venda.getNumeroNotaFiscal())
            .percentualComissao(venda.getPercentualComissao())
            .valorComissao(venda.getValorComissao())
            .formaPagamento(venda.getFormaPagamento())
            .quantidadeParcelas(venda.getQuantidadeParcelas())
            .build();
    }
}
