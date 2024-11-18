package br.com.corretor.integration;

import br.com.corretor.enums.TipoBanco;
import br.com.corretor.exception.banco.BancoAutenticacaoException;
import br.com.corretor.exception.banco.BancoIntegracaoException;
import br.com.corretor.service.banco.BancoIntegracaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BancoExceptionHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BancoIntegracaoService bancoIntegracaoService;

    @Test
    void quandoErroAutenticacao_RetornaUnauthorized() throws Exception {
        when(bancoIntegracaoService.consultarSaldoMultiplosBancos(any(), any()))
            .thenThrow(new BancoAutenticacaoException(
                TipoBanco.BANCO_DO_BRASIL,
                "Token inválido",
                "AUTH_001",
                "O token de acesso expirou"
            ));

        mockMvc.perform(get("/api/banco/saldo")
                .param("bancos", "BANCO_DO_BRASIL")
                .param("conta", "12345-6")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.banco").value("BANCO_DO_BRASIL"))
            .andExpect(jsonPath("$.codigo").value("AUTH_001"))
            .andExpect(jsonPath("$.detalhes").value("O token de acesso expirou"));
    }

    @Test
    void quandoErroValidacao_RetornaBadRequest() throws Exception {
        when(bancoIntegracaoService.sincronizarTransacoesMultiplosBancos(
            eq(Arrays.asList(TipoBanco.BANCO_DO_BRASIL)),
            any(),
            any(LocalDate.class),
            any(LocalDate.class)
        )).thenThrow(new BancoIntegracaoException(
            TipoBanco.BANCO_DO_BRASIL,
            "Conta inválida",
            "CONTA_INVALIDA",
            "O número da conta deve seguir o padrão NNNNNNNNNN-D"
        ));

        mockMvc.perform(get("/api/banco/transacoes")
                .param("bancos", "BANCO_DO_BRASIL")
                .param("conta", "123")
                .param("dataInicio", LocalDate.now().minusDays(30).toString())
                .param("dataFim", LocalDate.now().toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.banco").value("BANCO_DO_BRASIL"))
            .andExpect(jsonPath("$.codigo").value("CONTA_INVALIDA"));
    }

    @Test
    void quandoParametroInvalido_RetornaBadRequest() throws Exception {
        mockMvc.perform(post("/api/banco/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.banco").value("SISTEMA"))
            .andExpect(jsonPath("$.codigo").value("PARAMETRO_INVALIDO"));
    }

    @Test
    void quandoErroIntegracao_RetornaBadRequest() throws Exception {
        when(bancoIntegracaoService.consultarSaldoMultiplosBancos(any(), any()))
            .thenThrow(new BancoIntegracaoException(
                TipoBanco.BANCO_DO_BRASIL,
                "Erro de comunicação",
                "API_ERROR",
                "Não foi possível conectar ao servidor do banco"
            ));

        mockMvc.perform(get("/api/banco/saldo")
                .param("bancos", "BANCO_DO_BRASIL")
                .param("conta", "12345-6")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.banco").value("BANCO_DO_BRASIL"))
            .andExpect(jsonPath("$.codigo").value("API_ERROR"))
            .andExpect(jsonPath("$.detalhes").value("Não foi possível conectar ao servidor do banco"));
    }

    @Test
    void quandoMultiplosBancos_RetornaBadRequest() throws Exception {
        when(bancoIntegracaoService.consultarSaldoMultiplosBancos(any(), any()))
            .thenThrow(new BancoIntegracaoException(
                TipoBanco.BANCO_DO_BRASIL,
                "Erro em múltiplos bancos",
                "MULTI_ERR",
                "Erro ao consultar múltiplos bancos simultaneamente"
            ));

        mockMvc.perform(get("/api/banco/saldo")
                .param("bancos", "BANCO_DO_BRASIL,BRADESCO")
                .param("conta", "12345-6")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.banco").value("BANCO_DO_BRASIL"))
            .andExpect(jsonPath("$.codigo").value("MULTI_ERR"));
    }

    @Test
    void quandoPeriodoInvalido_RetornaBadRequest() throws Exception {
        LocalDate dataFim = LocalDate.now().minusDays(30);
        LocalDate dataInicio = LocalDate.now();

        mockMvc.perform(get("/api/banco/transacoes")
                .param("bancos", "BANCO_DO_BRASIL")
                .param("conta", "12345-6")
                .param("dataInicio", dataInicio.toString())
                .param("dataFim", dataFim.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.banco").value("SISTEMA"))
            .andExpect(jsonPath("$.codigo").value("PARAMETRO_INVALIDO"))
            .andExpect(jsonPath("$.mensagem").value(containsString("período")));
    }

    @Test
    void quandoWebhookInvalido_RetornaBadRequest() throws Exception {
        String webhookInvalido = "{\"url\": \"not-a-valid-url\", \"eventos\": [\"SALDO\"]}";

        mockMvc.perform(post("/api/banco/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(webhookInvalido))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.banco").value("SISTEMA"))
            .andExpect(jsonPath("$.codigo").value("PARAMETRO_INVALIDO"))
            .andExpect(jsonPath("$.mensagem").value(containsString("URL")));
    }

    @Test
    void quandoLimiteRequisicaoExcedido_RetornaTooManyRequests() throws Exception {
        when(bancoIntegracaoService.consultarSaldoMultiplosBancos(any(), any()))
            .thenThrow(new BancoIntegracaoException(
                TipoBanco.BANCO_DO_BRASIL,
                "Limite de requisições excedido",
                "RATE_LIMIT",
                "Aguarde alguns minutos antes de tentar novamente"
            ));

        mockMvc.perform(get("/api/banco/saldo")
                .param("bancos", "BANCO_DO_BRASIL")
                .param("conta", "12345-6")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isTooManyRequests())
            .andExpect(jsonPath("$.banco").value("BANCO_DO_BRASIL"))
            .andExpect(jsonPath("$.codigo").value("RATE_LIMIT"));
    }

    @Test
    void quandoServicoIndisponivel_RetornaServiceUnavailable() throws Exception {
        when(bancoIntegracaoService.consultarSaldoMultiplosBancos(any(), any()))
            .thenThrow(new BancoIntegracaoException(
                TipoBanco.BANCO_DO_BRASIL,
                "Serviço temporariamente indisponível",
                "SERVICO_INDISPONIVEL",
                "O serviço do banco está em manutenção"
            ));

        mockMvc.perform(get("/api/banco/saldo")
                .param("bancos", "BANCO_DO_BRASIL")
                .param("conta", "12345-6")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isServiceUnavailable())
            .andExpect(jsonPath("$.banco").value("BANCO_DO_BRASIL"))
            .andExpect(jsonPath("$.codigo").value("SERVICO_INDISPONIVEL"));
    }
}
