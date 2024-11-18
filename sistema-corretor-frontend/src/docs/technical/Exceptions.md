# Exceções Bancárias

## Visão Geral
O sistema utiliza uma hierarquia de exceções específicas para tratar diferentes tipos de erros que podem ocorrer durante a integração com bancos. Todas as exceções herdam da classe base `BankException` e são automaticamente integradas com o sistema de métricas e notificações.

## Hierarquia de Exceções

### BankException (Base)
Classe base para todas as exceções bancárias.

#### Propriedades
- `message`: Mensagem de erro
- `bankName`: Nome do banco
- `category`: Categoria do erro
- `severity`: Severidade do erro
- `timestamp`: Data/hora do erro
- `details`: Detalhes adicionais (opcional)

#### Métodos
- `toBankError()`: Converte a exceção em um objeto BankError
- `toString()`: Retorna uma representação em string da exceção

### Exceções Específicas

#### 1. BankConnectionException
Para erros de conexão com o banco.

```typescript
throw new BankConnectionException('BancoX', 'Falha na conexão');
```

#### 2. BankAuthenticationException
Para erros de autenticação.

```typescript
throw new BankAuthenticationException('BancoY', 'Token inválido');
```

#### 3. BankValidationException
Para erros de validação de dados.

```typescript
throw new BankValidationException('BancoZ', 'CPF inválido');
```

#### 4. BankTimeoutException
Para operações que excedem o tempo limite.

```typescript
throw new BankTimeoutException('BancoW', 'Operação demorou muito');
```

#### 5. BankRateLimitException
Para erros de limite de taxa de requisições.

```typescript
throw new BankRateLimitException('BancoV', undefined, {
  retryAfter: 30000 // 30 segundos
});
```

##### Métodos Específicos
- `getRetryAfter()`: Retorna o tempo de espera recomendado

#### 6. BankDataException
Para erros de dados inválidos ou mal formatados.

```typescript
throw new BankDataException('BancoU', 'Dados inválidos', {
  invalidFields: ['cpf', 'email']
});
```

##### Métodos Específicos
- `getInvalidFields()`: Retorna a lista de campos inválidos

#### 7. BankPermissionException
Para erros de permissão.

```typescript
throw new BankPermissionException('BancoT', 'Acesso negado', {
  requiredPermissions: ['read', 'write']
});
```

##### Métodos Específicos
- `getRequiredPermissions()`: Retorna as permissões necessárias

#### 8. BankServiceUnavailableException
Para serviços temporariamente indisponíveis.

```typescript
throw new BankServiceUnavailableException('BancoS', undefined, {
  estimatedDowntime: 60 // 60 minutos
});
```

##### Métodos Específicos
- `getEstimatedDowntime()`: Retorna o tempo estimado de indisponibilidade

## Integração com o Sistema

### 1. Métricas
Todas as exceções são automaticamente registradas no sistema de métricas:
- Contagem por tipo de exceção
- Tendências temporais
- Distribuição por banco
- Análise de severidade

### 2. Notificações
As exceções geram notificações automáticas com:
- Severidade visual apropriada
- Detalhes formatados
- Ações recomendadas
- Links para documentação

### 3. Armazenamento
Exceções são persistidas como `BankError` para:
- Análise histórica
- Relatórios
- Auditoria
- Troubleshooting

## Boas Práticas

### 1. Criação de Exceções
```typescript
// Com mensagem padrão
throw new BankConnectionException('BancoX');

// Com mensagem customizada
throw new BankConnectionException('BancoX', 'Falha específica');

// Com detalhes adicionais
throw new BankConnectionException('BancoX', 'Falha na conexão', {
  requestId: '123',
  endpoint: '/api/v1/status'
});
```

### 2. Tratamento de Exceções
```typescript
try {
  await bankOperation();
} catch (error) {
  if (error instanceof BankRateLimitException) {
    const retryAfter = error.getRetryAfter();
    // Agenda retry
  } else if (error instanceof BankDataException) {
    const invalidFields = error.getInvalidFields();
    // Trata campos inválidos
  } else if (error instanceof BankException) {
    // Trata outros erros bancários
  }
}
```

### 3. Conversão para BankError
```typescript
try {
  await bankOperation();
} catch (error) {
  if (error instanceof BankException) {
    const bankError = error.toBankError();
    bankErrorStorage.addError(bankError);
  }
}
```

## Extensibilidade

### 1. Criando Novas Exceções
```typescript
export class BankCustomException extends BankException {
  constructor(
    bankName: string,
    message: string = 'Erro customizado',
    details?: Record<string, unknown>
  ) {
    super(message, bankName, 'CUSTOM', 'medium', details);
    this.name = 'BankCustomException';
  }
}
```

### 2. Adicionando Funcionalidades
```typescript
export class BankApiException extends BankException {
  constructor(
    bankName: string,
    message: string,
    public readonly statusCode: number,
    details?: Record<string, unknown>
  ) {
    super(message, bankName, 'API', 'high', details);
    this.name = 'BankApiException';
  }

  public isClientError(): boolean {
    return this.statusCode >= 400 && this.statusCode < 500;
  }
}
```

## Considerações de Performance

1. As exceções são leves e não impactam significativamente a performance
2. O armazenamento de detalhes é opcional
3. A conversão para BankError é eficiente
4. O sistema de métricas usa agregação para evitar sobrecarga

## Manutenção

1. Mantenha as mensagens padrão atualizadas
2. Revise periodicamente as categorias e severidades
3. Monitore o uso de detalhes personalizados
4. Atualize a documentação ao adicionar novas exceções
