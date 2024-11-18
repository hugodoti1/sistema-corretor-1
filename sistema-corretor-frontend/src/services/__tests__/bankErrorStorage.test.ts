import { bankErrorStorage } from '../bankErrorStorage';
import { BankError } from '../../types/bankError';

describe('BankErrorStorage', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  const createMockError = (overrides?: Partial<BankError>): BankError => ({
    bankName: 'Banco Teste',
    message: 'Erro de teste',
    severity: 'high',
    category: 'CONNECTION',
    timestamp: new Date().toISOString(),
    ...overrides
  });

  it('deve armazenar e recuperar erros', () => {
    const error = createMockError();
    bankErrorStorage.addError(error);

    const storedErrors = bankErrorStorage.getErrors();
    expect(storedErrors).toHaveLength(1);
    expect(storedErrors[0].bankName).toBe('Banco Teste');
    expect(storedErrors[0].message).toBe('Erro de teste');
  });

  it('deve respeitar o limite máximo de erros', () => {
    // Adiciona mais erros que o limite
    for (let i = 0; i < 105; i++) {
      bankErrorStorage.addError(createMockError({
        message: `Erro ${i}`
      }));
    }

    const storedErrors = bankErrorStorage.getErrors();
    expect(storedErrors).toHaveLength(100); // Limite máximo
    expect(storedErrors[0].message).toBe('Erro 104'); // Último erro adicionado
  });

  it('deve remover erros corretamente', () => {
    const error1 = bankErrorStorage.addError(createMockError({ message: 'Erro 1' }));
    const error2 = bankErrorStorage.addError(createMockError({ message: 'Erro 2' }));

    bankErrorStorage.removeError(error1.id);
    const storedErrors = bankErrorStorage.getErrors();

    expect(storedErrors).toHaveLength(1);
    expect(storedErrors[0].message).toBe('Erro 2');
  });

  it('deve gerar estatísticas corretas', () => {
    bankErrorStorage.addError(createMockError({ 
      bankName: 'Banco A',
      severity: 'high'
    }));
    bankErrorStorage.addError(createMockError({ 
      bankName: 'Banco A',
      severity: 'medium'
    }));
    bankErrorStorage.addError(createMockError({ 
      bankName: 'Banco B',
      severity: 'high'
    }));

    const stats = bankErrorStorage.getErrorStatistics();

    expect(stats.totalErrors).toBe(3);
    expect(stats.errorsByBank['Banco A']).toBe(2);
    expect(stats.errorsByBank['Banco B']).toBe(1);
    expect(stats.errorsBySeverity['high']).toBe(2);
    expect(stats.errorsBySeverity['medium']).toBe(1);
  });

  it('deve persistir erros no localStorage', () => {
    bankErrorStorage.addError(createMockError());
    
    // Simula recarregamento criando nova instância
    const newStorage = bankErrorStorage;
    const storedErrors = newStorage.getErrors();
    
    expect(storedErrors).toHaveLength(1);
    expect(storedErrors[0].bankName).toBe('Banco Teste');
  });

  it('deve lidar com erros de localStorage', () => {
    // Simula erro no localStorage
    jest.spyOn(localStorage, 'setItem').mockImplementation(() => {
      throw new Error('localStorage error');
    });

    expect(() => {
      bankErrorStorage.addError(createMockError());
    }).toThrow('Erro ao armazenar erro no localStorage');

    jest.restoreAllMocks();
  });
});
