# Componentes do Sistema de Erros

## BankError

### Descrição
Componente React responsável pela exibição individual de um erro bancário.

### Props

```typescript
interface BankErrorProps {
  error: StoredBankError;
  onDelete?: (id: string) => void;
  expanded?: boolean;
}
```

### Funcionalidades

1. **Exibição de Detalhes**
   - Nome do banco
   - Mensagem de erro
   - Severidade
   - Categoria
   - Timestamp formatado

2. **Interatividade**
   - Expansão/colapso
   - Botão de exclusão
   - Feedback visual

### Exemplo de Uso

```typescript
<BankError
  error={storedError}
  onDelete={handleDelete}
  expanded={false}
/>
```

## BankErrorList

### Descrição
Componente de lista que exibe e gerencia múltiplos erros bancários.

### Props

```typescript
interface BankErrorListProps {
  title?: string;
  showControls?: boolean;
  maxHeight?: string;
  onErrorDelete?: (id: string) => void;
}
```

### Funcionalidades

1. **Listagem**
   - Ordenação por data
   - Paginação
   - Filtros

2. **Controles**
   - Busca
   - Filtros de severidade
   - Filtros de categoria
   - Filtros de banco

3. **Estatísticas**
   - Total de erros
   - Distribuição por severidade
   - Distribuição por banco

### Exemplo de Uso

```typescript
<BankErrorList
  title="Erros do Sistema"
  showControls={true}
  maxHeight="70vh"
  onErrorDelete={handleErrorDelete}
/>
```

## Notification

### Descrição
Componente de notificação que exibe mensagens temporárias.

### Props

```typescript
interface NotificationProps {
  // Sem props - usa NotificationService
}
```

### Funcionalidades

1. **Tipos de Notificação**
   - Sucesso
   - Erro
   - Aviso
   - Informação

2. **Comportamento**
   - Auto-fechamento
   - Animações
   - Posicionamento

### Exemplo de Uso

```typescript
// App.tsx
function App() {
  return (
    <>
      {/* ... */}
      <Notification />
    </>
  );
}
```

## Estilos e Temas

### Material-UI Theme

```typescript
const theme = createTheme({
  components: {
    MuiAlert: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          marginBottom: 8
        },
        standardError: {
          backgroundColor: '#FDE8E8',
          color: '#C53030'
        },
        standardWarning: {
          backgroundColor: '#FEFCBF',
          color: '#B7791F'
        },
        standardSuccess: {
          backgroundColor: '#E6FFE6',
          color: '#276749'
        }
      }
    }
  }
});
```

### Estilos Customizados

```typescript
const useStyles = makeStyles((theme) => ({
  errorCard: {
    marginBottom: theme.spacing(2),
    borderLeft: '4px solid',
    '&.high': {
      borderColor: theme.palette.error.main
    },
    '&.medium': {
      borderColor: theme.palette.warning.main
    },
    '&.low': {
      borderColor: theme.palette.info.main
    }
  }
}));
```

## Integração

### Com Context API

```typescript
const ErrorContext = React.createContext<{
  errors: StoredBankError[];
  addError: (error: BankError) => void;
  removeError: (id: string) => void;
}>({
  errors: [],
  addError: () => {},
  removeError: () => {}
});
```

### Com Redux

```typescript
const errorSlice = createSlice({
  name: 'errors',
  initialState: [],
  reducers: {
    addError: (state, action) => {
      state.unshift(action.payload);
    },
    removeError: (state, action) => {
      return state.filter(error => error.id !== action.payload);
    }
  }
});
```

## Testes

### Testes de Componente

```typescript
describe('BankError', () => {
  it('deve renderizar detalhes do erro', () => {
    const error = createMockError();
    render(<BankError error={error} />);
    
    expect(screen.getByText(error.message)).toBeInTheDocument();
    expect(screen.getByText(error.bankName)).toBeInTheDocument();
  });
});
```

### Testes de Integração

```typescript
describe('BankErrorList', () => {
  it('deve filtrar erros', async () => {
    render(<BankErrorList />);
    
    const searchInput = screen.getByPlaceholderText('Buscar erros...');
    await userEvent.type(searchInput, 'conexão');
    
    expect(screen.getByText('Erro de conexão')).toBeInTheDocument();
  });
});
```

## Performance

### Otimizações

1. **Memorização**
   ```typescript
   const MemoizedBankError = React.memo(BankError);
   ```

2. **Virtualização**
   ```typescript
   <VirtualizedList
     height={400}
     itemCount={errors.length}
     itemSize={100}
     width="100%"
   >
     {({ index, style }) => (
       <MemoizedBankError
         error={errors[index]}
         style={style}
       />
     )}
   </VirtualizedList>
   ```

3. **Lazy Loading**
   ```typescript
   const BankErrorList = React.lazy(() => 
     import('./BankErrorList')
   );
   ```

## Acessibilidade

### ARIA Labels

```typescript
<Alert
  role="alert"
  aria-live="polite"
  aria-label={`Erro no ${error.bankName}`}
>
  {error.message}
</Alert>
```

### Keyboard Navigation

```typescript
function handleKeyPress(event: React.KeyboardEvent) {
  if (event.key === 'Enter' || event.key === ' ') {
    setExpanded(!expanded);
  }
}
```

## Melhores Práticas

1. **Componentização**
   - Componentes pequenos e focados
   - Reutilização de código
   - Separação de responsabilidades

2. **Performance**
   - Uso de memo quando apropriado
   - Otimização de re-renders
   - Lazy loading de componentes grandes

3. **Manutenção**
   - Documentação clara
   - Testes abrangentes
   - Código limpo e organizado
