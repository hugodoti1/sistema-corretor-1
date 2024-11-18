/**
 * Códigos de erro comuns entre os bancos
 */
export enum CommonBankErrorCode {
  // Erros de Autenticação (1xxx)
  INVALID_CREDENTIALS = 1000,
  TOKEN_EXPIRED = 1001,
  TOKEN_INVALID = 1002,
  SESSION_EXPIRED = 1003,
  ACCOUNT_LOCKED = 1004,
  ACCOUNT_DISABLED = 1005,
  MFA_REQUIRED = 1006,
  MFA_FAILED = 1007,
  DEVICE_NOT_REGISTERED = 1008,

  // Erros de Conexão (2xxx)
  CONNECTION_FAILED = 2000,
  TIMEOUT = 2001,
  SERVICE_UNAVAILABLE = 2002,
  RATE_LIMIT_EXCEEDED = 2003,
  MAINTENANCE_MODE = 2004,
  DNS_ERROR = 2005,
  SSL_ERROR = 2006,
  PROXY_ERROR = 2007,

  // Erros de Validação (3xxx)
  INVALID_INPUT = 3000,
  MISSING_FIELD = 3001,
  INVALID_FORMAT = 3002,
  INVALID_LENGTH = 3003,
  INVALID_VALUE = 3004,
  DUPLICATE_ENTRY = 3005,
  BUSINESS_RULE_VIOLATION = 3006,

  // Erros de Conta (4xxx)
  ACCOUNT_NOT_FOUND = 4000,
  INSUFFICIENT_FUNDS = 4001,
  ACCOUNT_TYPE_INVALID = 4002,
  ACCOUNT_INACTIVE = 4003,
  ACCOUNT_BLOCKED = 4004,
  LIMIT_EXCEEDED = 4005,
  INVALID_BRANCH = 4006,
  INVALID_ACCOUNT_NUMBER = 4007,

  // Erros de Transação (5xxx)
  TRANSACTION_FAILED = 5000,
  TRANSACTION_NOT_FOUND = 5001,
  TRANSACTION_EXPIRED = 5002,
  TRANSACTION_CANCELLED = 5003,
  TRANSACTION_DUPLICATE = 5004,
  TRANSACTION_LIMIT_EXCEEDED = 5005,
  INVALID_TRANSACTION_TYPE = 5006,

  // Erros de Pagamento (6xxx)
  PAYMENT_FAILED = 6000,
  PAYMENT_NOT_FOUND = 6001,
  PAYMENT_EXPIRED = 6002,
  PAYMENT_CANCELLED = 6003,
  PAYMENT_REJECTED = 6004,
  PAYMENT_LIMIT_EXCEEDED = 6005,
  INVALID_PAYMENT_TYPE = 6006,
  INVALID_BARCODE = 6007,
  INVALID_RECIPIENT = 6008,
  PAYMENT_ALREADY_PROCESSED = 6009,
  PAYMENT_SCHEDULE_INVALID = 6010,
  PAYMENT_DATE_INVALID = 6011,

  // Erros de Documento (7xxx)
  DOCUMENT_NOT_FOUND = 7000,
  DOCUMENT_EXPIRED = 7001,
  DOCUMENT_INVALID = 7002,
  DOCUMENT_PROCESSING = 7003,
  DOCUMENT_REJECTED = 7004,
  INVALID_DOCUMENT_TYPE = 7005,
  DOCUMENT_LIMIT_EXCEEDED = 7006,

  // Erros de Sistema (8xxx)
  SYSTEM_ERROR = 8000,
  DATABASE_ERROR = 8001,
  CACHE_ERROR = 8002,
  INTEGRATION_ERROR = 8003,
  CONFIGURATION_ERROR = 8004,
  VERSION_MISMATCH = 8005,
  FEATURE_DISABLED = 8006,
  DEPENDENCY_ERROR = 8007,

  // Erros de Segurança (9xxx)
  SECURITY_VIOLATION = 9000,
  ACCESS_DENIED = 9001,
  INVALID_IP = 9002,
  BLOCKED_REGION = 9003,
  SUSPICIOUS_ACTIVITY = 9004,
  ENCRYPTION_ERROR = 9005,
  INVALID_SIGNATURE = 9006,
  FRAUD_DETECTED = 9007,

  // Erros Específicos de Negócio (10xxx)
  BUSINESS_HOUR_INVALID = 10000,
  PRODUCT_UNAVAILABLE = 10001,
  SERVICE_DISABLED = 10002,
  CUSTOMER_BLOCKED = 10003,
  CONTRACT_EXPIRED = 10004,
  INVALID_STATUS = 10005,
  OPERATION_NOT_SUPPORTED = 10006
}

/**
 * Interface para códigos de erro específicos de cada banco
 */
export interface BankSpecificErrorCodes {
  [bankName: string]: {
    [code: string]: {
      message: string;
      category: string;
      severity: 'high' | 'medium' | 'low';
      commonCode?: CommonBankErrorCode;
    };
  };
}

/**
 * Mapeamento de códigos de erro específicos por banco
 */
export const bankErrorCodes: BankSpecificErrorCodes = {
  // Banco do Brasil
  'Banco do Brasil': {
    // Erros de Autenticação
    'BB-AUTH-001': {
      message: 'Credenciais inválidas',
      category: 'AUTHENTICATION',
      severity: 'high',
      commonCode: CommonBankErrorCode.INVALID_CREDENTIALS
    },
    'BB-AUTH-002': {
      message: 'Sessão expirada',
      category: 'AUTHENTICATION',
      severity: 'medium',
      commonCode: CommonBankErrorCode.SESSION_EXPIRED
    },
    'BB-AUTH-003': {
      message: 'Dispositivo não registrado',
      category: 'AUTHENTICATION',
      severity: 'high',
      commonCode: CommonBankErrorCode.DEVICE_NOT_REGISTERED
    },

    // Erros de Conta
    'BB-ACC-001': {
      message: 'Conta não encontrada',
      category: 'ACCOUNT',
      severity: 'high',
      commonCode: CommonBankErrorCode.ACCOUNT_NOT_FOUND
    },
    'BB-ACC-002': {
      message: 'Saldo insuficiente',
      category: 'ACCOUNT',
      severity: 'high',
      commonCode: CommonBankErrorCode.INSUFFICIENT_FUNDS
    },
    'BB-ACC-003': {
      message: 'Conta bloqueada',
      category: 'ACCOUNT',
      severity: 'high',
      commonCode: CommonBankErrorCode.ACCOUNT_BLOCKED
    },

    // Erros de Pagamento
    'BB-PAY-001': {
      message: 'Pagamento não autorizado',
      category: 'PAYMENT',
      severity: 'high',
      commonCode: CommonBankErrorCode.PAYMENT_REJECTED
    },
    'BB-PAY-002': {
      message: 'Código de barras inválido',
      category: 'PAYMENT',
      severity: 'medium',
      commonCode: CommonBankErrorCode.INVALID_BARCODE
    },
    'BB-PAY-003': {
      message: 'Data de pagamento inválida',
      category: 'PAYMENT',
      severity: 'medium',
      commonCode: CommonBankErrorCode.PAYMENT_DATE_INVALID
    },

    // Erros de Transação
    'BB-TRX-001': {
      message: 'Transação não encontrada',
      category: 'TRANSACTION',
      severity: 'medium',
      commonCode: CommonBankErrorCode.TRANSACTION_NOT_FOUND
    },
    'BB-TRX-002': {
      message: 'Limite diário excedido',
      category: 'TRANSACTION',
      severity: 'high',
      commonCode: CommonBankErrorCode.TRANSACTION_LIMIT_EXCEEDED
    },
    'BB-TRX-003': {
      message: 'Transação duplicada',
      category: 'TRANSACTION',
      severity: 'medium',
      commonCode: CommonBankErrorCode.TRANSACTION_DUPLICATE
    }
  },

  // Bradesco
  'Bradesco': {
    // Erros de Autenticação
    'BRA-AUTH-001': {
      message: 'Usuário bloqueado',
      category: 'AUTHENTICATION',
      severity: 'high',
      commonCode: CommonBankErrorCode.ACCOUNT_LOCKED
    },
    'BRA-AUTH-002': {
      message: 'Token inválido',
      category: 'AUTHENTICATION',
      severity: 'high',
      commonCode: CommonBankErrorCode.TOKEN_INVALID
    },
    'BRA-AUTH-003': {
      message: 'MFA requerido',
      category: 'AUTHENTICATION',
      severity: 'medium',
      commonCode: CommonBankErrorCode.MFA_REQUIRED
    },

    // Erros de Conta
    'BRA-ACC-001': {
      message: 'Agência inválida',
      category: 'ACCOUNT',
      severity: 'high',
      commonCode: CommonBankErrorCode.INVALID_BRANCH
    },
    'BRA-ACC-002': {
      message: 'Conta inativa',
      category: 'ACCOUNT',
      severity: 'high',
      commonCode: CommonBankErrorCode.ACCOUNT_INACTIVE
    },
    'BRA-ACC-003': {
      message: 'Tipo de conta inválido',
      category: 'ACCOUNT',
      severity: 'medium',
      commonCode: CommonBankErrorCode.ACCOUNT_TYPE_INVALID
    },

    // Erros de Pagamento
    'BRA-PAY-001': {
      message: 'Beneficiário inválido',
      category: 'PAYMENT',
      severity: 'high',
      commonCode: CommonBankErrorCode.INVALID_RECIPIENT
    },
    'BRA-PAY-002': {
      message: 'Pagamento vencido',
      category: 'PAYMENT',
      severity: 'medium',
      commonCode: CommonBankErrorCode.PAYMENT_EXPIRED
    },
    'BRA-PAY-003': {
      message: 'Tipo de pagamento inválido',
      category: 'PAYMENT',
      severity: 'medium',
      commonCode: CommonBankErrorCode.INVALID_PAYMENT_TYPE
    }
  },

  // Itaú
  'Itau': {
    // Erros de Autenticação
    'ITAU-AUTH-001': {
      message: 'Senha expirada',
      category: 'AUTHENTICATION',
      severity: 'high',
      commonCode: CommonBankErrorCode.TOKEN_EXPIRED
    },
    'ITAU-AUTH-002': {
      message: 'Acesso bloqueado',
      category: 'AUTHENTICATION',
      severity: 'high',
      commonCode: CommonBankErrorCode.ACCESS_DENIED
    },
    'ITAU-AUTH-003': {
      message: 'Validação adicional necessária',
      category: 'AUTHENTICATION',
      severity: 'medium',
      commonCode: CommonBankErrorCode.MFA_REQUIRED
    },

    // Erros de Sistema
    'ITAU-SYS-001': {
      message: 'Sistema indisponível',
      category: 'SYSTEM',
      severity: 'high',
      commonCode: CommonBankErrorCode.SERVICE_UNAVAILABLE
    },
    'ITAU-SYS-002': {
      message: 'Erro de integração',
      category: 'SYSTEM',
      severity: 'high',
      commonCode: CommonBankErrorCode.INTEGRATION_ERROR
    },
    'ITAU-SYS-003': {
      message: 'Manutenção programada',
      category: 'SYSTEM',
      severity: 'medium',
      commonCode: CommonBankErrorCode.MAINTENANCE_MODE
    },

    // Erros de Negócio
    'ITAU-BUS-001': {
      message: 'Horário não permitido',
      category: 'BUSINESS',
      severity: 'medium',
      commonCode: CommonBankErrorCode.BUSINESS_HOUR_INVALID
    },
    'ITAU-BUS-002': {
      message: 'Serviço indisponível',
      category: 'BUSINESS',
      severity: 'high',
      commonCode: CommonBankErrorCode.SERVICE_DISABLED
    },
    'ITAU-BUS-003': {
      message: 'Operação não suportada',
      category: 'BUSINESS',
      severity: 'medium',
      commonCode: CommonBankErrorCode.OPERATION_NOT_SUPPORTED
    }
  },

  // Santander
  'Santander': {
    // Erros de Autenticação
    'STD-AUTH-001': {
      message: 'Credenciais inválidas',
      category: 'AUTHENTICATION',
      severity: 'high',
      commonCode: CommonBankErrorCode.INVALID_CREDENTIALS
    },
    'STD-AUTH-002': {
      message: 'Token expirado',
      category: 'AUTHENTICATION',
      severity: 'medium',
      commonCode: CommonBankErrorCode.TOKEN_EXPIRED
    },
    'STD-AUTH-003': {
      message: 'Conta desativada',
      category: 'AUTHENTICATION',
      severity: 'high',
      commonCode: CommonBankErrorCode.ACCOUNT_DISABLED
    },

    // Erros de Segurança
    'STD-SEC-001': {
      message: 'Atividade suspeita detectada',
      category: 'SECURITY',
      severity: 'high',
      commonCode: CommonBankErrorCode.SUSPICIOUS_ACTIVITY
    },
    'STD-SEC-002': {
      message: 'IP não autorizado',
      category: 'SECURITY',
      severity: 'high',
      commonCode: CommonBankErrorCode.INVALID_IP
    },
    'STD-SEC-003': {
      message: 'Região bloqueada',
      category: 'SECURITY',
      severity: 'high',
      commonCode: CommonBankErrorCode.BLOCKED_REGION
    },

    // Erros de Validação
    'STD-VAL-001': {
      message: 'Campo obrigatório ausente',
      category: 'VALIDATION',
      severity: 'medium',
      commonCode: CommonBankErrorCode.MISSING_FIELD
    },
    'STD-VAL-002': {
      message: 'Formato inválido',
      category: 'VALIDATION',
      severity: 'medium',
      commonCode: CommonBankErrorCode.INVALID_FORMAT
    },
    'STD-VAL-003': {
      message: 'Valor inválido',
      category: 'VALIDATION',
      severity: 'medium',
      commonCode: CommonBankErrorCode.INVALID_VALUE
    }
  }
};

/**
 * Função para obter detalhes de um código de erro específico de banco
 */
export function getBankErrorDetails(bankName: string, code: string) {
  return bankErrorCodes[bankName]?.[code];
}

/**
 * Função para verificar se um código de erro específico existe para um banco
 */
export function hasBankErrorCode(bankName: string, code: string): boolean {
  return !!bankErrorCodes[bankName]?.[code];
}

/**
 * Função para obter o código de erro comum correspondente
 */
export function getCommonErrorCode(bankName: string, code: string): CommonBankErrorCode | undefined {
  return bankErrorCodes[bankName]?.[code]?.commonCode;
}

/**
 * Função para obter a mensagem de erro formatada
 */
export function formatBankErrorMessage(bankName: string, code: string): string {
  const details = getBankErrorDetails(bankName, code);
  return details ? `[${code}] ${details.message}` : code;
}
