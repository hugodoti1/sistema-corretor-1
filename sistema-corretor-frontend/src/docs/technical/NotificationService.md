# Serviço de Notificações

## Descrição Técnica

O `NotificationService` é um serviço singleton que implementa o padrão Observer para gerenciar notificações em tempo real na aplicação.

## API

### Tipos

```typescript
type NotificationOptions = {
  autoHideDuration?: number;
  variant?: 'success' | 'error' | 'warning' | 'info';
};

type NotificationPayload = {
  message: string;
  options?: NotificationOptions;
};

type NotificationListener = (notification: NotificationPayload) => void;
```

### Métodos

#### `subscribe(listener: NotificationListener): () => void`
Registra um novo listener para notificações.
- **Retorno**: Função para cancelar a inscrição

```typescript
const unsubscribe = notificationService.subscribe(notification => {
  console.log(notification.message);
});
```

#### `notify(message: string, options?: NotificationOptions): void`
Envia uma notificação para todos os listeners.

```typescript
notificationService.notify('Mensagem', {
  variant: 'success',
  autoHideDuration: 5000
});
```

#### `notifyBankError(error: BankError): void`
Envia uma notificação formatada para erro bancário.

```typescript
notificationService.notifyBankError({
  bankName: 'Banco X',
  message: 'Erro de conexão',
  severity: 'high',
  category: 'CONNECTION'
});
```

#### Métodos Helper

```typescript
notifySuccess(message: string, duration?: number)
notifyError(message: string, duration?: number)
notifyWarning(message: string, duration?: number)
notifyInfo(message: string, duration?: number)
```

## Implementação

### Gerenciamento de Estado

```typescript
class NotificationService {
  private listeners: Set<NotificationListener> = new Set();
  // ...
}
```

### Ciclo de Vida

1. **Inicialização**
   - Criação do singleton
   - Set vazio de listeners

2. **Registro de Listener**
   - Adição ao Set
   - Retorno da função de cleanup

3. **Notificação**
   - Formatação da mensagem
   - Broadcast para todos os listeners

4. **Cleanup**
   - Remoção de listeners inativos
   - Prevenção de memory leaks

## Integração

### Com Componente React

```typescript
function MyComponent() {
  useEffect(() => {
    const unsubscribe = notificationService.subscribe(notification => {
      // Lógica de exibição
    });
    return unsubscribe;
  }, []);
}
```

### Com Sistema de Erros

```typescript
bankErrorStorage.addError(error).then(() => {
  notificationService.notifyBankError(error);
});
```

## Considerações de Performance

1. **Gerenciamento de Memória**
   - Uso de Set para listeners únicos
   - Limpeza automática de listeners

2. **Throttling**
   - Evitar spam de notificações
   - Agrupamento de notificações similares

3. **Renderização**
   - Uso de React.memo
   - Otimização de re-renders

## Testes

```typescript
describe('NotificationService', () => {
  it('deve notificar listeners', () => {
    const listener = jest.fn();
    const unsubscribe = service.subscribe(listener);
    service.notify('test');
    expect(listener).toHaveBeenCalled();
  });
});
```

## Exemplos de Uso

### Notificação Básica
```typescript
notificationService.notify('Operação realizada');
```

### Notificação com Opções
```typescript
notificationService.notify('Sucesso!', {
  variant: 'success',
  autoHideDuration: 3000
});
```

### Erro Bancário
```typescript
notificationService.notifyBankError({
  bankName: 'Banco',
  message: 'Falha',
  severity: 'high',
  category: 'CONNECTION'
});
```

## Troubleshooting

### Problemas Comuns

1. **Notificações Duplicadas**
   - Verifique múltiplos registros
   - Confirme unsubscribe correto

2. **Memory Leaks**
   - Use cleanup em useEffect
   - Verifique unsubscribe

3. **Timing Issues**
   - Ajuste autoHideDuration
   - Verifique ordem de execução

## Melhores Práticas

1. **Mensagens**
   - Seja conciso e claro
   - Use níveis apropriados
   - Inclua contexto relevante

2. **Performance**
   - Limite número de listeners
   - Faça unsubscribe adequado
   - Evite notificações em loop

3. **Manutenção**
   - Documente novos métodos
   - Mantenha testes atualizados
   - Monitore uso de memória
