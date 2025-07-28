import React, { createContext, useContext, useReducer, useEffect } from 'react'
import { productService } from '../services/api'

const ProductContext = createContext()

const productReducer = (state, action) => {
  switch (action.type) {
    case 'SET_PRODUCTS':
      return {
        ...state,
        products: action.payload.products,
        totalProducts: action.payload.total,
        loading: false,
        error: null,
      }

    case 'SET_LOADING':
      return {
        ...state,
        loading: action.payload,
      }

    case 'SET_ERROR':
      return {
        ...state,
        error: action.payload,
        loading: false,
      }

    case 'SET_FILTERS':
      return {
        ...state,
        filters: { ...state.filters, ...action.payload },
        currentPage: 1,
      }

    case 'SET_SORT':
      return {
        ...state,
        sortBy: action.payload.sortBy,
        sortOrder: action.payload.sortOrder,
        currentPage: 1,
      }

    case 'SET_PAGE':
      return {
        ...state,
        currentPage: action.payload,
      }

    case 'SET_SEARCH_QUERY':
      return {
        ...state,
        searchQuery: action.payload,
        currentPage: 1,
      }

    case 'ADD_PRODUCT':
      return {
        ...state,
        products: [action.payload, ...state.products],
      }

    case 'UPDATE_PRODUCT':
      return {
        ...state,
        products: state.products.map((product) =>
          product.id === action.payload.id ? action.payload : product
        ),
      }

    case 'DELETE_PRODUCT':
      return {
        ...state,
        products: state.products.filter((product) => product.id !== action.payload),
      }

    default:
      return state
  }
}

const initialState = {
  products: [],
  totalProducts: 0,
  loading: false,
  error: null,
  currentPage: 1,
  productsPerPage: 12,
  searchQuery: '',
  filters: {
    category: '',
    minPrice: '',
    maxPrice: '',
    inStock: false,
  },
  sortBy: 'name',
  sortOrder: 'asc',
}

export const ProductProvider = ({ children }) => {
  const [state, dispatch] = useReducer(productReducer, initialState)

  const fetchProducts = async (params = {}) => {
    dispatch({ type: 'SET_LOADING', payload: true })

    try {
      const queryParams = {
        page: state.currentPage,
        limit: state.productsPerPage,
        search: state.searchQuery,
        sortBy: state.sortBy,
        sortOrder: state.sortOrder,
        ...state.filters,
        ...params,
      }

      // Remove empty values
      Object.keys(queryParams).forEach((key) => {
        if (
          queryParams[key] === '' ||
          queryParams[key] === null ||
          queryParams[key] === undefined
        ) {
          delete queryParams[key]
        }
      })

      const response = await productService.getProducts(queryParams)

      dispatch({
        type: 'SET_PRODUCTS',
        payload: {
          products: response.products || response,
          total: response.total || response.length,
        },
      })
    } catch (error) {
      dispatch({ type: 'SET_ERROR', payload: error.message })
    }
  }

  const searchProducts = async (query) => {
    dispatch({ type: 'SET_SEARCH_QUERY', payload: query })

    if (query.trim()) {
      dispatch({ type: 'SET_LOADING', payload: true })

      try {
        const results = await productService.searchProducts(query)
        dispatch({
          type: 'SET_PRODUCTS',
          payload: {
            products: results.products || results,
            total: results.total || results.length,
          },
        })
      } catch (error) {
        dispatch({ type: 'SET_ERROR', payload: error.message })
      }
    } else {
      fetchProducts()
    }
  }

  const setFilters = (filters) => {
    dispatch({ type: 'SET_FILTERS', payload: filters })
  }

  const setSorting = (sortBy, sortOrder) => {
    dispatch({ type: 'SET_SORT', payload: { sortBy, sortOrder } })
  }

  const setPage = (page) => {
    dispatch({ type: 'SET_PAGE', payload: page })
  }

  const clearFilters = () => {
    dispatch({
      type: 'SET_FILTERS',
      payload: {
        category: '',
        minPrice: '',
        maxPrice: '',
        inStock: false,
      },
    })
    dispatch({ type: 'SET_SEARCH_QUERY', payload: '' })
  }

  // Fetch products when filters, sorting, or page changes
  useEffect(() => {
    fetchProducts()
  }, [state.currentPage, state.filters, state.sortBy, state.sortOrder])

  return (
    <ProductContext.Provider
      value={{
        ...state,
        fetchProducts,
        searchProducts,
        setFilters,
        setSorting,
        setPage,
        clearFilters,
      }}>
      {children}
    </ProductContext.Provider>
  )
}

export const useProducts = () => {
  const context = useContext(ProductContext)
  if (!context) {
    throw new Error('useProducts must be used within a ProductProvider')
  }
  return context
}
