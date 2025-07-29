import api from './api';

// Cart Services
export const cartService = {
    addItem: async (productId, quantity, size) => {
        return await api.post('/cart/items', { productId, quantity, size });
    },

    removeItem: async (productId, size) => {
        return await api.delete(`/cart/items/${productId}`, { data: { size } });
    },

    updateItem: async (productId, quantity, size) => {
        return await api.put(`/cart/items/${productId}`, { quantity, size });
    },

    getCart: async () => {
        return await api.get('/cart');
    },

    clearCart: async () => {
        return await api.delete('/cart');
    }
};