import React, { useState, useEffect } from 'react';
import {
  Card,
  CardContent,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Box,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  TextField,
  Button,
  CircularProgress,
} from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { useSnackbar } from 'notistack';
import axios from 'axios';
import { format } from 'date-fns';

interface Transaction {
  id: string;
  date: string;
  description: string;
  amount: number;
  type: string;
  bank: string;
  status: string;
}

const BankTransactions: React.FC = () => {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedBank, setSelectedBank] = useState('all');
  const [startDate, setStartDate] = useState<Date | null>(null);
  const [endDate, setEndDate] = useState<Date | null>(null);

  const { enqueueSnackbar } = useSnackbar();

  useEffect(() => {
    loadTransactions();
  }, [selectedBank, startDate, endDate]);

  const loadTransactions = async () => {
    try {
      setLoading(true);
      const params = {
        bank: selectedBank !== 'all' ? selectedBank : undefined,
        startDate: startDate ? format(startDate, 'yyyy-MM-dd') : undefined,
        endDate: endDate ? format(endDate, 'yyyy-MM-dd') : undefined,
      };
      const response = await axios.get('/api/bank/transactions', { params });
      setTransactions(response.data);
    } catch (error) {
      enqueueSnackbar('Erro ao carregar transações', { variant: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const handleSync = async () => {
    try {
      setLoading(true);
      await axios.post('/api/bank/sync');
      enqueueSnackbar('Sincronização iniciada com sucesso', { variant: 'success' });
      await loadTransactions();
    } catch (error) {
      enqueueSnackbar('Erro ao sincronizar transações', { variant: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(value);
  };

  const formatDate = (dateString: string) => {
    return format(new Date(dateString), 'dd/MM/yyyy HH:mm');
  };

  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
          <Typography variant="h5">Transações Bancárias</Typography>
          <Button
            variant="contained"
            onClick={handleSync}
            disabled={loading}
          >
            Sincronizar
          </Button>
        </Box>

        <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
          <FormControl sx={{ minWidth: 200 }}>
            <InputLabel>Banco</InputLabel>
            <Select
              value={selectedBank}
              label="Banco"
              onChange={(e) => setSelectedBank(e.target.value)}
            >
              <MenuItem value="all">Todos</MenuItem>
              <MenuItem value="bb">Banco do Brasil</MenuItem>
              <MenuItem value="inter">Banco Inter</MenuItem>
              <MenuItem value="caixa">Caixa Econômica</MenuItem>
            </Select>
          </FormControl>

          <DatePicker
            label="Data Inicial"
            value={startDate}
            onChange={setStartDate}
          />

          <DatePicker
            label="Data Final"
            value={endDate}
            onChange={setEndDate}
          />
        </Box>

        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
            <CircularProgress />
          </Box>
        ) : (
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Data</TableCell>
                  <TableCell>Banco</TableCell>
                  <TableCell>Descrição</TableCell>
                  <TableCell align="right">Valor</TableCell>
                  <TableCell>Tipo</TableCell>
                  <TableCell>Status</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {transactions.map((transaction) => (
                  <TableRow key={transaction.id}>
                    <TableCell>{formatDate(transaction.date)}</TableCell>
                    <TableCell>{transaction.bank}</TableCell>
                    <TableCell>{transaction.description}</TableCell>
                    <TableCell align="right">{formatCurrency(transaction.amount)}</TableCell>
                    <TableCell>{transaction.type}</TableCell>
                    <TableCell>{transaction.status}</TableCell>
                  </TableRow>
                ))}
                {transactions.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={6} align="center">
                      Nenhuma transação encontrada
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </CardContent>
    </Card>
  );
};

export default BankTransactions;
