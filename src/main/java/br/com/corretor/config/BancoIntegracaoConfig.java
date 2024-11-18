package br.com.corretor.config;

import br.com.corretor.enums.TipoBanco;
import br.com.corretor.service.banco.IntegracaoBancariaBase;
import br.com.corretor.service.banco.impl.BancoDoBrasilIntegracao;
import br.com.corretor.service.banco.impl.BancoInterIntegracao;
import br.com.corretor.service.banco.impl.BradescoIntegracao;
import br.com.corretor.service.banco.impl.ItauIntegracao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class BancoIntegracaoConfig {

    @Bean
    public Map<TipoBanco, IntegracaoBancariaBase> integracoesBancarias(
            BancoDoBrasilIntegracao bancoDoBrasilIntegracao,
            BradescoIntegracao bradescoIntegracao,
            ItauIntegracao itauIntegracao,
            BancoInterIntegracao bancoInterIntegracao
    ) {
        Map<TipoBanco, IntegracaoBancariaBase> integracoes = new HashMap<>();
        integracoes.put(TipoBanco.BANCO_DO_BRASIL, bancoDoBrasilIntegracao);
        integracoes.put(TipoBanco.BRADESCO, bradescoIntegracao);
        integracoes.put(TipoBanco.ITAU, itauIntegracao);
        // Adicionar código do Banco Inter quando disponível
        return integracoes;
    }
}
