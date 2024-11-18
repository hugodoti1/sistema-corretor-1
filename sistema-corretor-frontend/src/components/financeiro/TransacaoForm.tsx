import React, { useEffect, useState } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Grid,
    SelectChangeEvent
} from '@mui/material';
import { ITransacao } from '../../interfaces/IFinanceiro';
import { FinanceiroService } from '../../services/FinanceiroService';

interface TransacaoFormProps {
    open: boolean;
    onClose: () => void;
    transacao?: ITransacao;
    onSuccess: () => void;
}

const categorias = [
    'Comissão',
    'Honorários',
    'Aluguel',
    'Marketing',
    'Serviços',
    'Impostos',
    'Outros'
];

export const TransacaoForm: React.FC<TransacaoFormProps> = ({
    open,
    onClose,
    transacao,
    onSuccess
}) => {
    const [formData, setFormData] = useState<Partial<ITransacao>>({
        descricao: '',
        valor: 0,
        data: new Date().toISOString().split('T')[0],
        tipo: 'RECEITA',
        categoria: '',
        status: 'PENDENTE'
    });

    useEffect(() => {
        if (transacao) {
            setFormData({
                ...transacao,
                data: new Date(transacao.data).toISOString().split('T')[0]
            });
        }
    }, [transacao]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSelectChange = (e: SelectChangeEvent) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            if (transacao?.id) {
                await FinanceiroService.atualizarTransacao(transacao.id, formData as ITransacao);
            } else {
                await FinanceiroService.criarTransacao(formData as Omit<ITransacao, 'id'>);
            }
            onSuccess();
            onClose();
        } catch (error) {
            console.error('Erro ao salvar transação:', error);
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <form onSubmit={handleSubmit}>
                <DialogTitle>
                    {transacao ? 'Editar Transação' : 'Nova Transação'}
                </DialogTitle>
                <DialogContent>
                    <Grid container spacing={2} sx={{ mt: 1 }}>
                        <Grid item xs={12}>
                            <TextField
                                fullWidth
                                label="Descrição"
                                name="descricao"
                                value={formData.descricao}
                                onChange={handleChange}
                                required
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Valor"
                                name="valor"
                                type="number"
                                value={formData.valor}
                                onChange={handleChange}
                                required
                                inputProps={{ step: "0.01" }}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Data"
                                name="data"
                                type="date"
                                value={formData.data}
                                onChange={handleChange}
                                required
                                InputLabelProps={{ shrink: true }}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <FormControl fullWidth required>
                                <InputLabel>Tipo</InputLabel>
                                <Select
                                    name="tipo"
                                    value={formData.tipo}
                                    label="Tipo"
                                    onChange={handleSelectChange}
                                >
                                    <MenuItem value="RECEITA">Receita</MenuItem>
                                    <MenuItem value="DESPESA">Despesa</MenuItem>
                                </Select>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <FormControl fullWidth required>
                                <InputLabel>Categoria</InputLabel>
                                <Select
                                    name="categoria"
                                    value={formData.categoria}
                                    label="Categoria"
                                    onChange={handleSelectChange}
                                >
                                    {categorias.map(cat => (
                                        <MenuItem key={cat} value={cat}>{cat}</MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12}>
                            <FormControl fullWidth required>
                                <InputLabel>Status</InputLabel>
                                <Select
                                    name="status"
                                    value={formData.status}
                                    label="Status"
                                    onChange={handleSelectChange}
                                >
                                    <MenuItem value="PENDENTE">Pendente</MenuItem>
                                    <MenuItem value="PAGO">Pago</MenuItem>
                                    <MenuItem value="CANCELADO">Cancelado</MenuItem>
                                </Select>
                            </FormControl>
                        </Grid>
                    </Grid>
                </DialogContent>
                <DialogActions>
                    <Button onClick={onClose}>Cancelar</Button>
                    <Button type="submit" variant="contained" color="primary">
                        Salvar
                    </Button>
                </DialogActions>
            </form>
        </Dialog>
    );
};
