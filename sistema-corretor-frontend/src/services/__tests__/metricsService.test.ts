import { metricsService } from '../metricsService';
import { BankError } from '../../types/bankError';

describe('MetricsService', () => {
  beforeEach(() => {
    localStorage.clear();
    jest.useFakeTimers();
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  const mockError: BankError = {
    bankName: 'TestBank',
    message: 'Test error',
    severity: 'high',
    category: 'CONNECTION',
    timestamp: new Date().toISOString(),
    id: '1',
    resolvedAt: null
  };

  const mockResolvedError: BankError = {
    ...mockError,
    id: '2',
    resolvedAt: new Date(Date.now() + 30 * 60 * 1000).toISOString() // Resolvido após 30 minutos
  };

  describe('trackError', () => {
    it('should track new errors correctly', () => {
      metricsService.trackError(mockError);
      const trends = metricsService.getTrends();
      
      expect(trends).toHaveLength(1);
      expect(trends[0].count).toBe(1);
    });

    it('should increment existing hour count', () => {
      metricsService.trackError(mockError);
      metricsService.trackError(mockError);
      
      const trends = metricsService.getTrends();
      expect(trends[0].count).toBe(2);
    });
  });

  describe('getMetrics', () => {
    it('should calculate basic metrics correctly', () => {
      const metrics = metricsService.getMetrics([mockError, mockResolvedError]);
      
      expect(metrics.totalErrors).toBe(2);
      expect(metrics.activeErrors).toBe(1);
      expect(metrics.resolvedErrors).toBe(1);
    });

    it('should calculate resolution time metrics', () => {
      const metrics = metricsService.getMetrics([mockResolvedError]);
      
      expect(metrics.averageResolutionTime).toBe(30); // 30 minutos
      expect(metrics.maxResolutionTime).toBe(30);
      expect(metrics.minResolutionTime).toBe(30);
    });

    it('should calculate error distribution metrics', () => {
      const metrics = metricsService.getMetrics([mockError, mockResolvedError]);
      
      expect(metrics.errorsByBank.TestBank).toBe(2);
      expect(metrics.errorsBySeverity.high).toBe(2);
      expect(metrics.errorsByCategory.CONNECTION).toBe(2);
    });

    it('should calculate time-based metrics', () => {
      const recentError = {
        ...mockError,
        timestamp: new Date().toISOString()
      };
      
      const metrics = metricsService.getMetrics([recentError]);
      
      expect(metrics.errorsLast24h).toBe(1);
      expect(metrics.errorRate24h).toBe(1/24); // 1 erro em 24 horas
    });
  });

  describe('periodic updates', () => {
    it('should update trends periodically', () => {
      metricsService.trackError(mockError);
      
      // Avança 1 hora
      jest.advanceTimersByTime(60 * 60 * 1000);
      
      const trends = metricsService.getTrends();
      expect(trends).toHaveLength(2); // Entrada original + nova entrada
    });

    it('should maintain only 30 days of data', () => {
      // Adiciona um erro
      metricsService.trackError(mockError);
      
      // Avança 31 dias
      jest.advanceTimersByTime(31 * 24 * 60 * 60 * 1000);
      
      const trends = metricsService.getTrends();
      expect(trends.some(t => t.count > 0)).toBeFalsy(); // Não deve ter mais o erro antigo
    });
  });
});
