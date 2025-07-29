// authService.js
import api from './api';

// Auth Services
export const authService = {
    login: async (email, password) => {
        try {
            return await api.post('/auth/login', { email, password });
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    register: async (userData) => {
        try {
            return await api.post('/auth/register', userData);
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    logout: async () => {
        try {
            return await api.post('/auth/logout');
        } catch (error) {
            throw error.response?.data || error.message;
        }
    },

    getCurrentUser: async () => {
        try {
            return await api.get('/auth/me');
        } catch (error) {
            throw error.response?.data || error.message;
        }
    }
};