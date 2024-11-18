import React from 'react';
import {
  Alert,
  AlertTitle,
  Box,
  Card,
  CardContent,
  Collapse,
  IconButton,
  Typography,
  Tooltip,
} from '@mui/material';
import {
  Error as ErrorIcon,
  Warning as WarningIcon,
  Info as InfoIcon,
  ExpandMore as ExpandMoreIcon,
  Delete as DeleteIcon,
} from '@mui/icons-material';
import {
  BankError as IBankError,
  ErrorSeverity,
  getErrorSeverity,
  getBankName,
} from '../../types/bankError';
import { StoredBankError } from '../../services/bankErrorStorage';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

interface BankErrorProps {
  error: StoredBankError;
  expanded?: boolean;
  onExpand?: () => void;
  onDelete?: () => void;
  showTimestamp?: boolean;
}

const getErrorIcon = (severity: ErrorSeverity) => {
  switch (severity) {
    case 'error':
      return <ErrorIcon color="error" />;
    case 'warning':
      return <WarningIcon color="warning" />;
    case 'info':
      return <InfoIcon color="info" />;
  }
};

const formatErrorTimestamp = (timestamp: number) => {
  return format(new Date(timestamp), "d 'de' MMMM 'às' HH:mm", { locale: ptBR });
};

const BankError: React.FC<BankErrorProps> = ({
  error,
  expanded = false,
  onExpand,
  onDelete,
  showTimestamp = true,
}) => {
  const severity = getErrorSeverity(error.code);
  const bankName = getBankName(error.bank);

  return (
    <Card variant="outlined">
      <Alert
        severity={severity}
        icon={getErrorIcon(severity)}
        action={
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
            {error.details && (
              <IconButton
                aria-label="show more"
                size="small"
                onClick={onExpand}
                sx={{
                  transform: expanded ? 'rotate(180deg)' : 'rotate(0deg)',
                  transition: 'transform 0.2s',
                }}
              >
                <ExpandMoreIcon />
              </IconButton>
            )}
            {onDelete && (
              <Tooltip title="Remover erro">
                <IconButton
                  aria-label="delete error"
                  size="small"
                  onClick={onDelete}
                  sx={{ ml: 1 }}
                >
                  <DeleteIcon />
                </IconButton>
              </Tooltip>
            )}
          </Box>
        }
      >
        <AlertTitle>
          {bankName} - {error.code}
          {showTimestamp && (
            <Typography
              variant="caption"
              component="span"
              sx={{ ml: 1, color: 'text.secondary' }}
            >
              {formatErrorTimestamp(error.timestamp)}
            </Typography>
          )}
        </AlertTitle>
        {error.message}
      </Alert>
      {error.details && (
        <Collapse in={expanded}>
          <CardContent>
            <Typography variant="body2" color="text.secondary">
              Detalhes do Erro:
            </Typography>
            <Box
              sx={{
                mt: 1,
                p: 1,
                bgcolor: 'grey.100',
                borderRadius: 1,
                fontFamily: 'monospace',
              }}
            >
              <Typography variant="body2">{error.details}</Typography>
            </Box>
          </CardContent>
        </Collapse>
      )}
    </Card>
  );
};

export default BankError;
export type { IBankError as BankError };

// Exemplo de uso:
/*
import BankError from './BankError';

const MyComponent = () => {
  const [expanded, setExpanded] = useState(false);
  const error = {
    code: 'ERR-AUTH-001',
    message: 'Token de autenticação expirado',
    bank: 'bb',
    details: 'OAuth token expired at 2023-10-20T15:30:00Z'
  };

  return (
    <BankError
      error={error}
      expanded={expanded}
      onExpand={() => setExpanded(!expanded)}
    />
  );
};
*/
