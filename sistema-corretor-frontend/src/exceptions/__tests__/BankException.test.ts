import {
  BankException,
  BankConnectionException,
  BankAuthenticationException,
  BankValidationException,
  BankTimeoutException,
  BankRateLimitException,
  BankDataException,
  BankPermissionException,
  BankServiceUnavailableException
} from '../BankException';

describe('BankException', () => {
  const bankName = 'TestBank';
  const message = 'Test error message';
  const details = { key: 'value' };

  describe('Base BankException', () => {
    it('should create base exception with correct properties', () => {
      const error = new BankException(
        message,
        bankName,
        'CONNECTION',
        'high',
        details
      );

      expect(error).toBeInstanceOf(BankException);
      expect(error.message).toBe(message);
      expect(error.bankName).toBe(bankName);
      expect(error.category).toBe('CONNECTION');
      expect(error.severity).toBe('high');
      expect(error.details).toEqual(details);
      expect(error.timestamp).toBeDefined();
    });

    it('should convert to BankError correctly', () => {
      const error = new BankException(
        message,
        bankName,
        'CONNECTION',
        'high',
        details
      );

      const bankError = error.toBankError();

      expect(bankError.id).toBeDefined();
      expect(bankError.message).toBe(message);
      expect(bankError.bankName).toBe(bankName);
      expect(bankError.category).toBe('CONNECTION');
      expect(bankError.severity).toBe('high');
      expect(bankError.details).toEqual(details);
      expect(bankError.timestamp).toBeDefined();
      expect(bankError.resolvedAt).toBeNull();
    });

    it('should format toString correctly', () => {
      const error = new BankException(
        message,
        bankName,
        'CONNECTION',
        'high'
      );

      expect(error.toString()).toBe('[HIGH] TestBank - CONNECTION: Test error message');
    });
  });

  describe('BankConnectionException', () => {
    it('should create connection exception with default message', () => {
      const error = new BankConnectionException(bankName);

      expect(error).toBeInstanceOf(BankConnectionException);
      expect(error).toBeInstanceOf(BankException);
      expect(error.category).toBe('CONNECTION');
      expect(error.severity).toBe('high');
      expect(error.message).toBe('Erro de conexão com o banco');
    });
  });

  describe('BankAuthenticationException', () => {
    it('should create authentication exception with default message', () => {
      const error = new BankAuthenticationException(bankName);

      expect(error).toBeInstanceOf(BankAuthenticationException);
      expect(error.category).toBe('AUTHENTICATION');
      expect(error.severity).toBe('high');
      expect(error.message).toBe('Erro de autenticação');
    });
  });

  describe('BankValidationException', () => {
    it('should create validation exception with default message', () => {
      const error = new BankValidationException(bankName);

      expect(error).toBeInstanceOf(BankValidationException);
      expect(error.category).toBe('VALIDATION');
      expect(error.severity).toBe('medium');
      expect(error.message).toBe('Erro de validação');
    });
  });

  describe('BankTimeoutException', () => {
    it('should create timeout exception with default message', () => {
      const error = new BankTimeoutException(bankName);

      expect(error).toBeInstanceOf(BankTimeoutException);
      expect(error.category).toBe('TIMEOUT');
      expect(error.severity).toBe('high');
      expect(error.message).toBe('Operação excedeu o tempo limite');
    });
  });

  describe('BankRateLimitException', () => {
    it('should create rate limit exception with retry after', () => {
      const error = new BankRateLimitException(bankName, undefined, {
        retryAfter: 30000
      });

      expect(error).toBeInstanceOf(BankRateLimitException);
      expect(error.category).toBe('TIMEOUT');
      expect(error.severity).toBe('medium');
      expect(error.getRetryAfter()).toBe(30000);
    });

    it('should return default retry after when not specified', () => {
      const error = new BankRateLimitException(bankName);
      expect(error.getRetryAfter()).toBe(60000); // 1 minuto
    });
  });

  describe('BankDataException', () => {
    it('should create data exception with invalid fields', () => {
      const error = new BankDataException(bankName, undefined, {
        invalidFields: ['field1', 'field2']
      });

      expect(error).toBeInstanceOf(BankDataException);
      expect(error.category).toBe('VALIDATION');
      expect(error.severity).toBe('medium');
      expect(error.getInvalidFields()).toEqual(['field1', 'field2']);
    });

    it('should return empty array when no invalid fields specified', () => {
      const error = new BankDataException(bankName);
      expect(error.getInvalidFields()).toEqual([]);
    });
  });

  describe('BankPermissionException', () => {
    it('should create permission exception with required permissions', () => {
      const error = new BankPermissionException(bankName, undefined, {
        requiredPermissions: ['read', 'write']
      });

      expect(error).toBeInstanceOf(BankPermissionException);
      expect(error.category).toBe('AUTHENTICATION');
      expect(error.severity).toBe('high');
      expect(error.getRequiredPermissions()).toEqual(['read', 'write']);
    });

    it('should return empty array when no permissions specified', () => {
      const error = new BankPermissionException(bankName);
      expect(error.getRequiredPermissions()).toEqual([]);
    });
  });

  describe('BankServiceUnavailableException', () => {
    it('should create service unavailable exception with estimated downtime', () => {
      const error = new BankServiceUnavailableException(bankName, undefined, {
        estimatedDowntime: 60
      });

      expect(error).toBeInstanceOf(BankServiceUnavailableException);
      expect(error.category).toBe('CONNECTION');
      expect(error.severity).toBe('high');
      expect(error.getEstimatedDowntime()).toBe(60);
    });

    it('should return default downtime when not specified', () => {
      const error = new BankServiceUnavailableException(bankName);
      expect(error.getEstimatedDowntime()).toBe(30); // 30 minutos
    });
  });
});
