import React, { useEffect, useState } from 'react';
import {
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    IconButton,
    Typography,
    Box,
    Chip
} from '@mui/material';
import { Edit as EditIcon, Delete as DeleteIcon } from '@mui/icons-material';
import { ITransacao } from '../../interfaces/IFinanceiro';
import { FinanceiroService } from '../../services/FinanceiroService';

interface TransacaoListProps {
    onEdit: (transacao: ITransacao) => void;
}

export const TransacaoList: React.FC<TransacaoListProps> = ({ onEdit }) => {
    const [transacoes, setTransacoes] = useState<ITransacao[]>([]);

    useEffect(() => {
        carregarTransacoes();
    }, []);

    const carregarTransacoes = async () => {
        try {
            const data = await FinanceiroService.listarTransacoes();
            setTransacoes(data);
        } catch (error) {
            console.error('Erro ao carregar transações:', error);
        }
    };

    const handleDelete = async (id: number) => {
        if (window.confirm('Tem certeza que deseja excluir esta transação?')) {
            try {
                await FinanceiroService.deletarTransacao(id);
                await carregarTransacoes();
            } catch (error) {
                console.error('Erro ao deletar transação:', error);
            }
        }
    };

    const formatarValor = (valor: number) => {
        return new Intl.NumberFormat('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        }).format(valor);
    };

    const getStatusColor = (status: string) => {
        switch (status) {
            case 'PAGO':
                return 'success';
            case 'PENDENTE':
                return 'warning';
            case 'CANCELADO':
                return 'error';
            default:
                return 'default';
        }
    };

    return (
        <Box>
            <Typography variant="h6" gutterBottom>
                Transações Financeiras
            </Typography>
            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Data</TableCell>
                            <TableCell>Descrição</TableCell>
                            <TableCell>Categoria</TableCell>
                            <TableCell>Tipo</TableCell>
                            <TableCell align="right">Valor</TableCell>
                            <TableCell>Status</TableCell>
                            <TableCell align="center">Ações</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {transacoes.map((transacao) => (
                            <TableRow key={transacao.id}>
                                <TableCell>{new Date(transacao.data).toLocaleDateString()}</TableCell>
                                <TableCell>{transacao.descricao}</TableCell>
                                <TableCell>{transacao.categoria}</TableCell>
                                <TableCell>
                                    <Chip
                                        label={transacao.tipo}
                                        color={transacao.tipo === 'RECEITA' ? 'success' : 'error'}
                                        size="small"
                                    />
                                </TableCell>
                                <TableCell align="right">{formatarValor(transacao.valor)}</TableCell>
                                <TableCell>
                                    <Chip
                                        label={transacao.status}
                                        color={getStatusColor(transacao.status)}
                                        size="small"
                                    />
                                </TableCell>
                                <TableCell align="center">
                                    <IconButton
                                        size="small"
                                        onClick={() => onEdit(transacao)}
                                        color="primary"
                                    >
                                        <EditIcon />
                                    </IconButton>
                                    <IconButton
                                        size="small"
                                        onClick={() => transacao.id && handleDelete(transacao.id)}
                                        color="error"
                                    >
                                        <DeleteIcon />
                                    </IconButton>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    );
};
