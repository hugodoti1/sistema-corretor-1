import { CommonBankErrorCode, getBankErrorDetails, hasBankErrorCode, getCommonErrorCode, formatBankErrorMessage } from '../bankErrorCodes';

describe('Bank Error Codes', () => {
  describe('getBankErrorDetails', () => {
    it('should return error details for valid bank and code', () => {
      const details = getBankErrorDetails('Banco do Brasil', 'BB-001');
      expect(details).toBeDefined();
      expect(details?.message).toBe('Chave J inválida');
      expect(details?.category).toBe('VALIDATION');
      expect(details?.severity).toBe('medium');
      expect(details?.commonCode).toBe(CommonBankErrorCode.INVALID_CREDENTIALS);
    });

    it('should return undefined for invalid bank', () => {
      const details = getBankErrorDetails('Banco Inexistente', 'BB-001');
      expect(details).toBeUndefined();
    });

    it('should return undefined for invalid code', () => {
      const details = getBankErrorDetails('Banco do Brasil', 'INVALID-CODE');
      expect(details).toBeUndefined();
    });
  });

  describe('hasBankErrorCode', () => {
    it('should return true for existing error code', () => {
      expect(hasBankErrorCode('Bradesco', 'BRA-1001')).toBe(true);
    });

    it('should return false for non-existing error code', () => {
      expect(hasBankErrorCode('Bradesco', 'INVALID-CODE')).toBe(false);
    });

    it('should return false for non-existing bank', () => {
      expect(hasBankErrorCode('Banco Inexistente', 'BRA-1001')).toBe(false);
    });
  });

  describe('getCommonErrorCode', () => {
    it('should return common error code for mapped error', () => {
      const commonCode = getCommonErrorCode('Itau', 'ITAU-501');
      expect(commonCode).toBe(CommonBankErrorCode.INVALID_CREDENTIALS);
    });

    it('should return undefined for unmapped error', () => {
      const commonCode = getCommonErrorCode('Bradesco', 'BRA-1003');
      expect(commonCode).toBeUndefined();
    });

    it('should return undefined for invalid bank', () => {
      const commonCode = getCommonErrorCode('Banco Inexistente', 'CODE-001');
      expect(commonCode).toBeUndefined();
    });
  });

  describe('formatBankErrorMessage', () => {
    it('should format message with code for existing error', () => {
      const message = formatBankErrorMessage('Santander', 'SANT-E001');
      expect(message).toBe('[SANT-E001] Token JWT expirado');
    });

    it('should return code only for non-existing error', () => {
      const message = formatBankErrorMessage('Santander', 'INVALID-CODE');
      expect(message).toBe('INVALID-CODE');
    });

    it('should return code for non-existing bank', () => {
      const message = formatBankErrorMessage('Banco Inexistente', 'CODE-001');
      expect(message).toBe('CODE-001');
    });
  });
});
