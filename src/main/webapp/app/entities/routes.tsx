import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Order from './order';
import Category from './category';
import Product from './product';
import Customer from './customer';
import Cart from './cart';
import CartItem from './cart-item';
import OrderItem from './order-item';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="order/*" element={<Order />} />
        <Route path="category/*" element={<Category />} />
        <Route path="product/*" element={<Product />} />
        <Route path="customer/*" element={<Customer />} />
        <Route path="cart/*" element={<Cart />} />
        <Route path="cart-item/*" element={<CartItem />} />
        <Route path="order-item/*" element={<OrderItem />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
