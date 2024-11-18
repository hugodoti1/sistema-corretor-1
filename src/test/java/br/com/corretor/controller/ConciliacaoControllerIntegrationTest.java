package br.com.corretor.controller;

import br.com.corretor.model.Banco;
import br.com.corretor.model.Conciliacao;
import br.com.corretor.model.Transacao;
import br.com.corretor.repository.BancoRepository;
import br.com.corretor.repository.ConciliacaoRepository;
import br.com.corretor.repository.TransacaoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ConciliacaoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BancoRepository bancoRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private ConciliacaoRepository conciliacaoRepository;

    private Banco banco;
    private Transacao transacao1;
    private Transacao transacao2;
    private String dataInicioStr;
    private String dataFimStr;

    @BeforeEach
    void setUp() {
        // Criar banco
        banco = new Banco();
        banco.setEmpresaId(1L);
        banco.setAgencia("1234");
        banco.setConta("56789");
        banco.setTipo("CORRENTE");
        banco = bancoRepository.save(banco);

        LocalDateTime agora = LocalDateTime.now();
        dataInicioStr = agora.minusDays(30).format(DateTimeFormatter.ISO_DATE_TIME);
        dataFimStr = agora.format(DateTimeFormatter.ISO_DATE_TIME);

        // Criar transações
        transacao1 = new Transacao();
        transacao1.setEmpresaId(1L);
        transacao1.setBancoId(banco.getId());
        transacao1.setValor(new BigDecimal("100.00"));
        transacao1.setTipo("CREDITO");
        transacao1.setDataTransacao(agora.minusDays(15));
        transacao1.setIdTransacaoBanco("TX123");
        transacao1 = transacaoRepository.save(transacao1);

        transacao2 = new Transacao();
        transacao2.setEmpresaId(1L);
        transacao2.setBancoId(banco.getId());
        transacao2.setValor(new BigDecimal("100.00"));
        transacao2.setTipo("CREDITO");
        transacao2.setDataTransacao(agora.minusDays(15));
        transacao2.setIdTransacaoBanco("TX123");
        transacao2 = transacaoRepository.save(transacao2);
    }

    @Test
    void iniciarConciliacao_DeveRetornarConciliacaoIniciada() throws Exception {
        mockMvc.perform(post("/api/conciliacoes/iniciar")
                .param("empresaId", "1")
                .param("bancoId", banco.getId().toString())
                .param("dataInicio", dataInicioStr)
                .param("dataFim", dataFimStr)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empresaId").value(1))
                .andExpect(jsonPath("$.bancoId").value(banco.getId()))
                .andExpect(jsonPath("$.concluida").value(false));
    }

    @Test
    void processarConciliacao_DeveProcessarComSucesso() throws Exception {
        // Criar conciliação
        Conciliacao conciliacao = new Conciliacao();
        conciliacao.setEmpresaId(1L);
        conciliacao.setBancoId(banco.getId());
        conciliacao.setDataInicio(LocalDateTime.parse(dataInicioStr));
        conciliacao.setDataFim(LocalDateTime.parse(dataFimStr));
        conciliacao = conciliacaoRepository.save(conciliacao);

        mockMvc.perform(post("/api/conciliacoes/{id}/processar", conciliacao.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verificar se as transações foram conciliadas
        mockMvc.perform(get("/api/conciliacoes/conciliadas")
                .param("empresaId", "1")
                .param("bancoId", banco.getId().toString())
                .param("dataInicio", dataInicioStr)
                .param("dataFim", dataFimStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].conciliada").value(true))
                .andExpect(jsonPath("$[1].conciliada").value(true));
    }

    @Test
    void buscarTransacoesPendentes_DeveRetornarTransacoesPendentes() throws Exception {
        mockMvc.perform(get("/api/conciliacoes/pendentes")
                .param("empresaId", "1")
                .param("bancoId", banco.getId().toString())
                .param("dataInicio", dataInicioStr)
                .param("dataFim", dataFimStr))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].conciliada").value(false))
                .andExpect(jsonPath("$[1].conciliada").value(false));
    }

    @Test
    void calcularSaldoConciliado_DeveCalcularCorretamente() throws Exception {
        // Conciliar transações primeiro
        transacao1.setConciliada(true);
        transacao2.setConciliada(true);
        transacaoRepository.save(transacao1);
        transacaoRepository.save(transacao2);

        mockMvc.perform(get("/api/conciliacoes/saldo")
                .param("empresaId", "1")
                .param("bancoId", banco.getId().toString())
                .param("dataInicio", dataInicioStr)
                .param("dataFim", dataFimStr))
                .andExpect(status().isOk())
                .andExpect(content().string("200.00")); // 100 + 100
    }

    @Test
    void iniciarConciliacao_ComBancoInexistente_DeveRetornarErro() throws Exception {
        mockMvc.perform(post("/api/conciliacoes/iniciar")
                .param("empresaId", "1")
                .param("bancoId", "999")
                .param("dataInicio", dataInicioStr)
                .param("dataFim", dataFimStr)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void processarConciliacao_ConciliacaoInexistente_DeveRetornarErro() throws Exception {
        mockMvc.perform(post("/api/conciliacoes/999/processar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
