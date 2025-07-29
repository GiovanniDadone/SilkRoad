import api from "./api";
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