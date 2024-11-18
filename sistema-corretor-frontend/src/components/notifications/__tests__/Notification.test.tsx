import React from 'react';
import { render, screen, act, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Notification } from '../Notification';
import { notificationService } from '../../../services/notificationService';

jest.useFakeTimers();

describe('Notification Component', () => {
  beforeEach(() => {
    render(<Notification />);
  });

  afterEach(() => {
    jest.clearAllTimers();
  });

  it('deve renderizar uma notificação quando receber um evento', async () => {
    act(() => {
      notificationService.notify('Mensagem de teste');
    });

    const alert = await screen.findByRole('alert');
    expect(alert).toBeInTheDocument();
    expect(alert).toHaveTextContent('Mensagem de teste');
  });

  it('deve fechar a notificação quando clicar no botão fechar', async () => {
    act(() => {
      notificationService.notify('Mensagem de teste');
    });

    const alert = await screen.findByRole('alert');
    const closeButton = screen.getByTitle('Fechar');
    
    await userEvent.click(closeButton);
    
    await waitFor(() => {
      expect(alert).not.toBeVisible();
    });
  });

  it('deve mostrar notificações com diferentes severidades', async () => {
    act(() => {
      notificationService.notifyError('Erro teste');
    });

    let alert = await screen.findByRole('alert');
    expect(alert).toHaveClass('MuiAlert-standardError');

    act(() => {
      notificationService.notifySuccess('Sucesso teste');
    });

    alert = await screen.findByRole('alert');
    expect(alert).toHaveClass('MuiAlert-standardSuccess');
  });

  it('deve fechar automaticamente após o tempo definido', async () => {
    act(() => {
      notificationService.notify('Mensagem teste', { autoHideDuration: 2000 });
    });

    const alert = await screen.findByRole('alert');
    expect(alert).toBeVisible();

    act(() => {
      jest.advanceTimersByTime(2000);
    });

    await waitFor(() => {
      expect(alert).not.toBeVisible();
    });
  });

  it('não deve fechar ao clicar fora da notificação', async () => {
    act(() => {
      notificationService.notify('Mensagem teste');
    });

    const alert = await screen.findByRole('alert');
    
    act(() => {
      // Simula clique fora da notificação
      userEvent.click(document.body);
    });

    expect(alert).toBeVisible();
  });
});
