import React, { useEffect, useState } from 'react';
import { Alert, AlertColor, Snackbar } from '@mui/material';
import { notificationService, NotificationPayload } from '../../services/notificationService';

const severityLabels: Record<AlertColor, string> = {
  error: 'Erro',
  warning: 'Atenção',
  info: 'Informação',
  success: 'Sucesso'
};

export const Notification: React.FC = () => {
  const [open, setOpen] = useState(false);
  const [message, setMessage] = useState('');
  const [severity, setSeverity] = useState<AlertColor>('info');
  const [autoHideDuration, setAutoHideDuration] = useState(6000);

  useEffect(() => {
    const unsubscribe = notificationService.subscribe((notification: NotificationPayload) => {
      setMessage(notification.message);
      setSeverity((notification.options?.variant || 'info') as AlertColor);
      setAutoHideDuration(notification.options?.autoHideDuration || 6000);
      setOpen(true);
    });

    return () => unsubscribe();
  }, []);

  const handleClose = (event?: React.SyntheticEvent | Event, reason?: string) => {
    if (reason === 'clickaway') return;
    setOpen(false);
  };

  return (
    <Snackbar
      open={open}
      autoHideDuration={autoHideDuration}
      onClose={handleClose}
      anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
    >
      <Alert 
        onClose={handleClose} 
        severity={severity} 
        sx={{ width: '100%' }}
        closeText="Fechar"
      >
        {message}
      </Alert>
    </Snackbar>
  );
};
