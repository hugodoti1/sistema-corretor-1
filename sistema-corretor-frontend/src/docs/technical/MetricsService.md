# Serviço de Métricas de Erros Bancários

## Visão Geral
O serviço de métricas (`metricsService`) é responsável por coletar, processar e fornecer métricas relacionadas aos erros bancários no sistema. Ele oferece insights valiosos sobre o comportamento dos erros ao longo do tempo, permitindo uma melhor compreensão e monitoramento do sistema.

## Funcionalidades Principais

### 1. Rastreamento de Erros
- Registra cada novo erro no momento em que ocorre
- Mantém histórico de tendências por hora
- Armazena até 30 dias de dados históricos
- Integração automática com o serviço de armazenamento de erros

### 2. Métricas Calculadas

#### Métricas Básicas
- Total de erros
- Erros ativos
- Erros resolvidos
- Taxa de erro (24h)

#### Métricas de Tempo
- Tempo médio de resolução
- Tempo máximo de resolução
- Tempo mínimo de resolução
- Hora do dia com mais erros

#### Distribuição de Erros
- Por banco
- Por severidade
- Por categoria

#### Tendências
- Últimas 24 horas
- Últimos 7 dias
- Últimos 30 dias

## Implementação

### Armazenamento
- Utiliza localStorage para persistência
- Chaves de armazenamento:
  - `@SistemaCorretor:errorMetrics`: Métricas gerais
  - `@SistemaCorretor:errorTrends`: Dados de tendência

### Atualização de Dados
- Atualização em tempo real ao adicionar erros
- Atualização periódica a cada hora
- Limpeza automática de dados antigos (>30 dias)

## Uso

### Rastreamento de Erros
```typescript
metricsService.trackError(error);
```

### Obtenção de Métricas
```typescript
const metrics = metricsService.getMetrics(errors);
```

### Obtenção de Tendências
```typescript
const trends = metricsService.getTrends();
```

## Visualização

O componente `ErrorMetricsPanel` fornece uma interface visual para as métricas, incluindo:

1. Cards de métricas principais
2. Gráfico de tendências (30 dias)
3. Gráfico de distribuição por severidade
4. Gráfico de distribuição por categoria

## Integração com Outros Serviços

### BankErrorStorage
- Integração automática ao adicionar novos erros
- Atualização de métricas ao resolver erros

### NotificationService
- Complementa as notificações em tempo real
- Fornece contexto histórico para notificações

## Considerações de Performance

### Otimizações
- Armazenamento eficiente de dados históricos
- Atualização periódica em vez de tempo real
- Limpeza automática de dados antigos

### Limites
- Máximo de 30 dias de histórico
- Agregação por hora para reduzir volume de dados
- Uso eficiente do localStorage

## Testes

Os testes cobrem:
1. Rastreamento de erros
2. Cálculo de métricas
3. Gestão de tendências
4. Atualizações periódicas
5. Limpeza de dados

## Manutenção

### Monitoramento
- Verificar uso do localStorage
- Monitorar performance das atualizações
- Validar precisão das métricas

### Backup
- Considerar exportação periódica
- Implementar recuperação de dados
- Manter redundância se necessário

## Roadmap

### Melhorias Futuras
1. Exportação de métricas
2. Alertas baseados em thresholds
3. Análise preditiva
4. Integração com sistemas externos
5. Dashboards customizáveis
