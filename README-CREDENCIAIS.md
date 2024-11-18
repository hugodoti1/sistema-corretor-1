# Configuração de Credenciais Bancárias

Este documento descreve como configurar as credenciais para integração com os bancos.

## Estrutura de Diretórios

```
/certificados/
  ├── bb/
  │   └── certificado-bb.p12
  ├── inter/
  │   └── certificado-inter.p12
  └── caixa/
      └── certificado-caixa.p12
```

## Configuração do Ambiente

1. Crie um arquivo `.env` na raiz do projeto baseado no modelo `.env.example`
2. Preencha as variáveis com suas credenciais:

```properties
# Banco do Brasil
BB_CLIENT_ID=seu_client_id_bb
BB_CLIENT_SECRET=seu_client_secret_bb
BB_CERTIFICADO=/certificados/bb/certificado-bb.p12
BB_CERTIFICADO_SENHA=senha_certificado_bb
BB_WEBHOOK_URL=https://seu-dominio.com.br/api/banco/webhooks/bb

# Banco Inter
INTER_CLIENT_ID=seu_client_id_inter
INTER_CLIENT_SECRET=seu_client_secret_inter
INTER_CERTIFICADO=/certificados/inter/certificado-inter.p12
INTER_CERTIFICADO_SENHA=senha_certificado_inter
INTER_WEBHOOK_URL=https://seu-dominio.com.br/api/banco/webhooks/inter

# Caixa Econômica
CAIXA_CLIENT_ID=seu_client_id_caixa
CAIXA_CLIENT_SECRET=seu_client_secret_caixa
CAIXA_CERTIFICADO=/certificados/caixa/certificado-caixa.p12
CAIXA_CERTIFICADO_SENHA=senha_certificado_caixa
CAIXA_WEBHOOK_URL=https://seu-dominio.com.br/api/banco/webhooks/caixa

# Configurações Gerais
WEBHOOK_BASE_URL=https://seu-dominio.com.br
```

## Obtendo as Credenciais

### Banco do Brasil
1. Acesse o [Portal do Desenvolvedor BB](https://developers.bb.com.br)
2. Crie uma aplicação
3. Solicite acesso aos recursos necessários
4. Baixe o certificado digital

### Banco Inter
1. Acesse o [Portal do Desenvolvedor Inter](https://developers.bancointer.com.br)
2. Crie uma aplicação
3. Configure os scopes necessários
4. Gere o certificado digital

### Caixa Econômica
1. Entre em contato com seu gerente Caixa
2. Solicite acesso ao ambiente de APIs
3. Obtenha as credenciais e certificado digital

## Segurança

- NUNCA comite o arquivo `.env` ou certificados no repositório
- Mantenha as credenciais em local seguro
- Use variáveis de ambiente em produção
- Rotacione as credenciais periodicamente
- Monitore o uso das APIs

## Troubleshooting

### Erro de Certificado
- Verifique se o caminho do certificado está correto
- Confirme se a senha do certificado está correta
- Certifique-se que o certificado não está expirado

### Erro de Autenticação
- Verifique se as credenciais estão corretas
- Confirme se os scopes solicitados estão corretos
- Verifique se a aplicação está aprovada no banco

### Erro de Webhook
- Confirme se a URL do webhook está acessível
- Verifique se o domínio está registrado no banco
- Teste o endpoint do webhook localmente
