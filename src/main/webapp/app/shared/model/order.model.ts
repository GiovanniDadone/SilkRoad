import dayjs from 'dayjs';
import { ICustomer } from 'app/shared/model/customer.model';
import { OrderStatus } from 'app/shared/model/enumerations/order-status.model';

export interface IOrder {
  id?: number;
  orderDate?: dayjs.Dayjs;
  totalAmount?: number;
  status?: keyof typeof OrderStatus | null;
  customer?: ICustomer;
}

export const defaultValue: Readonly<IOrder> = {};
