import React from 'react';
import { Routes, Route } from 'react-router-dom';
import BankIntegrationConfig from '../components/bank/BankIntegrationConfig';
import BankTransactions from '../components/bank/BankTransactions';
import ClienteForm from '../components/forms/ClienteForm';
import Layout from '../components/Layout';

const AppRoutes: React.FC = () => {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        <Route index element={<ClienteForm />} />
        <Route path="clientes" element={<ClienteForm />} />
        <Route path="bank">
          <Route path="config" element={<BankIntegrationConfig />} />
          <Route path="transactions" element={<BankTransactions />} />
        </Route>
      </Route>
    </Routes>
  );
};

export default AppRoutes;
