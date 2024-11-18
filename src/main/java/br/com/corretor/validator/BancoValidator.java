package br.com.corretor.validator;

import br.com.corretor.enums.TipoBanco;
import br.com.corretor.exception.banco.BancoIntegracaoException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class BancoValidator {
    private static final Pattern CONTA_PATTERN = Pattern.compile("^\\d{1,10}-?\\d{1}$");
    private static final Pattern AGENCIA_PATTERN = Pattern.compile("^\\d{4}(-\\d{1})?$");
    private static final int MAX_DIAS_CONSULTA = 90;

    public void validarConta(String conta, TipoBanco banco) {
        if (conta == null || !CONTA_PATTERN.matcher(conta).matches()) {
            throw new BancoIntegracaoException(
                banco,
                "Número da conta inválido",
                "CONTA_INVALIDA",
                "O número da conta deve seguir o padrão NNNNNNNNNN-D"
            );
        }
    }

    public void validarAgencia(String agencia, TipoBanco banco) {
        if (agencia == null || !AGENCIA_PATTERN.matcher(agencia).matches()) {
            throw new BancoIntegracaoException(
                banco,
                "Número da agência inválido",
                "AGENCIA_INVALIDA",
                "O número da agência deve seguir o padrão NNNN ou NNNN-D"
            );
        }
    }

    public void validarPeriodoConsulta(LocalDate dataInicio, LocalDate dataFim, TipoBanco banco) {
        if (dataInicio == null || dataFim == null) {
            throw new BancoIntegracaoException(
                banco,
                "Datas de consulta inválidas",
                "DATA_INVALIDA",
                "As datas de início e fim são obrigatórias"
            );
        }

        if (dataInicio.isAfter(dataFim)) {
            throw new BancoIntegracaoException(
                banco,
                "Período de consulta inválido",
                "PERIODO_INVALIDO",
                "A data de início deve ser anterior à data fim"
            );
        }

        if (dataInicio.plusDays(MAX_DIAS_CONSULTA).isBefore(dataFim)) {
            throw new BancoIntegracaoException(
                banco,
                "Período de consulta muito longo",
                "PERIODO_MUITO_LONGO",
                String.format("O período máximo de consulta é de %d dias", MAX_DIAS_CONSULTA)
            );
        }
    }

    public void validarWebhookUrl(String webhookUrl, TipoBanco banco) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            throw new BancoIntegracaoException(
                banco,
                "URL de webhook inválida",
                "WEBHOOK_INVALIDO",
                "A URL do webhook é obrigatória"
            );
        }

        try {
            new java.net.URL(webhookUrl);
        } catch (Exception e) {
            throw new BancoIntegracaoException(
                banco,
                "URL de webhook mal formatada",
                "WEBHOOK_INVALIDO",
                "A URL do webhook deve ser uma URL válida"
            );
        }
    }

    public void validarBancos(List<TipoBanco> bancos) {
        if (bancos == null || bancos.isEmpty()) {
            throw new IllegalArgumentException("A lista de bancos não pode ser vazia");
        }

        if (bancos.contains(null)) {
            throw new IllegalArgumentException("A lista de bancos contém valores nulos");
        }
    }
}
