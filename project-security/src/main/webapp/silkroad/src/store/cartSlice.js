import { createSlice } from '@reduxjs/toolkit';

const cartSlice = createSlice({
  name: 'cart',
  initialState: {
    items: [],
    totalItems: 0,
    totalPrice: 0,
    loading: false,
    error: null,
  },
  reducers: {
    setCart: (state, action) => {
      state.items = action.payload.items || [];
      state.totalItems = action.payload.totalItems || 0;
      state.totalPrice = action.payload.totalPrice || 0;
    },
    setLoading: (state, action) => {
      state.loading = action.payload;
    },
    setError: (state, action) => {
      state.error = action.payload;
    },
    clearCart: (state) => {
      state.items = [];
      state.totalItems = 0;
      state.totalPrice = 0;
    },
  },
});

export const { setCart, setLoading, setError, clearCart } = cartSlice.actions;
export default cartSlice.reducer;
