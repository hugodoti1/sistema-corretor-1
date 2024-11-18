# Sistema de Gerenciamento de Erros Bancários

## Visão Geral

O Sistema de Gerenciamento de Erros Bancários é uma solução completa para rastreamento, armazenamento e notificação de erros em integrações bancárias. O sistema oferece persistência local, notificações em tempo real e estatísticas detalhadas.

## Arquitetura

O sistema é composto por três componentes principais:

1. **Armazenamento de Erros** (`bankErrorStorage`)
   - Persistência local via localStorage
   - Limite de 100 erros armazenados
   - Estatísticas em tempo real
   - Gerenciamento de ciclo de vida dos erros

2. **Sistema de Notificações** (`notificationService`)
   - Notificações em tempo real
   - Diferentes níveis de severidade
   - Personalização de duração e estilo
   - Suporte a múltiplos listeners

3. **Componentes Visuais**
   - `BankError`: Exibição individual de erro
   - `BankErrorList`: Lista de erros com filtros
   - `Notification`: Componente de notificação

## Tipos de Erro

### Severidade
- **Alto (high)**: Erros críticos que requerem ação imediata
- **Médio (medium)**: Alertas que precisam de atenção
- **Baixo (low)**: Informações e avisos menos críticos

### Categorias
- **CONNECTION**: Problemas de conexão com banco
- **AUTHENTICATION**: Falhas de autenticação
- **VALIDATION**: Erros de validação de dados
- **TIMEOUT**: Tempo limite excedido
- **UNKNOWN**: Erros não categorizados

## Como Usar

### 1. Registrando um Erro

```typescript
import { bankErrorStorage } from '../services/bankErrorStorage';

// Registrar um novo erro
bankErrorStorage.addError({
  bankName: 'Banco Example',
  message: 'Falha na conexão',
  severity: 'high',
  category: 'CONNECTION',
  timestamp: new Date().toISOString()
});
```

### 2. Exibindo Lista de Erros

```typescript
import { BankErrorList } from '../components/bank/BankErrorList';

function MinhaPage() {
  return (
    <BankErrorList 
      title="Erros do Sistema"
      showControls={true}
      maxHeight="70vh"
    />
  );
}
```

### 3. Enviando Notificações

```typescript
import { notificationService } from '../services/notificationService';

// Notificação simples
notificationService.notify('Mensagem importante');

// Notificação com tipo
notificationService.notifyError('Erro crítico detectado');
notificationService.notifySuccess('Operação concluída');
notificationService.notifyWarning('Atenção necessária');
notificationService.notifyInfo('Informação do sistema');
```

## Estatísticas

O sistema mantém estatísticas em tempo real sobre:
- Total de erros
- Erros por banco
- Erros por severidade
- Erros por categoria
- Timestamps do primeiro e último erro

```typescript
const stats = bankErrorStorage.getErrorStatistics();
console.log(stats.totalErrors); // Total de erros
console.log(stats.errorsByBank); // Distribuição por banco
```

## Persistência

Os erros são automaticamente persistidos no localStorage do navegador:
- Limite máximo de 100 erros
- Ordenação por timestamp (mais recente primeiro)
- Remoção automática de erros antigos
- Preservação entre recarregamentos da página

## Boas Práticas

1. **Registro de Erros**
   - Sempre inclua mensagens descritivas
   - Categorize corretamente a severidade
   - Adicione contexto relevante

2. **Notificações**
   - Use o nível apropriado de severidade
   - Mantenha mensagens concisas
   - Evite spam de notificações

3. **Manutenção**
   - Remova erros resolvidos
   - Monitore as estatísticas
   - Revise erros frequentes

## Customização

### Estilos
O sistema usa Material-UI e pode ser customizado via tema:

```typescript
const theme = createTheme({
  components: {
    MuiAlert: {
      styleOverrides: {
        // Seus estilos customizados
      }
    }
  }
});
```

### Configurações
Principais configurações disponíveis:
- `MAX_STORED_ERRORS`: Limite de erros armazenados
- `autoHideDuration`: Duração das notificações
- Posicionamento das notificações
- Filtros da lista de erros

## Troubleshooting

### Problemas Comuns

1. **Erros não aparecem na lista**
   - Verifique o localStorage
   - Confirme o limite máximo
   - Verifique filtros ativos

2. **Notificações não aparecem**
   - Verifique se o componente está montado
   - Confirme se o serviço está sendo chamado
   - Verifique configurações de duração

3. **Estatísticas incorretas**
   - Limpe o localStorage
   - Recarregue a aplicação
   - Verifique a consistência dos dados

## Desenvolvimento

### Testes
O sistema inclui testes unitários e de integração:

```bash
# Executar todos os testes
npm test

# Testes específicos
npm test -- notificationService
npm test -- Notification
npm test -- bankErrorStorage
```

### Extensão
Para adicionar novos recursos:

1. **Novas Categorias**
   - Adicione ao tipo `ErrorCategory`
   - Atualize as funções de formatação
   - Adicione aos filtros

2. **Novos Tipos de Notificação**
   - Estenda o serviço de notificação
   - Adicione novos métodos helper
   - Atualize os testes

## Segurança

- Dados sensíveis são mascarados
- Limite de armazenamento local
- Sanitização de mensagens
- Validação de dados

## Roadmap

Melhorias planejadas:
1. Sincronização com backend
2. Exportação de relatórios
3. Filtros avançados
4. Visualizações gráficas
5. Sistema de arquivamento
