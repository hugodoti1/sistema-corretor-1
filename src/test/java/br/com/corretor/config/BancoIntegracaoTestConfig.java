package br.com.corretor.config;

import br.com.corretor.enums.TipoBanco;
import br.com.corretor.service.banco.IntegracaoBancariaBase;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@TestConfiguration
public class BancoIntegracaoTestConfig {

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Primary
    public Map<TipoBanco, IntegracaoBancariaBase> integracoesBancariasMock() {
        Map<TipoBanco, IntegracaoBancariaBase> integracoes = new HashMap<>();
        for (TipoBanco banco : TipoBanco.values()) {
            IntegracaoBancariaBase mockIntegracao = Mockito.mock(IntegracaoBancariaBase.class);
            integracoes.put(banco, mockIntegracao);
        }
        return integracoes;
    }
}
