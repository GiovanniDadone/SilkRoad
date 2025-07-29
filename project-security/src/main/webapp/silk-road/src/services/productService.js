import { api } from './api';

// Product Services
export const productService = {
    getProducts: async (params = {}) => {
        return await api.get('/products', { params });
    },

    getProductById: async (id) => {
        return await api.get(`/products/${id}`);
    },

    searchProducts: async (query) => {
        return await api.get(`/products/search?q=${query}`);
    }
};