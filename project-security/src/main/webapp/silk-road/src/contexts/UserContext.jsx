import React, { createContext, useContext, useReducer, useEffect } from 'react'
import { authService } from '../services/api'

const UserContext = createContext()

const userReducer = (state, action) => {
  switch (action.type) {
    case 'LOGIN_SUCCESS':
      return {
        ...state,
        user: action.payload.user,
        token: action.payload.token,
        isAuthenticated: true,
        loading: false,
        error: null,
      }

    case 'LOGIN_FAILURE':
      return {
        ...state,
        user: null,
        token: null,
        isAuthenticated: false,
        loading: false,
        error: action.payload,
      }

    case 'LOGOUT':
      return {
        ...state,
        user: null,
        token: null,
        isAuthenticated: false,
        loading: false,
        error: null,
      }

    case 'SET_LOADING':
      return {
        ...state,
        loading: action.payload,
      }

    case 'UPDATE_USER':
      return {
        ...state,
        user: { ...state.user, ...action.payload },
      }

    default:
      return state
  }
}

export const UserProvider = ({ children }) => {
  const [state, dispatch] = useReducer(userReducer, {
    user: null,
    token: null,
    isAuthenticated: false,
    loading: false,
    error: null,
  })

  useEffect(() => {
    const token = localStorage.getItem('silk-road-token')
    const user = localStorage.getItem('silk-road-user')

    if (token && user) {
      dispatch({
        type: 'LOGIN_SUCCESS',
        payload: {
          token,
          user: JSON.parse(user),
        },
      })
    }
  }, [])

  const login = async (email, password) => {
    dispatch({ type: 'SET_LOADING', payload: true })

    try {
      const response = await authService.login(email, password)

      localStorage.setItem('silk-road-token', response.token)
      localStorage.setItem('silk-road-user', JSON.stringify(response.user))

      dispatch({
        type: 'LOGIN_SUCCESS',
        payload: response,
      })

      return response
    } catch (error) {
      dispatch({
        type: 'LOGIN_FAILURE',
        payload: error.message,
      })
      throw error
    }
  }

  const register = async (userData) => {
    dispatch({ type: 'SET_LOADING', payload: true })

    try {
      const response = await authService.register(userData)

      localStorage.setItem('silk-road-token', response.token)
      localStorage.setItem('silk-road-user', JSON.stringify(response.user))

      dispatch({
        type: 'LOGIN_SUCCESS',
        payload: response,
      })

      return response
    } catch (error) {
      dispatch({
        type: 'LOGIN_FAILURE',
        payload: error.message,
      })
      throw error
    }
  }

  const logout = async () => {
    try {
      await authService.logout()
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      localStorage.removeItem('silk-road-token')
      localStorage.removeItem('silk-road-user')
      dispatch({ type: 'LOGOUT' })
    }
  }

  const updateUser = (userData) => {
    dispatch({ type: 'UPDATE_USER', payload: userData })
    localStorage.setItem('silk-road-user', JSON.stringify({ ...state.user, ...userData }))
  }

  return (
    <UserContext.Provider
      value={{
        ...state,
        login,
        register,
        logout,
        updateUser,
      }}>
      {children}
    </UserContext.Provider>
  )
}

export const useUser = () => {
  const context = useContext(UserContext)
  if (!context) {
    throw new Error('useUser must be used within a UserProvider')
  }
  return context
}
