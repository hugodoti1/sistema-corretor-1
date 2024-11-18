package br.com.corretor.controller;

import br.com.corretor.model.Conciliacao;
import br.com.corretor.model.Transacao;
import br.com.corretor.service.ConciliacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/conciliacoes")
@RequiredArgsConstructor
public class ConciliacaoController {

    private final ConciliacaoService conciliacaoService;

    @PostMapping("/iniciar")
    public ResponseEntity<Conciliacao> iniciarConciliacao(
            @RequestParam Long empresaId,
            @RequestParam Long bancoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        
        return ResponseEntity.ok(conciliacaoService.iniciarConciliacao(empresaId, bancoId, dataInicio, dataFim));
    }

    @PostMapping("/{conciliacaoId}/processar")
    public ResponseEntity<Void> processarConciliacao(@PathVariable Long conciliacaoId) {
        conciliacaoService.processarConciliacao(conciliacaoId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<Transacao>> buscarTransacoesPendentes(
            @RequestParam Long empresaId,
            @RequestParam Long bancoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        
        return ResponseEntity.ok(conciliacaoService.buscarTransacoesPendentes(
            empresaId, bancoId, dataInicio, dataFim));
    }

    @GetMapping("/conciliadas")
    public ResponseEntity<List<Transacao>> buscarTransacoesConciliadas(
            @RequestParam Long empresaId,
            @RequestParam Long bancoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        
        return ResponseEntity.ok(conciliacaoService.buscarTransacoesConciliadas(
            empresaId, bancoId, dataInicio, dataFim));
    }

    @GetMapping("/saldo")
    public ResponseEntity<BigDecimal> calcularSaldoConciliado(
            @RequestParam Long empresaId,
            @RequestParam Long bancoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        
        return ResponseEntity.ok(conciliacaoService.calcularSaldoConciliado(
            empresaId, bancoId, dataInicio, dataFim));
    }
}
