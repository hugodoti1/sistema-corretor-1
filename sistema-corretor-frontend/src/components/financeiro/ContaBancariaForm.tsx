import React, { useState } from 'react';
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
    Grid
} from '@mui/material';
import { IContaBancaria } from '../../interfaces/IBanco';

interface ContaBancariaFormProps {
    open: boolean;
    onClose: () => void;
    conta?: IContaBancaria;
    onSuccess: () => void;
}

const bancos = [
    { codigo: '001', nome: 'Banco do Brasil' },
    { codigo: '341', nome: 'Itaú' },
    { codigo: '033', nome: 'Santander' },
    { codigo: '104', nome: 'Caixa Econômica' },
    { codigo: '237', nome: 'Bradesco' },
    // Adicione mais bancos conforme necessário
];

export const ContaBancariaForm: React.FC<ContaBancariaFormProps> = ({
    open,
    onClose,
    conta,
    onSuccess
}) => {
    const [formData, setFormData] = useState<Partial<IContaBancaria>>(
        conta || {
            banco: '',
            agencia: '',
            conta: '',
            tipo: 'CORRENTE',
            saldo: 0
        }
    );

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSelectChange = (e: any) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            // Aqui você chamaria o serviço para salvar a conta
            onSuccess();
            onClose();
        } catch (error) {
            console.error('Erro ao salvar conta bancária:', error);
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <form onSubmit={handleSubmit}>
                <DialogTitle>
                    {conta ? 'Editar Conta Bancária' : 'Nova Conta Bancária'}
                </DialogTitle>
                <DialogContent>
                    <Grid container spacing={2} sx={{ mt: 1 }}>
                        <Grid item xs={12}>
                            <FormControl fullWidth required>
                                <InputLabel>Banco</InputLabel>
                                <Select
                                    name="banco"
                                    value={formData.banco}
                                    label="Banco"
                                    onChange={handleSelectChange}
                                >
                                    {bancos.map(banco => (
                                        <MenuItem key={banco.codigo} value={banco.codigo}>
                                            {banco.nome}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Agência"
                                name="agencia"
                                value={formData.agencia}
                                onChange={handleChange}
                                required
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                label="Conta"
                                name="conta"
                                value={formData.conta}
                                onChange={handleChange}
                                required
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <FormControl fullWidth required>
                                <InputLabel>Tipo de Conta</InputLabel>
                                <Select
                                    name="tipo"
                                    value={formData.tipo}
                                    label="Tipo de Conta"
                                    onChange={handleSelectChange}
                                >
                                    <MenuItem value="CORRENTE">Conta Corrente</MenuItem>
                                    <MenuItem value="POUPANCA">Conta Poupança</MenuItem>
                                </Select>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                fullWidth
                                label="Saldo Inicial"
                                name="saldo"
                                type="number"
                                value={formData.saldo}
                                onChange={handleChange}
                                required
                                inputProps={{ step: "0.01" }}
                            />
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
