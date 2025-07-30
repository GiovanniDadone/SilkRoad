import api from './api';

export const productService = {
  getAllProducts: async (params = {}) => {
    const response = await api.get('/products', { params });
    return response.data;
  },

  getProductById: async (id) => {
    const response = await api.get(`/products/${id}`);
    return response.data;
  },

  searchProducts: async (keyword, params = {}) => {
    const response = await api.get(`/products/search/advanced`, {
      params: { keyword, ...params }
    });
    return response.data;
  },

  getProductsByCategory: async (categoryId, params = {}) => {
    const response = await api.get(`/products/category/${categoryId}`, { params });
    return response.data;
  },

  filterProducts: async (filters) => {
    const response = await api.post('/products/filter', filters);
    return response.data;
  }
};