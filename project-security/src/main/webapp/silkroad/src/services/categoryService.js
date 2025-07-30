import api from './api';

export const categoryService = {
  getAllCategories: async () => {
    const response = await api.get('/categories');
    return response.data;
  },

  getCategoryTree: async () => {
    const response = await api.get('/categories/tree');
    return response.data;
  },

  getCategoryById: async (id) => {
    const response = await api.get(`/categories/${id}`);
    return response.data;
  },

  getRootCategories: async () => {
    const response = await api.get('/categories/roots');
    return response.data;
  },

  getSubcategories: async (parentId) => {
    const response = await api.get(`/categories/${parentId}/subcategories`);
    return response.data;
  },

  getCategoriesWithProducts: async () => {
    const response = await api.get('/categories/with-products');
    return response.data;
  }
};