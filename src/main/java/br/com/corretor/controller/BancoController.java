package br.com.corretor.controller;

import br.com.corretor.dto.SaldoBancarioDTO;
import br.com.corretor.exception.BancoIntegracaoException;
import br.com.corretor.model.ContaBancaria;
import br.com.corretor.model.TransacaoBancaria;
import br.com.corretor.repository.ContaBancariaRepository;
import br.com.corretor.repository.TransacaoBancariaRepository;
import br.com.corretor.service.BancoIntegracaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/banco")
@RequiredArgsConstructor
@Tag(name = "Banco", description = "API para integração bancária")
public class BancoController {

    private final BancoIntegracaoService bancoIntegracaoService;
    private final ContaBancariaRepository contaBancariaRepository;
    private final TransacaoBancariaRepository transacaoBancariaRepository;

    @Operation(summary = "Sincroniza transações de uma conta bancária")
    @ApiResponse(responseCode = "200", description = "Transações sincronizadas com sucesso")
    @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    @PostMapping("/contas/{contaId}/sincronizar")
    public ResponseEntity<List<TransacaoBancaria>> sincronizarConta(@PathVariable Long contaId) {
        ContaBancaria conta = contaBancariaRepository.findById(contaId)
            .orElseThrow(() -> new BancoIntegracaoException("Conta não encontrada", "CONTA_NAO_ENCONTRADA", null, ErrorType.CONTA_INVALIDA));
        
        List<TransacaoBancaria> transacoes = bancoIntegracaoService.sincronizarTransacoes(conta);
        return ResponseEntity.ok(transacoes);
    }

    @Operation(summary = "Obtém extrato de uma conta bancária")
    @ApiResponse(responseCode = "200", description = "Extrato obtido com sucesso")
    @GetMapping("/contas/{contaId}/extrato")
    public ResponseEntity<List<TransacaoBancaria>> obterExtrato(
            @PathVariable Long contaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        
        List<TransacaoBancaria> transacoes = transacaoBancariaRepository
            .findByContaBancariaIdAndDataBetweenOrderByDataDesc(contaId, dataInicio, dataFim);
        return ResponseEntity.ok(transacoes);
    }

    @Operation(summary = "Atualiza saldo de uma conta bancária")
    @ApiResponse(responseCode = "200", description = "Saldo atualizado com sucesso")
    @PostMapping("/contas/{contaId}/atualizar-saldo")
    public ResponseEntity<SaldoBancarioDTO> atualizarSaldo(@PathVariable Long contaId) {
        ContaBancaria conta = contaBancariaRepository.findById(contaId)
            .orElseThrow(() -> new BancoIntegracaoException("Conta não encontrada", "CONTA_NAO_ENCONTRADA", null, ErrorType.CONTA_INVALIDA));
        
        SaldoBancarioDTO saldo = bancoIntegracaoService.atualizarSaldo(conta);
        return ResponseEntity.ok(saldo);
    }

    @Operation(summary = "Recebe webhook de notificações bancárias")
    @ApiResponse(responseCode = "200", description = "Notificação processada com sucesso")
    @PostMapping("/webhooks/{bancoId}")
    public ResponseEntity<Void> receberWebhook(
            @PathVariable String bancoId,
            @RequestBody Map<String, Object> payload) {
        bancoIntegracaoService.processarWebhook(bancoId, payload);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Concilia uma transação bancária com uma transação do sistema")
    @ApiResponse(responseCode = "200", description = "Transação conciliada com sucesso")
    @PostMapping("/conciliacao")
    public ResponseEntity<Void> conciliarTransacao(
            @RequestParam Long transacaoBancariaId,
            @RequestParam Long transacaoSistemaId) {
        bancoIntegracaoService.conciliarTransacao(transacaoBancariaId, transacaoSistemaId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove conciliação de uma transação")
    @ApiResponse(responseCode = "200", description = "Conciliação removida com sucesso")
    @DeleteMapping("/conciliacao/{transacaoBancariaId}")
    public ResponseEntity<Void> desconciliarTransacao(@PathVariable Long transacaoBancariaId) {
        bancoIntegracaoService.desconciliarTransacao(transacaoBancariaId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Verifica status de uma conta bancária")
    @ApiResponse(responseCode = "200", description = "Status verificado com sucesso")
    @GetMapping("/contas/{contaId}/status")
    public ResponseEntity<Boolean> verificarStatusConta(@PathVariable Long contaId) {
        ContaBancaria conta = contaBancariaRepository.findById(contaId)
            .orElseThrow(() -> new BancoIntegracaoException("Conta não encontrada", "CONTA_NAO_ENCONTRADA", null, ErrorType.CONTA_INVALIDA));
        
        boolean status = bancoIntegracaoService.verificarStatusConta(conta);
        return ResponseEntity.ok(status);
    }

    @Operation(summary = "Obtém detalhes de uma conta bancária")
    @ApiResponse(responseCode = "200", description = "Detalhes obtidos com sucesso")
    @GetMapping("/contas/{contaId}/detalhes")
    public ResponseEntity<ContaBancaria> obterDetalhesConta(@PathVariable Long contaId) {
        ContaBancaria conta = contaBancariaRepository.findById(contaId)
            .orElseThrow(() -> new BancoIntegracaoException("Conta não encontrada", "CONTA_NAO_ENCONTRADA", null, ErrorType.CONTA_INVALIDA));
        
        ContaBancaria detalhes = bancoIntegracaoService.obterDetalhesConta(conta);
        return ResponseEntity.ok(detalhes);
    }
}
