import api from "./api";

// Order Services
export const orderService = {
    createOrder: async (orderData) => {
        return await api.post('/orders', orderData);
    },

    getOrders: async () => {
        return await api.get('/orders');
    },

    getOrderById: async (id) => {
        return await api.get(`/orders/${id}`);
    },

    updateOrderStatus: async (id, status) => {
        return await api.put(`/orders/${id}/status`, { status });
    }
};