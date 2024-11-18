package br.com.corretor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "banco")
public class BancoCredenciaisConfig {

    private BancoBBConfig bb;
    private BancoInterConfig inter;
    private BancoCaixaConfig caixa;
    private WebhookConfig webhook;

    @Getter
    @Setter
    public static class BancoBBConfig {
        @NotBlank(message = "BB Client ID é obrigatório")
        private String clientId;
        
        @NotBlank(message = "BB Client Secret é obrigatório")
        private String clientSecret;
        
        @NotBlank(message = "BB Base URL é obrigatória")
        private String baseUrl;
        
        @NotBlank(message = "BB Certificado é obrigatório")
        private String certificado;
        
        @NotBlank(message = "BB Senha do certificado é obrigatória")
        private String certificadoSenha;
        
        private String webhookUrl;
    }

    @Getter
    @Setter
    public static class BancoInterConfig {
        @NotBlank(message = "Inter Client ID é obrigatório")
        private String clientId;
        
        @NotBlank(message = "Inter Client Secret é obrigatório")
        private String clientSecret;
        
        @NotBlank(message = "Inter Base URL é obrigatória")
        private String baseUrl;
        
        @NotBlank(message = "Inter Certificado é obrigatório")
        private String certificado;
        
        @NotBlank(message = "Inter Senha do certificado é obrigatória")
        private String certificadoSenha;
        
        private String webhookUrl;
        private String scope;
    }

    @Getter
    @Setter
    public static class BancoCaixaConfig {
        @NotBlank(message = "Caixa Client ID é obrigatório")
        private String clientId;
        
        @NotBlank(message = "Caixa Client Secret é obrigatório")
        private String clientSecret;
        
        @NotBlank(message = "Caixa Base URL é obrigatória")
        private String baseUrl;
        
        @NotBlank(message = "Caixa Certificado é obrigatório")
        private String certificado;
        
        @NotBlank(message = "Caixa Senha do certificado é obrigatória")
        private String certificadoSenha;
        
        private String webhookUrl;
    }

    @Getter
    @Setter
    public static class WebhookConfig {
        private String baseUrl;
        private String path;
    }
}
