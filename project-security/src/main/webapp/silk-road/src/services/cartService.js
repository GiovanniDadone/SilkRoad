import api from './api';

// Cart Services
export const cartService = {
    addItem: async (productId, quantity, size) => {
        try {
            return await api.post('/cart/items', { productId, quantity, size });
        } catch (error) {
            console.error(error);
        }
    },

    removeItem: async (productId, size) => {
        try {
            return await api.delete(`/cart/items/${productId}`, { data: { size } });
        } catch (error) {
            console.error(error);
        }
    },

    updateItem: async (productId, quantity, size) => {
        try {
            return await api.put(`/cart/items/${productId}`, { quantity, size });
        } catch (error) {
            console.error(error);
        }
    },

    getCart: async () => {
        try {
            return await api.get('/cart');
        } catch (error) {
            console.error(error);
        }
    },

    clearCart: async () => {
        try {
            return await api.delete('/cart');
        } catch (error) {
            console.error(error);
        }
    }
};