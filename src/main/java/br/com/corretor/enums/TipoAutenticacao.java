package br.com.corretor.enums;

/**
 * Enum que define os tipos de autenticação suportados para integração bancária
 */
public enum TipoAutenticacao {
    OAUTH2,
    CERTIFICADO_DIGITAL,
    TOKEN_ESTATICO,
    CHAVE_API
}
