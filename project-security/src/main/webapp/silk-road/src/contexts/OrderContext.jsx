import React, { createContext, useContext, useReducer, useEffect } from 'react'
import { orderService } from '../services/api'

const OrderContext = createContext()

const orderReducer = (state, action) => {
  switch (action.type) {
    case 'SET_ORDERS':
      return {
        ...state,
        orders: action.payload,
        loading: false,
      }

    case 'ADD_ORDER':
      return {
        ...state,
        orders: [action.payload, ...state.orders],
      }

    case 'UPDATE_ORDER':
      return {
        ...state,
        orders: state.orders.map((order) =>
          order.id === action.payload.id ? action.payload : order
        ),
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

    default:
      return state
  }
}

export const OrderProvider = ({ children }) => {
  const [state, dispatch] = useReducer(orderReducer, {
    orders: [],
    loading: false,
    error: null,
  })

  const fetchOrders = async () => {
    dispatch({ type: 'SET_LOADING', payload: true })

    try {
      const orders = await orderService.getOrders()
      dispatch({ type: 'SET_ORDERS', payload: orders })
    } catch (error) {
      dispatch({ type: 'SET_ERROR', payload: error.message })
    }
  }

  const createOrder = async (orderData) => {
    dispatch({ type: 'SET_LOADING', payload: true })

    try {
      const newOrder = await orderService.createOrder(orderData)
      dispatch({ type: 'ADD_ORDER', payload: newOrder })
      return newOrder
    } catch (error) {
      dispatch({ type: 'SET_ERROR', payload: error.message })
      throw error
    }
  }

  const getOrderById = (id) => {
    return state.orders.find((order) => order.id === parseInt(id))
  }

  useEffect(() => {
    fetchOrders()
  }, [])

  return (
    <OrderContext.Provider
      value={{
        ...state,
        fetchOrders,
        createOrder,
        getOrderById,
      }}>
      {children}
    </OrderContext.Provider>
  )
}

export const useOrder = () => {
  const context = useContext(OrderContext)
  if (!context) {
    throw new Error('useOrder must be used within an OrderProvider')
  }
  return context
}
