// src/services/orderService.js
import api from './api';

export const orderService = {
  // Crea un nuovo ordine dal carrello
  createOrder: async (orderData) => {
    const response = await api.post('/orders', orderData);
    return response.data;
  },

  // Recupera gli ordini dell'utente corrente
  getMyOrders: async (params = {}) => {
    const response = await api.get('/orders/my-orders', { params });
    return response.data;
  },

  // Recupera un ordine specifico dell'utente
  getMyOrderById: async (orderId) => {
    const response = await api.get(`/orders/my-orders/${orderId}`);
    return response.data;
  },

  // Cancella un ordine
  cancelMyOrder: async (orderId, reason = 'Cancellato dall\'utente') => {
    const response = await api.post(`/orders/my-orders/${orderId}/cancel`, { reason });
    return response.data;
  },

  // Recupera il totale speso
  getMyOrdersTotal: async () => {
    const response = await api.get('/orders/my-orders/total');
    return response.data;
  },

  // Traccia un ordine tramite tracking number
  trackOrder: async (trackingNumber) => {
    const response = await api.get(`/orders/track/${trackingNumber}`);
    return response.data;
  },

  // Admin endpoints (se l'utente è admin)
  getAllOrders: async (params = {}) => {
    const response = await api.get('/orders', { params });
    return response.data;
  },

  getOrderById: async (orderId) => {
    const response = await api.get(`/orders/${orderId}`);
    return response.data;
  },

  getOrdersByStatus: async (status, params = {}) => {
    const response = await api.get(`/orders/by-status/${status}`, { params });
    return response.data;
  },

  updateOrderStatus: async (orderId, statusData) => {
    const response = await api.patch(`/orders/${orderId}/status`, statusData);
    return response.data;
  },

  getOrdersToProcess: async () => {
    const response = await api.get('/orders/to-process');
    return response.data;
  },

  getOrdersToShip: async () => {
    const response = await api.get('/orders/to-ship');
    return response.data;
  },

  generateSalesReport: async (startDate, endDate) => {
    const response = await api.get('/orders/reports/sales', {
      params: { startDate, endDate }
    });
    return response.data;
  }
};