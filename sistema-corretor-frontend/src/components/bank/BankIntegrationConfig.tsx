import React, { useState, useEffect } from 'react';
import {
  Card,
  CardContent,
  Typography,
  Grid,
  TextField,
  Button,
  Box,
  Tab,
  Tabs,
  CircularProgress,
  Stack,
} from '@mui/material';
import { useSnackbar } from 'notistack';
import axios from 'axios';
import { useBankError } from '../../hooks/useBankError';
import BankError from './BankError';

interface BankConfig {
  clientId: string;
  clientSecret: string;
  certificatePath: string;
  certificatePassword: string;
  webhookUrl: string;
}

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`bank-tabpanel-${index}`}
      aria-labelledby={`bank-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );
}

const BankIntegrationConfig: React.FC = () => {
  const [activeTab, setActiveTab] = useState(0);
  const [loading, setLoading] = useState(false);
  const { errors, addError, clearErrors, toggleErrorExpansion, isErrorExpanded } = useBankError();
  const [bbConfig, setBbConfig] = useState<BankConfig>({
    clientId: '',
    clientSecret: '',
    certificatePath: '',
    certificatePassword: '',
    webhookUrl: '',
  });
  const [interConfig, setInterConfig] = useState<BankConfig>({
    clientId: '',
    clientSecret: '',
    certificatePath: '',
    certificatePassword: '',
    webhookUrl: '',
  });
  const [caixaConfig, setCaixaConfig] = useState<BankConfig>({
    clientId: '',
    clientSecret: '',
    certificatePath: '',
    certificatePassword: '',
    webhookUrl: '',
  });

  const { enqueueSnackbar } = useSnackbar();

  useEffect(() => {
    loadConfigurations();
  }, []);

  const loadConfigurations = async () => {
    try {
      setLoading(true);
      clearErrors();
      const response = await axios.get('/api/bank/config');
      const { bb, inter, caixa } = response.data;
      setBbConfig(bb);
      setInterConfig(inter);
      setCaixaConfig(caixa);
    } catch (error) {
      addError(error);
    } finally {
      setLoading(false);
    }
  };

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setActiveTab(newValue);
  };

  const handleSave = async (bank: string, config: BankConfig) => {
    try {
      setLoading(true);
      await axios.post(`/api/bank/config/${bank}`, config);
      enqueueSnackbar('Configurações salvas com sucesso', { variant: 'success' });
      await loadConfigurations();
    } catch (error) {
      addError(error);
    } finally {
      setLoading(false);
    }
  };

  const handleTestConnection = async (bank: string) => {
    try {
      setLoading(true);
      await axios.post(`/api/bank/test-connection/${bank}`);
      enqueueSnackbar('Conexão testada com sucesso', { variant: 'success' });
    } catch (error) {
      addError(error);
    } finally {
      setLoading(false);
    }
  };

  const renderBankConfigForm = (
    bank: string,
    config: BankConfig,
    setConfig: React.Dispatch<React.SetStateAction<BankConfig>>
  ) => (
    <Grid container spacing={2}>
      <Grid item xs={12}>
        <TextField
          fullWidth
          label="Client ID"
          value={config.clientId}
          onChange={(e) => setConfig({ ...config, clientId: e.target.value })}
        />
      </Grid>
      <Grid item xs={12}>
        <TextField
          fullWidth
          label="Client Secret"
          type="password"
          value={config.clientSecret}
          onChange={(e) => setConfig({ ...config, clientSecret: e.target.value })}
        />
      </Grid>
      <Grid item xs={12}>
        <TextField
          fullWidth
          label="Caminho do Certificado"
          value={config.certificatePath}
          onChange={(e) => setConfig({ ...config, certificatePath: e.target.value })}
        />
      </Grid>
      <Grid item xs={12}>
        <TextField
          fullWidth
          label="Senha do Certificado"
          type="password"
          value={config.certificatePassword}
          onChange={(e) => setConfig({ ...config, certificatePassword: e.target.value })}
        />
      </Grid>
      <Grid item xs={12}>
        <TextField
          fullWidth
          label="URL do Webhook"
          value={config.webhookUrl}
          onChange={(e) => setConfig({ ...config, webhookUrl: e.target.value })}
        />
      </Grid>
      <Grid item xs={12}>
        <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
          <Button
            variant="outlined"
            onClick={() => handleTestConnection(bank)}
            disabled={loading}
          >
            Testar Conexão
          </Button>
          <Button
            variant="contained"
            onClick={() => handleSave(bank, config)}
            disabled={loading}
          >
            Salvar
          </Button>
        </Box>
      </Grid>
    </Grid>
  );

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Card>
      <CardContent>
        <Typography variant="h5" gutterBottom>
          Configuração de Integrações Bancárias
        </Typography>

        {errors.length > 0 && (
          <Stack spacing={1} sx={{ mb: 3 }}>
            {errors.map((error, index) => (
              <BankError
                key={index}
                error={error}
                expanded={isErrorExpanded(index)}
                onExpand={() => toggleErrorExpansion(index)}
              />
            ))}
          </Stack>
        )}

        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <Tabs value={activeTab} onChange={handleTabChange}>
            <Tab label="Banco do Brasil" />
            <Tab label="Banco Inter" />
            <Tab label="Caixa Econômica" />
          </Tabs>
        </Box>
        <TabPanel value={activeTab} index={0}>
          {renderBankConfigForm('bb', bbConfig, setBbConfig)}
        </TabPanel>
        <TabPanel value={activeTab} index={1}>
          {renderBankConfigForm('inter', interConfig, setInterConfig)}
        </TabPanel>
        <TabPanel value={activeTab} index={2}>
          {renderBankConfigForm('caixa', caixaConfig, setCaixaConfig)}
        </TabPanel>
      </CardContent>
    </Card>
  );
};

export default BankIntegrationConfig;
