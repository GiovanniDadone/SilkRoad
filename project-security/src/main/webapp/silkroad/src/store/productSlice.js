import { createSlice } from '@reduxjs/toolkit';

const productsSlice = createSlice({
  name: 'products',
  initialState: {
    items: [],
    loading: false,
    error: null,
    totalPages: 0,
    totalElements: 0,
    currentPage: 0,
  },
  reducers: {
    setProducts: (state, action) => {
      state.items = action.payload.content || [];
      state.totalPages = action.payload.totalPages || 0;
      state.totalElements = action.payload.totalElements || 0;
      state.currentPage = action.payload.number || 0;
    },
    setLoading: (state, action) => {
      state.loading = action.payload;
    },
    setError: (state, action) => {
      state.error = action.payload;
    },
  },
});

export const { setProducts, setLoading, setError } = productsSlice.actions;
export default productsSlice.reducer;