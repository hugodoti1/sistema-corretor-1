import React, { useState, useMemo } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  TextField,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Stack,
  IconButton,
  Tooltip,
  Grid,
  Chip,
  Badge,
  Button,
} from '@mui/material';
import {
  FilterList as FilterIcon,
  Clear as ClearIcon,
  DeleteSweep as DeleteSweepIcon,
  Assessment as AssessmentIcon,
  Timeline as TimelineIcon,
} from '@mui/icons-material';
import BankError from './BankError';
import { useBankError } from '../../hooks/useBankError';
import {
  ErrorSeverity,
  ErrorCategory,
  getErrorCategory,
  categoryLabels,
  severityLabels,
  getErrorSeverity,
  getBankName,
} from '../../types/bankError';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

interface BankErrorListProps {
  title?: string;
  showControls?: boolean;
  maxHeight?: string | number;
}

type FilterSeverity = 'all' | ErrorSeverity;
type FilterCategory = 'all' | ErrorCategory;

const BankErrorList: React.FC<BankErrorListProps> = ({
  title = 'Erros Bancários',
  showControls = true,
  maxHeight = '70vh',
}) => {
  const {
    errors,
    stats,
    clearErrors,
    removeError,
    toggleErrorExpansion,
    isErrorExpanded,
  } = useBankError();
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedBank, setSelectedBank] = useState<string>('all');
  const [selectedSeverity, setSelectedSeverity] = useState<FilterSeverity>('all');
  const [selectedCategory, setSelectedCategory] = useState<FilterCategory>('all');
  const [showStats, setShowStats] = useState(false);

  const banks = useMemo(() => {
    const bankSet = new Set(['all']);
    errors.forEach((error) => {
      if (error.bank) bankSet.add(error.bank.toLowerCase());
    });
    return Array.from(bankSet);
  }, [errors]);

  const filteredErrors = useMemo(() => {
    return errors.filter((error) => {
      const matchesSearch =
        searchTerm === '' ||
        error.message.toLowerCase().includes(searchTerm.toLowerCase()) ||
        error.code.toLowerCase().includes(searchTerm.toLowerCase()) ||
        (error.details || '').toLowerCase().includes(searchTerm.toLowerCase());

      const matchesBank =
        selectedBank === 'all' ||
        error.bank?.toLowerCase() === selectedBank.toLowerCase();

      const matchesSeverity =
        selectedSeverity === 'all' ||
        getErrorSeverity(error.code) === selectedSeverity;

      const matchesCategory =
        selectedCategory === 'all' ||
        getErrorCategory(error.code) === selectedCategory;

      return matchesSearch && matchesBank && matchesSeverity && matchesCategory;
    });
  }, [errors, searchTerm, selectedBank, selectedSeverity, selectedCategory]);

  const handleClearFilters = () => {
    setSearchTerm('');
    setSelectedBank('all');
    setSelectedSeverity('all');
    setSelectedCategory('all');
  };

  const formatDate = (timestamp: number | null) => {
    if (!timestamp) return 'N/A';
    return format(new Date(timestamp), "d 'de' MMMM 'às' HH:mm", { locale: ptBR });
  };

  return (
    <Card>
      <CardContent>
        <Box sx={{ mb: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Typography variant="h6">{title}</Typography>
            <Badge
              badgeContent={stats.total}
              color="primary"
              sx={{ '& .MuiBadge-badge': { fontSize: '0.8rem' } }}
            >
              <TimelineIcon color="action" />
            </Badge>
          </Box>
          {showControls && (
            <Box sx={{ display: 'flex', gap: 1 }}>
              <Tooltip title="Visualizar estatísticas">
                <Button
                  size="small"
                  startIcon={<AssessmentIcon />}
                  onClick={() => setShowStats(!showStats)}
                  color="primary"
                  variant={showStats ? 'contained' : 'outlined'}
                >
                  Estatísticas
                </Button>
              </Tooltip>
              <Tooltip title="Limpar todos os erros">
                <IconButton onClick={clearErrors} size="small" color="error">
                  <DeleteSweepIcon />
                </IconButton>
              </Tooltip>
            </Box>
          )}
        </Box>

        {showStats && (
          <Box sx={{ mb: 3, p: 2, bgcolor: 'grey.50', borderRadius: 1 }}>
            <Grid container spacing={2}>
              <Grid item xs={12} md={6}>
                <Typography variant="subtitle2" gutterBottom>
                  Distribuição por Banco
                </Typography>
                <Stack spacing={1}>
                  {Object.entries(stats.byBank).map(([bank, count]) => (
                    <Box
                      key={bank}
                      sx={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                      }}
                    >
                      <Typography variant="body2">{getBankName(bank)}</Typography>
                      <Chip
                        size="small"
                        label={count}
                        color="primary"
                        variant="outlined"
                      />
                    </Box>
                  ))}
                </Stack>
              </Grid>
              <Grid item xs={12} md={6}>
                <Typography variant="subtitle2" gutterBottom>
                  Distribuição por Severidade
                </Typography>
                <Stack spacing={1}>
                  {Object.entries(stats.bySeverity).map(([severity, count]) => (
                    <Box
                      key={severity}
                      sx={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                      }}
                    >
                      <Typography variant="body2">
                        {severityLabels[severity as ErrorSeverity]}
                      </Typography>
                      <Chip
                        size="small"
                        label={count}
                        color={
                          severity === 'error'
                            ? 'error'
                            : severity === 'warning'
                            ? 'warning'
                            : 'info'
                        }
                        variant="outlined"
                      />
                    </Box>
                  ))}
                </Stack>
              </Grid>
              <Grid item xs={12}>
                <Typography variant="subtitle2" gutterBottom>
                  Distribuição por Categoria
                </Typography>
                <Stack spacing={1}>
                  {Object.entries(stats.byCategory).map(([category, count]) => (
                    <Box
                      key={category}
                      sx={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                      }}
                    >
                      <Typography variant="body2">
                        {categoryLabels[category as ErrorCategory] ||
                          'Categoria Desconhecida'}
                      </Typography>
                      <Chip size="small" label={count} variant="outlined" />
                    </Box>
                  ))}
                </Stack>
              </Grid>
              <Grid item xs={12}>
                <Box sx={{ mt: 2 }}>
                  <Typography variant="body2" color="text.secondary">
                    Primeiro erro: {formatDate(stats.firstError)}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Último erro: {formatDate(stats.lastError)}
                  </Typography>
                </Box>
              </Grid>
            </Grid>
          </Box>
        )}

        {showControls && (
          <>
            <Grid container spacing={2} sx={{ mb: 3 }}>
              <Grid item xs={12} sm={6} md={3}>
                <TextField
                  fullWidth
                  size="small"
                  label="Buscar"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  InputProps={{
                    endAdornment: searchTerm && (
                      <IconButton
                        size="small"
                        onClick={() => setSearchTerm('')}
                        sx={{ mr: -1 }}
                      >
                        <ClearIcon fontSize="small" />
                      </IconButton>
                    ),
                  }}
                />
              </Grid>
              <Grid item xs={12} sm={6} md={3}>
                <FormControl fullWidth size="small">
                  <InputLabel>Banco</InputLabel>
                  <Select
                    value={selectedBank}
                    label="Banco"
                    onChange={(e) => setSelectedBank(e.target.value)}
                  >
                    <MenuItem value="all">Todos os Bancos</MenuItem>
                    {banks.map((bank) => (
                      bank !== 'all' && (
                        <MenuItem key={bank} value={bank}>
                          {getBankName(bank)}
                        </MenuItem>
                      )
                    ))}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={6} md={3}>
                <FormControl fullWidth size="small">
                  <InputLabel>Severidade</InputLabel>
                  <Select
                    value={selectedSeverity}
                    label="Severidade"
                    onChange={(e) => setSelectedSeverity(e.target.value as FilterSeverity)}
                  >
                    <MenuItem value="all">Todas</MenuItem>
                    {Object.entries(severityLabels).map(([value, label]) => (
                      <MenuItem key={value} value={value}>
                        {label}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12} sm={6} md={3}>
                <FormControl fullWidth size="small">
                  <InputLabel>Categoria</InputLabel>
                  <Select
                    value={selectedCategory}
                    label="Categoria"
                    onChange={(e) => setSelectedCategory(e.target.value as FilterCategory)}
                  >
                    <MenuItem value="all">Todas</MenuItem>
                    {Object.entries(categoryLabels).map(([value, label]) => (
                      <MenuItem key={value} value={value}>
                        {label}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
            </Grid>

            <Box sx={{ mb: 2, display: 'flex', gap: 1, flexWrap: 'wrap' }}>
              {(searchTerm || selectedBank !== 'all' || selectedSeverity !== 'all' || selectedCategory !== 'all') && (
                <>
                  <Chip
                    size="small"
                    icon={<FilterIcon />}
                    label={`${filteredErrors.length} resultado${
                      filteredErrors.length === 1 ? '' : 's'
                    }`}
                  />
                  <Chip
                    size="small"
                    icon={<ClearIcon />}
                    label="Limpar filtros"
                    onClick={handleClearFilters}
                    color="secondary"
                  />
                </>
              )}
            </Box>
          </>
        )}

        <Box
          sx={{
            maxHeight,
            overflowY: 'auto',
            px: 1,
            mx: -1,
          }}
        >
          <Stack spacing={1}>
            {filteredErrors.map((error) => (
              <BankError
                key={error.id}
                error={error}
                expanded={isErrorExpanded(error.id)}
                onExpand={() => toggleErrorExpansion(error.id)}
                onDelete={() => removeError(error.id)}
              />
            ))}
            {filteredErrors.length === 0 && (
              <Typography
                variant="body2"
                color="text.secondary"
                sx={{ textAlign: 'center', py: 4 }}
              >
                Nenhum erro encontrado
                {showControls && ' com os filtros selecionados'}
              </Typography>
            )}
          </Stack>
        </Box>
      </CardContent>
    </Card>
  );
};

export default BankErrorList;
