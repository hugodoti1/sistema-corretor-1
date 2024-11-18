import { GlobalExceptionHandler } from '../GlobalExceptionHandler';
import { BankConnectionException } from '../BankException';
import { bankErrorStorage } from '../../services/bankErrorStorage';
import { metricsService } from '../../services/metricsService';
import { notificationService } from '../../services/notificationService';

// Mock dos serviços
jest.mock('../../services/bankErrorStorage');
jest.mock('../../services/metricsService');
jest.mock('../../services/notificationService');

describe('GlobalExceptionHandler', () => {
  let handler: GlobalExceptionHandler;
  let consoleLogSpy: jest.SpyInstance;
  let consoleGroupSpy: jest.SpyInstance;
  let consoleGroupEndSpy: jest.SpyInstance;

  beforeEach(() => {
    handler = GlobalExceptionHandler.getInstance();
    handler.resetDefaultConfig();

    // Mock do console
    consoleLogSpy = jest.spyOn(console, 'log').mockImplementation();
    consoleGroupSpy = jest.spyOn(console, 'group').mockImplementation();
    consoleGroupEndSpy = jest.spyOn(console, 'groupEnd').mockImplementation();

    // Limpa os mocks
    jest.clearAllMocks();
  });

  afterEach(() => {
    consoleLogSpy.mockRestore();
    consoleGroupSpy.mockRestore();
    consoleGroupEndSpy.mockRestore();
  });

  it('should handle bank exception with default config', async () => {
    const error = new BankConnectionException(
      'Bradesco',
      undefined,
      { requestId: '123' },
      'BRA-1001'
    );

    await handler.handleException(error);

    // Verifica se todos os serviços foram chamados
    expect(bankErrorStorage.addError).toHaveBeenCalled();
    expect(metricsService.trackError).toHaveBeenCalled();
    expect(notificationService.showError).toHaveBeenCalled();
    expect(consoleGroupSpy).toHaveBeenCalled();
  });

  it('should handle bank exception with custom config', async () => {
    const error = new BankConnectionException('Bradesco');
    
    await handler.handleException(error, {
      logToConsole: false,
      showNotification: false
    });

    // Verifica se apenas os serviços configurados foram chamados
    expect(bankErrorStorage.addError).toHaveBeenCalled();
    expect(metricsService.trackError).toHaveBeenCalled();
    expect(notificationService.showError).not.toHaveBeenCalled();
    expect(consoleGroupSpy).not.toHaveBeenCalled();
  });

  it('should format error message with error code', async () => {
    const error = new BankConnectionException(
      'Bradesco',
      undefined,
      { requestId: '123' },
      'BRA-1001'
    );

    await handler.handleException(error, {
      logToConsole: true,
      showNotification: true,
      storeError: false,
      updateMetrics: false
    });

    // Verifica se a mensagem formatada foi usada
    expect(consoleLogSpy).toHaveBeenCalledWith(
      expect.stringContaining('[BRA-1001]')
    );
    expect(notificationService.showError).toHaveBeenCalledWith(
      expect.stringContaining('[BRA-1001]'),
      expect.any(Object)
    );
  });

  it('should handle error in handler gracefully', async () => {
    const error = new BankConnectionException('Bradesco');
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();

    // Força um erro no storage
    (bankErrorStorage.addError as jest.Mock).mockRejectedValue(new Error('Storage error'));

    await handler.handleException(error);

    // Verifica se o erro foi logado
    expect(consoleErrorSpy).toHaveBeenCalledWith(
      'Erro ao tratar exceção:',
      expect.any(Error)
    );
    expect(consoleErrorSpy).toHaveBeenCalledWith(
      'Erro original:',
      error
    );

    consoleErrorSpy.mockRestore();
  });

  it('should update default config', () => {
    handler.setDefaultConfig({
      logToConsole: false,
      showNotification: false
    });

    const error = new BankConnectionException('Bradesco');
    handler.handleException(error);

    expect(notificationService.showError).not.toHaveBeenCalled();
    expect(consoleGroupSpy).not.toHaveBeenCalled();
  });

  it('should reset default config', () => {
    handler.setDefaultConfig({
      logToConsole: false,
      showNotification: false
    });

    handler.resetDefaultConfig();

    const error = new BankConnectionException('Bradesco');
    handler.handleException(error);

    expect(notificationService.showError).toHaveBeenCalled();
    expect(consoleGroupSpy).toHaveBeenCalled();
  });
});
