# Armazenamento de Erros Bancários

## Descrição Técnica

O `BankErrorStorage` é um serviço responsável pelo gerenciamento persistente de erros bancários utilizando localStorage, com suporte a estatísticas e limite de armazenamento.

## API

### Tipos

```typescript
interface StoredBankError extends BankError {
  id: string;
  timestamp: string;
}

interface ErrorStatistics {
  totalErrors: number;
  errorsByBank: Record<string, number>;
  errorsBySeverity: Record<string, number>;
  errorsByCategory: Record<string, number>;
  firstErrorTimestamp: string | null;
  lastErrorTimestamp: string | null;
}
```

### Métodos Principais

#### `addError(error: BankError): StoredBankError`
Adiciona um novo erro ao armazenamento.
- Gera ID único
- Adiciona timestamp
- Mantém limite máximo
- Retorna erro armazenado

```typescript
const storedError = bankErrorStorage.addError({
  bankName: 'Banco',
  message: 'Erro de conexão',
  severity: 'high',
  category: 'CONNECTION'
});
```

#### `getErrors(): StoredBankError[]`
Recupera todos os erros armazenados.

```typescript
const errors = bankErrorStorage.getErrors();
```

#### `removeError(id: string): void`
Remove um erro específico.

```typescript
bankErrorStorage.removeError('error-123');
```

#### `getErrorStatistics(): ErrorStatistics`
Gera estatísticas dos erros armazenados.

```typescript
const stats = bankErrorStorage.getErrorStatistics();
```

## Implementação

### Constantes

```typescript
const STORAGE_KEY = '@SistemaCorretor:bankErrors';
const MAX_STORED_ERRORS = 100;
```

### Persistência

1. **Armazenamento**
   ```typescript
   localStorage.setItem(STORAGE_KEY, JSON.stringify(errors));
   ```

2. **Recuperação**
   ```typescript
   JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]');
   ```

3. **Limpeza**
   ```typescript
   if (errors.length > MAX_STORED_ERRORS) {
     errors.pop();
   }
   ```

## Estatísticas

### Cálculo de Métricas

```typescript
const stats = {
  totalErrors: errors.length,
  errorsByBank: groupBy(errors, 'bankName'),
  errorsBySeverity: groupBy(errors, 'severity'),
  errorsByCategory: groupBy(errors, 'category'),
  firstErrorTimestamp: errors[errors.length - 1]?.timestamp,
  lastErrorTimestamp: errors[0]?.timestamp
};
```

### Agrupamento

```typescript
function groupBy(errors: StoredBankError[], key: keyof StoredBankError) {
  return errors.reduce((acc, error) => {
    const value = error[key] as string;
    acc[value] = (acc[value] || 0) + 1;
    return acc;
  }, {} as Record<string, number>);
}
```

## Tratamento de Erros

### Validação de Dados

```typescript
function validateError(error: BankError) {
  if (!error.bankName || !error.message) {
    throw new Error('Erro inválido');
  }
}
```

### Tratamento de localStorage

```typescript
try {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(errors));
} catch (error) {
  console.error('Erro ao armazenar:', error);
  throw new Error('Erro ao armazenar no localStorage');
}
```

## Integração

### Com Componentes React

```typescript
function ErrorList() {
  const [errors, setErrors] = useState<StoredBankError[]>([]);
  
  useEffect(() => {
    setErrors(bankErrorStorage.getErrors());
  }, []);
}
```

### Com Sistema de Notificações

```typescript
bankErrorStorage.addError(error);
notificationService.notifyBankError(error);
```

## Performance

### Otimizações

1. **Limite de Armazenamento**
   - Máximo de 100 erros
   - Remoção automática dos mais antigos

2. **Indexação**
   - Uso de IDs únicos
   - Ordenação por timestamp

3. **Caching**
   - Memória local para estatísticas
   - Atualização sob demanda

## Testes

```typescript
describe('BankErrorStorage', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('deve armazenar erro', () => {
    const error = createMockError();
    const stored = bankErrorStorage.addError(error);
    expect(stored.id).toBeDefined();
  });
});
```

## Exemplos de Uso

### Adição de Erro
```typescript
const error = bankErrorStorage.addError({
  bankName: 'Banco X',
  message: 'Timeout na conexão',
  severity: 'high',
  category: 'TIMEOUT'
});
```

### Consulta de Estatísticas
```typescript
const stats = bankErrorStorage.getErrorStatistics();
console.log(`Total de erros: ${stats.totalErrors}`);
console.log('Erros por banco:', stats.errorsByBank);
```

## Troubleshooting

### Problemas Comuns

1. **Erro de Quota**
   - Limpe erros antigos
   - Verifique tamanho dos dados
   - Use compressão se necessário

2. **Inconsistência de Dados**
   - Valide dados na entrada
   - Mantenha backup
   - Implemente recuperação

3. **Performance**
   - Limite tamanho das mensagens
   - Otimize consultas
   - Use paginação

## Melhores Práticas

1. **Armazenamento**
   - Valide dados antes de armazenar
   - Mantenha backup dos dados críticos
   - Implemente limpeza periódica

2. **Consultas**
   - Use índices quando possível
   - Implemente paginação
   - Cache resultados frequentes

3. **Manutenção**
   - Monitore uso de espaço
   - Faça backup regular
   - Documente alterações
