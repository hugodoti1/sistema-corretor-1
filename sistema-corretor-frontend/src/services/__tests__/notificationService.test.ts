import { notificationService } from '../notificationService';
import { BankError } from '../../types/bankError';

describe('NotificationService', () => {
  let mockListener: jest.Mock;

  beforeEach(() => {
    mockListener = jest.fn();
    // Limpa os listeners anteriores
    const unsubscribe = notificationService.subscribe(mockListener);
    unsubscribe();
  });

  it('deve notificar os listeners quando uma notificação é enviada', () => {
    const unsubscribe = notificationService.subscribe(mockListener);
    const message = 'Teste de notificação';
    
    notificationService.notify(message);
    
    expect(mockListener).toHaveBeenCalledWith({
      message,
      options: undefined
    });
    
    unsubscribe();
  });

  it('deve formatar corretamente notificações de erro bancário', () => {
    const unsubscribe = notificationService.subscribe(mockListener);
    
    const bankError: BankError = {
      bankName: 'Banco Teste',
      message: 'Erro de conexão',
      severity: 'high',
      category: 'CONNECTION',
      timestamp: new Date().toISOString()
    };

    notificationService.notifyBankError(bankError);

    expect(mockListener).toHaveBeenCalledWith({
      message: 'Crítico - Banco Teste: Erro de conexão',
      options: {
        variant: 'error',
        autoHideDuration: 6000
      }
    });

    unsubscribe();
  });

  it('deve enviar notificações de sucesso corretamente', () => {
    const unsubscribe = notificationService.subscribe(mockListener);
    const message = 'Operação bem sucedida';
    
    notificationService.notifySuccess(message);
    
    expect(mockListener).toHaveBeenCalledWith({
      message,
      options: {
        variant: 'success',
        autoHideDuration: undefined
      }
    });
    
    unsubscribe();
  });

  it('deve permitir múltiplos listeners', () => {
    const mockListener2 = jest.fn();
    
    const unsubscribe1 = notificationService.subscribe(mockListener);
    const unsubscribe2 = notificationService.subscribe(mockListener2);
    
    notificationService.notifyInfo('Teste múltiplos listeners');
    
    expect(mockListener).toHaveBeenCalled();
    expect(mockListener2).toHaveBeenCalled();
    
    unsubscribe1();
    unsubscribe2();
  });

  it('deve remover listener corretamente após unsubscribe', () => {
    const unsubscribe = notificationService.subscribe(mockListener);
    unsubscribe();
    
    notificationService.notify('Teste após unsubscribe');
    
    expect(mockListener).not.toHaveBeenCalled();
  });
});
