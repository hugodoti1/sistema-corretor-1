package br.com.corretor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "bancos")
public class BancoConfig {
    
    private BancoBrasilConfig bancoBrasil;
    private ItauConfig itau;
    
    @Data
    public static class BancoBrasilConfig {
        private String baseUrl;
        private String token;
        private String accessKey;
        private String certificado;
        private int timeout;
        private RetryConfig retry;
    }
    
    @Data
    public static class ItauConfig {
        private String baseUrl;
        private String token;
        private String apiKey;
        private String certificado;
        private int timeout;
        private RetryConfig retry;
    }
    
    @Data
    public static class RetryConfig {
        private int maxAttempts;
        private long backoffPeriod;
        private long maxBackoffPeriod;
    }
}
