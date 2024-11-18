import React, { useEffect, useState } from 'react';
import {
    Container,
    Paper,
    Typography,
    Button,
    Grid,
    Box,
    Card,
    CardContent
} from '@mui/material';
import { Add as AddIcon } from '@mui/icons-material';
import { TransacaoList } from '../components/financeiro/TransacaoList';
import { TransacaoForm } from '../components/financeiro/TransacaoForm';
import { ITransacao, IBalanco } from '../interfaces/IFinanceiro';
import { FinanceiroService } from '../services/FinanceiroService';

export const Financeiro: React.FC = () => {
    const [openForm, setOpenForm] = useState(false);
    const [selectedTransacao, setSelectedTransacao] = useState<ITransacao | undefined>();
    const [balanco, setBalanco] = useState<IBalanco>({
        totalReceitas: 0,
        totalDespesas: 0,
        saldoAtual: 0
    });

    useEffect(() => {
        carregarBalanco();
    }, []);

    const carregarBalanco = async () => {
        try {
            const data = await FinanceiroService.obterBalanco();
            setBalanco(data);
        } catch (error) {
            console.error('Erro ao carregar balanço:', error);
        }
    };

    const handleAddClick = () => {
        setSelectedTransacao(undefined);
        setOpenForm(true);
    };

    const handleEditClick = (transacao: ITransacao) => {
        setSelectedTransacao(transacao);
        setOpenForm(true);
    };

    const handleCloseForm = () => {
        setOpenForm(false);
        setSelectedTransacao(undefined);
    };

    const handleSuccess = () => {
        carregarBalanco();
    };

    const formatarValor = (valor: number) => {
        return new Intl.NumberFormat('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        }).format(valor);
    };

    return (
        <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                        <Typography variant="h4" component="h1">
                            Gestão Financeira
                        </Typography>
                        <Button
                            variant="contained"
                            color="primary"
                            startIcon={<AddIcon />}
                            onClick={handleAddClick}
                        >
                            Nova Transação
                        </Button>
                    </Box>
                </Grid>

                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Typography color="textSecondary" gutterBottom>
                                Total de Receitas
                            </Typography>
                            <Typography variant="h5" component="div" color="success.main">
                                {formatarValor(balanco.totalReceitas)}
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>

                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Typography color="textSecondary" gutterBottom>
                                Total de Despesas
                            </Typography>
                            <Typography variant="h5" component="div" color="error.main">
                                {formatarValor(balanco.totalDespesas)}
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>

                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Typography color="textSecondary" gutterBottom>
                                Saldo Atual
                            </Typography>
                            <Typography
                                variant="h5"
                                component="div"
                                color={balanco.saldoAtual >= 0 ? 'success.main' : 'error.main'}
                            >
                                {formatarValor(balanco.saldoAtual)}
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>

                <Grid item xs={12}>
                    <Paper sx={{ p: 2 }}>
                        <TransacaoList onEdit={handleEditClick} />
                    </Paper>
                </Grid>
            </Grid>

            <TransacaoForm
                open={openForm}
                onClose={handleCloseForm}
                transacao={selectedTransacao}
                onSuccess={handleSuccess}
            />
        </Container>
    );
};
