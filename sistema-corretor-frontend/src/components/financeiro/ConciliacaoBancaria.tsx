import React, { useState, useEffect } from 'react';
import {
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Typography,
    Button,
    Box,
    Chip,
    IconButton,
    Grid,
    FormControl,
    InputLabel,
    Select,
    MenuItem
} from '@mui/material';
import { Link as LinkIcon, Refresh as RefreshIcon } from '@mui/icons-material';
import { ITransacaoBancaria } from '../../interfaces/IBanco';
import { ITransacao } from '../../interfaces/IFinanceiro';
import { BancoService } from '../../services/BancoService';
import { FinanceiroService } from '../../services/FinanceiroService';

interface ConciliacaoBancariaProps {
    contaId: number;
}

export const ConciliacaoBancaria: React.FC<ConciliacaoBancariaProps> = ({ contaId }) => {
    const [transacoesBancarias, setTransacoesBancarias] = useState<ITransacaoBancaria[]>([]);
    const [transacoesSistema, setTransacoesSistema] = useState<ITransacao[]>([]);
    const [dataInicio, setDataInicio] = useState(
        new Date(new Date().setDate(1)).toISOString().split('T')[0]
    );
    const [dataFim, setDataFim] = useState(
        new Date().toISOString().split('T')[0]
    );

    useEffect(() => {
        carregarDados();
    }, [contaId, dataInicio, dataFim]);

    const carregarDados = async () => {
        try {
            const [extratoResponse, transacoesResponse] = await Promise.all([
                BancoService.obterExtrato(contaId, dataInicio, dataFim),
                FinanceiroService.listarTransacoes()
            ]);

            setTransacoesBancarias(extratoResponse.transacoes);
            setTransacoesSistema(transacoesResponse);
        } catch (error) {
            console.error('Erro ao carregar dados para conciliação:', error);
        }
    };

    const handleSincronizar = async () => {
        try {
            await BancoService.sincronizarConta(contaId);
            await carregarDados();
        } catch (error) {
            console.error('Erro ao sincronizar conta:', error);
        }
    };

    const handleConciliar = async (transacaoBancariaId: number, transacaoSistemaId: number) => {
        try {
            await BancoService.conciliarTransacao(transacaoBancariaId, transacaoSistemaId);
            await carregarDados();
        } catch (error) {
            console.error('Erro ao conciliar transação:', error);
        }
    };

    const formatarValor = (valor: number) => {
        return new Intl.NumberFormat('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        }).format(valor);
    };

    return (
        <Box>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                <Typography variant="h6">Conciliação Bancária</Typography>
                <Button
                    variant="contained"
                    color="primary"
                    startIcon={<RefreshIcon />}
                    onClick={handleSincronizar}
                >
                    Sincronizar
                </Button>
            </Box>

            <Grid container spacing={2} mb={3}>
                <Grid item xs={12} sm={6}>
                    <TextField
                        fullWidth
                        label="Data Início"
                        type="date"
                        value={dataInicio}
                        onChange={(e) => setDataInicio(e.target.value)}
                        InputLabelProps={{ shrink: true }}
                    />
                </Grid>
                <Grid item xs={12} sm={6}>
                    <TextField
                        fullWidth
                        label="Data Fim"
                        type="date"
                        value={dataFim}
                        onChange={(e) => setDataFim(e.target.value)}
                        InputLabelProps={{ shrink: true }}
                    />
                </Grid>
            </Grid>

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Data</TableCell>
                            <TableCell>Descrição</TableCell>
                            <TableCell align="right">Valor</TableCell>
                            <TableCell>Tipo</TableCell>
                            <TableCell>Status</TableCell>
                            <TableCell>Ações</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {transacoesBancarias.map((transacao) => (
                            <TableRow key={transacao.id}>
                                <TableCell>{new Date(transacao.data).toLocaleDateString()}</TableCell>
                                <TableCell>{transacao.descricao}</TableCell>
                                <TableCell align="right">{formatarValor(transacao.valor)}</TableCell>
                                <TableCell>
                                    <Chip
                                        label={transacao.tipo}
                                        color={transacao.tipo === 'CREDITO' ? 'success' : 'error'}
                                        size="small"
                                    />
                                </TableCell>
                                <TableCell>
                                    <Chip
                                        label={transacao.conciliado ? 'Conciliado' : 'Pendente'}
                                        color={transacao.conciliado ? 'success' : 'warning'}
                                        size="small"
                                    />
                                </TableCell>
                                <TableCell>
                                    {!transacao.conciliado && (
                                        <FormControl fullWidth size="small">
                                            <Select
                                                displayEmpty
                                                onChange={(e) => handleConciliar(transacao.id!, Number(e.target.value))}
                                            >
                                                <MenuItem value="" disabled>
                                                    Conciliar com...
                                                </MenuItem>
                                                {transacoesSistema
                                                    .filter(t => !t.conciliado)
                                                    .map(t => (
                                                        <MenuItem key={t.id} value={t.id}>
                                                            {t.descricao} - {formatarValor(t.valor)}
                                                        </MenuItem>
                                                    ))}
                                            </Select>
                                        </FormControl>
                                    )}
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    );
};
