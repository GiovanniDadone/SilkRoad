import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CartItem from './cart-item';
import CartItemDetail from './cart-item-detail';
import CartItemUpdate from './cart-item-update';
import CartItemDeleteDialog from './cart-item-delete-dialog';

const CartItemRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CartItem />} />
    <Route path="new" element={<CartItemUpdate />} />
    <Route path=":id">
      <Route index element={<CartItemDetail />} />
      <Route path="edit" element={<CartItemUpdate />} />
      <Route path="delete" element={<CartItemDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CartItemRoutes;
