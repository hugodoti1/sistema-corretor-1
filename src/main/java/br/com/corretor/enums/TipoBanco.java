package br.com.corretor.enums;

public enum TipoBanco {
    BANCO_DO_BRASIL("001"),
    BRADESCO("237"),
    ITAU("341"),
    SANTANDER("033"),
    CAIXA("104"),
    INTER("077");

    private final String codigo;

    TipoBanco(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public static TipoBanco fromCodigo(String codigo) {
        for (TipoBanco tipo : values()) {
            if (tipo.getCodigo().equals(codigo)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Código de banco inválido: " + codigo);
    }
}
