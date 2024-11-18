import { BankPaymentException } from '../BankException';
import { CommonBankErrorCode } from '../../types/bankErrorCodes';

describe('BankPaymentException', () => {
  const bankName = 'Banco do Brasil';

  describe('basic functionality', () => {
    it('should create exception with default message', () => {
      const error = new BankPaymentException(bankName);
      expect(error.message).toBe('Erro no processamento do pagamento');
      expect(error.bankName).toBe(bankName);
      expect(error.category).toBe('PAYMENT');
      expect(error.severity).toBe('high');
    });

    it('should create exception with custom message and details', () => {
      const details = {
        transactionId: '123',
        paymentStatus: 'REJECTED',
        failureReason: 'Saldo insuficiente'
      };
      const error = new BankPaymentException(
        bankName,
        'Pagamento rejeitado',
        details,
        'BB-PAY-003'
      );

      expect(error.message).toBe('Pagamento rejeitado');
      expect(error.details).toEqual(details);
      expect(error.errorCode).toBe('BB-PAY-003');
      expect(error.commonErrorCode).toBe(CommonBankErrorCode.INSUFFICIENT_FUNDS);
    });
  });

  describe('transaction details', () => {
    it('should return transaction details when available', () => {
      const error = new BankPaymentException(bankName, undefined, {
        transactionId: 'TX123',
        paymentStatus: 'PROCESSING',
        failureReason: 'Timeout'
      });

      expect(error.getTransactionId()).toBe('TX123');
      expect(error.getPaymentStatus()).toBe('PROCESSING');
      expect(error.getFailureReason()).toBe('Timeout');
    });

    it('should return undefined for missing details', () => {
      const error = new BankPaymentException(bankName);

      expect(error.getTransactionId()).toBeUndefined();
      expect(error.getPaymentStatus()).toBeUndefined();
      expect(error.getFailureReason()).toBeUndefined();
    });
  });

  describe('retry logic', () => {
    it('should allow retry for unprocessed payments', () => {
      const error = new BankPaymentException(bankName);
      expect(error.canRetry()).toBe(true);
    });

    it('should allow retry for temporary failures', () => {
      const error = new BankPaymentException(bankName, undefined, {
        transactionId: 'TX123',
        paymentStatus: 'PROCESSING'
      });
      expect(error.canRetry()).toBe(true);
    });

    it('should not allow retry for rejected payments', () => {
      const error = new BankPaymentException(bankName, undefined, {
        transactionId: 'TX123',
        paymentStatus: 'REJECTED'
      });
      expect(error.canRetry()).toBe(false);
    });
  });

  describe('payment status', () => {
    it('should identify processed payments', () => {
      const error = new BankPaymentException(bankName, undefined, {
        transactionId: 'TX123'
      });
      expect(error.wasProcessed()).toBe(true);
    });

    it('should identify unprocessed payments', () => {
      const error = new BankPaymentException(bankName);
      expect(error.wasProcessed()).toBe(false);
    });

    it('should identify payments requiring status check', () => {
      const pendingError = new BankPaymentException(bankName, undefined, {
        paymentStatus: 'PENDING'
      });
      expect(pendingError.requiresStatusCheck()).toBe(true);

      const processingError = new BankPaymentException(bankName, undefined, {
        paymentStatus: 'PROCESSING'
      });
      expect(processingError.requiresStatusCheck()).toBe(true);

      const rejectedError = new BankPaymentException(bankName, undefined, {
        paymentStatus: 'REJECTED'
      });
      expect(rejectedError.requiresStatusCheck()).toBe(false);
    });
  });

  describe('resolution steps', () => {
    it('should provide steps for unprocessed payment', () => {
      const error = new BankPaymentException(bankName);
      const steps = error.getResolutionSteps();
      
      expect(steps).toContain('Tentar processar o pagamento novamente');
    });

    it('should provide steps for rejected payment', () => {
      const error = new BankPaymentException(bankName, undefined, {
        transactionId: 'TX123',
        paymentStatus: 'REJECTED',
        failureReason: 'Saldo insuficiente'
      });
      const steps = error.getResolutionSteps();
      
      expect(steps).toContain('Verificar os dados do pagamento');
      expect(steps).toContain('Corrigir o problema: Saldo insuficiente');
    });

    it('should provide steps for pending payment', () => {
      const error = new BankPaymentException(bankName, undefined, {
        transactionId: 'TX123',
        paymentStatus: 'PENDING'
      });
      const steps = error.getResolutionSteps();
      
      expect(steps).toContain('Aguardar processamento pelo banco');
      expect(steps).toContain('Verificar status em alguns minutos');
      expect(steps).toContain('Consultar status atual no banco');
    });

    it('should provide fallback steps when no specific steps apply', () => {
      const error = new BankPaymentException(bankName, undefined, {
        transactionId: 'TX123',
        paymentStatus: 'UNKNOWN'
      });
      const steps = error.getResolutionSteps();
      
      expect(steps).toContain('Entrar em contato com o suporte do banco');
    });
  });

  describe('error conversion', () => {
    it('should convert to BankError with payment details', () => {
      const error = new BankPaymentException(
        bankName,
        'Pagamento rejeitado',
        {
          transactionId: 'TX123',
          paymentStatus: 'REJECTED',
          failureReason: 'Saldo insuficiente'
        },
        'BB-PAY-003'
      );

      const bankError = error.toBankError();

      expect(bankError.message).toBe('Pagamento rejeitado');
      expect(bankError.category).toBe('PAYMENT');
      expect(bankError.severity).toBe('high');
      expect(bankError.errorCode).toBe('BB-PAY-003');
      expect(bankError.commonErrorCode).toBe(CommonBankErrorCode.INSUFFICIENT_FUNDS);
      expect(bankError.details).toEqual({
        transactionId: 'TX123',
        paymentStatus: 'REJECTED',
        failureReason: 'Saldo insuficiente'
      });
    });
  });
});
