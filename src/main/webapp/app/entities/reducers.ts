import order from 'app/entities/order/order.reducer';
import category from 'app/entities/category/category.reducer';
import product from 'app/entities/product/product.reducer';
import customer from 'app/entities/customer/customer.reducer';
import cart from 'app/entities/cart/cart.reducer';
import cartItem from 'app/entities/cart-item/cart-item.reducer';
import orderItem from 'app/entities/order-item/order-item.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  order,
  category,
  product,
  customer,
  cart,
  cartItem,
  orderItem,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
