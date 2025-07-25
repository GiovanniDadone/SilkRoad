export interface ICustomer {
  id?: number;
  firstName?: string;
  lastName?: string;
  email?: string;
  phoneNumber?: string | null;
  address?: string | null;
}

export const defaultValue: Readonly<ICustomer> = {};
