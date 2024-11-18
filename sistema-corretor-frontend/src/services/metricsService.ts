import { BankError } from '../types/bankError';

export interface ErrorMetrics {
  // Métricas gerais
  totalErrors: number;
  activeErrors: number;
  resolvedErrors: number;
  
  // Métricas de tempo
  averageResolutionTime: number; // em minutos
  maxResolutionTime: number;     // em minutos
  minResolutionTime: number;     // em minutos
  
  // Métricas por banco
  errorsByBank: Record<string, number>;
  resolutionTimeByBank: Record<string, number>;
  
  // Métricas por severidade
  errorsBySeverity: Record<string, number>;
  resolutionTimeBySeverity: Record<string, number>;
  
  // Métricas por categoria
  errorsByCategory: Record<string, number>;
  resolutionTimeByCategory: Record<string, number>;
  
  // Métricas de tendência
  errorsLast24h: number;
  errorsLast7d: number;
  errorsLast30d: number;
  
  // Taxa de erro
  errorRate24h: number; // erros por hora nas últimas 24h
  peakErrorTime: string; // hora do dia com mais erros
}

export interface ErrorTrend {
  timestamp: string;
  count: number;
}

class MetricsService {
  private static STORAGE_KEY = '@SistemaCorretor:errorMetrics';
  private static TREND_STORAGE_KEY = '@SistemaCorretor:errorTrends';

  // Armazena tendências de erro (últimas 30 dias)
  private errorTrends: ErrorTrend[] = [];
  
  constructor() {
    this.loadTrends();
    this.startPeriodicUpdate();
  }

  private loadTrends() {
    try {
      const storedTrends = localStorage.getItem(MetricsService.TREND_STORAGE_KEY);
      if (storedTrends) {
        this.errorTrends = JSON.parse(storedTrends);
      }
    } catch (error) {
      console.error('Erro ao carregar tendências:', error);
    }
  }

  private saveTrends() {
    try {
      localStorage.setItem(
        MetricsService.TREND_STORAGE_KEY,
        JSON.stringify(this.errorTrends)
      );
    } catch (error) {
      console.error('Erro ao salvar tendências:', error);
    }
  }

  private startPeriodicUpdate() {
    // Atualiza métricas a cada hora
    setInterval(() => {
      this.updateTrends();
    }, 60 * 60 * 1000); // 1 hora
  }

  private updateTrends() {
    const now = new Date();
    const hourKey = now.toISOString().slice(0, 13); // Format: YYYY-MM-DDTHH

    // Adiciona entrada para a hora atual
    this.errorTrends.push({
      timestamp: hourKey,
      count: 0
    });

    // Mantém apenas os últimos 30 dias de dados
    const thirtyDaysAgo = new Date();
    thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);
    this.errorTrends = this.errorTrends.filter(trend => 
      new Date(trend.timestamp) >= thirtyDaysAgo
    );

    this.saveTrends();
  }

  public trackError(error: BankError) {
    // Atualiza contagem da hora atual
    const now = new Date();
    const hourKey = now.toISOString().slice(0, 13);
    
    const currentTrend = this.errorTrends.find(t => t.timestamp === hourKey);
    if (currentTrend) {
      currentTrend.count++;
    } else {
      this.errorTrends.push({
        timestamp: hourKey,
        count: 1
      });
    }

    this.saveTrends();
  }

  public getMetrics(errors: BankError[]): ErrorMetrics {
    const now = new Date();
    const last24h = new Date(now.getTime() - 24 * 60 * 60 * 1000);
    const last7d = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
    const last30d = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);

    // Calcula métricas básicas
    const metrics: ErrorMetrics = {
      totalErrors: errors.length,
      activeErrors: errors.filter(e => !e.resolvedAt).length,
      resolvedErrors: errors.filter(e => e.resolvedAt).length,
      
      averageResolutionTime: 0,
      maxResolutionTime: 0,
      minResolutionTime: Infinity,
      
      errorsByBank: {},
      resolutionTimeByBank: {},
      
      errorsBySeverity: {},
      resolutionTimeBySeverity: {},
      
      errorsByCategory: {},
      resolutionTimeByCategory: {},
      
      errorsLast24h: 0,
      errorsLast7d: 0,
      errorsLast30d: 0,
      
      errorRate24h: 0,
      peakErrorTime: ''
    };

    // Processa cada erro
    errors.forEach(error => {
      // Contagem por banco
      metrics.errorsByBank[error.bankName] = (metrics.errorsByBank[error.bankName] || 0) + 1;
      
      // Contagem por severidade
      metrics.errorsBySeverity[error.severity] = (metrics.errorsBySeverity[error.severity] || 0) + 1;
      
      // Contagem por categoria
      metrics.errorsByCategory[error.category] = (metrics.errorsByCategory[error.category] || 0) + 1;
      
      // Tempo de resolução
      if (error.resolvedAt) {
        const resolutionTime = (new Date(error.resolvedAt).getTime() - new Date(error.timestamp).getTime()) / (1000 * 60);
        
        // Atualiza tempos min/max
        metrics.maxResolutionTime = Math.max(metrics.maxResolutionTime, resolutionTime);
        metrics.minResolutionTime = Math.min(metrics.minResolutionTime, resolutionTime);
        
        // Soma para média
        metrics.averageResolutionTime += resolutionTime;
        
        // Tempo por banco
        metrics.resolutionTimeByBank[error.bankName] = (metrics.resolutionTimeByBank[error.bankName] || 0) + resolutionTime;
        
        // Tempo por severidade
        metrics.resolutionTimeBySeverity[error.severity] = (metrics.resolutionTimeBySeverity[error.severity] || 0) + resolutionTime;
        
        // Tempo por categoria
        metrics.resolutionTimeByCategory[error.category] = (metrics.resolutionTimeByCategory[error.category] || 0) + resolutionTime;
      }
      
      // Contagem por período
      const errorDate = new Date(error.timestamp);
      if (errorDate >= last24h) metrics.errorsLast24h++;
      if (errorDate >= last7d) metrics.errorsLast7d++;
      if (errorDate >= last30d) metrics.errorsLast30d++;
    });

    // Calcula médias
    if (metrics.resolvedErrors > 0) {
      metrics.averageResolutionTime /= metrics.resolvedErrors;
    }

    // Calcula taxa de erro por hora
    metrics.errorRate24h = metrics.errorsLast24h / 24;

    // Encontra hora com mais erros
    const hourCounts = this.errorTrends.reduce((acc, trend) => {
      const hour = new Date(trend.timestamp).getHours();
      acc[hour] = (acc[hour] || 0) + trend.count;
      return acc;
    }, {} as Record<number, number>);

    const peakHour = Object.entries(hourCounts)
      .reduce((max, [hour, count]) => 
        count > max.count ? { hour: Number(hour), count } : max,
        { hour: 0, count: 0 }
      );

    metrics.peakErrorTime = `${peakHour.hour.toString().padStart(2, '0')}:00`;

    return metrics;
  }

  public getTrends(): ErrorTrend[] {
    return [...this.errorTrends];
  }
}

export const metricsService = new MetricsService();
