import { ICategory } from 'app/shared/model/category.model';

export interface IProduct {
  id?: number;
  name?: string;
  description?: string | null;
  price?: number;
  stockQuantity?: number;
  imageUrl?: string | null;
  isActive?: boolean | null;
  category?: ICategory;
}

export const defaultValue: Readonly<IProduct> = {
  isActive: false,
};
