import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Order from './order';
import OrderDetail from './order-detail';
import OrderUpdate from './order-update';
import OrderDeleteDialog from './order-delete-dialog';

const OrderRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Order />} />
    <Route path="new" element={<OrderUpdate />} />
    <Route path=":id">
      <Route index element={<OrderDetail />} />
      <Route path="edit" element={<OrderUpdate />} />
      <Route path="delete" element={<OrderDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default OrderRoutes;
