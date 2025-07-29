import api from "./api";
// User Services
export const userService = {
    updateProfile: async (userData) => {
        try {
            return await api.put('/user/profile', userData);
        } catch (error) {
            console.error(error);
        }
    },

    changePassword: async (passwordData) => {
        try {
            return await api.put('/user/password', passwordData);
        } catch (error) {
            console.error(error);
        }
    },

    getAddresses: async () => {
        try {
            return await api.get('/user/addresses');
        } catch (error) {
            console.error(error);
        }
    },

    addAddress: async (address) => {
        try {
            return await api.post('/user/addresses', address);

        } catch (error) {
            console.error(error);
        }
    },

    updateAddress: async (id, address) => {
        try {
            return await api.put(`/user/addresses/${id}`, address);
        } catch (error) {
            console.error(error);
        }
    },

    deleteAddress: async (id) => {
        try {
            return await api.delete(`/user/addresses/${id}`);
        } catch (error) {
            console.error(error);
        }
    }
};