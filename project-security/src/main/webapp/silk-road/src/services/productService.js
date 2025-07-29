import apiService from '../services/api.js';
const { api } = apiService;

// Product Services
export const productService = {
    getProducts: async (params = {}) => {
        try {
            return await api.get('/products', { params });
        } catch (error) {
            console.error('Error fetching products:', error);
        }
    },

    getProductById: async (id) => {
        try {
            return await api.get(`/products/${id}`);
        } catch (error) {
            console.error('Error fetching product n°:' `${id}`, error);
        }
    },

    searchProducts: async (query) => {
        try {
            return await api.get(`/products/search?q=${query}`);
        } catch (error) {
            console.error('Error fetching products:', error);

        }
    }
};

