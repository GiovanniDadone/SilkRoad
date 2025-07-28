import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor to add auth token
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('silk-road-token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Response interceptor for error handling
api.interceptors.response.use(
    (response) => response.data,
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('silk-road-token');
            localStorage.removeItem('silk-road-user');
            window.location.href = '/user';
        }
        return Promise.reject(error.response?.data || error.message);
    }
);

// Auth Services
export const authService = {
    login: async (email, password) => {
        return await api.post('/auth/login', { email, password });
    },

    register: async (userData) => {
        return await api.post('/auth/register', userData);
    },

    logout: async () => {
        return await api.post('/auth/logout');
    },

    getCurrentUser: async () => {
        return await api.get('/auth/me');
    }
};

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

// User Services
export const userService = {
    updateProfile: async (userData) => {
        return await api.put('/user/profile', userData);
    },

    changePassword: async (passwordData) => {
        return await api.put('/user/password', passwordData);
    },

    getAddresses: async () => {
        return await api.get('/user/addresses');
    },

    addAddress: async (address) => {
        return await api.post('/user/addresses', address);
    },

    updateAddress: async (id, address) => {
        return await api.put(`/user/addresses/${id}`, address);
    },

    deleteAddress: async (id) => {
        return await api.delete(`/user/addresses/${id}`);
    }
};

export default api;