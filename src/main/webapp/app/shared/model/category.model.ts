export interface ICategory {
  id?: number;
  name?: string;
  description?: string | null;
}

export const defaultValue: Readonly<ICategory> = {};
