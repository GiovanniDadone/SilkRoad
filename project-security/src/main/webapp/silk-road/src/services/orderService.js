import api from "./api";

// Order Services
export const orderService = {
    createOrder: async (orderData) => {
        try {
            return await api.post('/orders', orderData);
        } catch (error) {
            console.error(error);
        }
    },

    getOrders: async () => {
        try {
            return await api.get('/orders');
        } catch (error) {
            console.error(error);
        }
    },

    getOrderById: async (id) => {
        try {
            return await api.get(`/orders/${id}`);
        } catch (error) {
            console.error(error);
        }

    },

    updateOrderStatus: async (id, status) => {
        try {
            return await api.put(`/orders/${id}/status`, { status });
        } catch (error) {
            console.error(error);
        }
    }
};