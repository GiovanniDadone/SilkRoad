import { ICart } from 'app/shared/model/cart.model';
import { IProduct } from 'app/shared/model/product.model';

export interface ICartItem {
  id?: number;
  quantity?: number;
  cart?: ICart;
  product?: IProduct;
}

export const defaultValue: Readonly<ICartItem> = {};
