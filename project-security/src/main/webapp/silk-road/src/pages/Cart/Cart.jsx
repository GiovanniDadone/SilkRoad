import React from 'react'
import { useCart } from '../../hooks/useCart'
import { Link } from 'react-router-dom'

const Cart = () => {
  const { cartItems, addToCart, removeFromCart, clearCart, getTotal } = useCart()

  return (
    <div style={{ padding: '20px' }}>
      <h1>Shopping Cart</h1>

      {cartItems.length === 0 ? (
        <div style={{ textAlign: 'center', marginTop: '50px' }}>
          <p>Your cart is empty</p>
          <Link to='/' style={buttonStyle}>
            Continue Shopping
          </Link>
        </div>
      ) : (
        <div>
          <div style={{ marginBottom: '20px' }}>
            {cartItems.map((item) => (
              <div key={item.id} style={itemStyle}>
                <h3>{item.name}</h3>
                <p>Price: ${item.price}</p>
                <p>Quantity: {item.quantity}</p>
                <button onClick={() => removeFromCart(item.id)} style={removeButtonStyle}>
                  Remove
                </button>
              </div>
            ))}
          </div>

          <div style={{ borderTop: '1px solid #ccc', paddingTop: '20px' }}>
            <h3>Total: ${getTotal()}</h3>
            <div style={{ display: 'flex', gap: '10px', marginTop: '20px' }}>
              <button onClick={clearCart} style={clearButtonStyle}>
                Clear Cart
              </button>
              <Link to='/checkout' style={buttonStyle}>
                Proceed to Checkout
              </Link>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

const itemStyle = {
  border: '1px solid #ddd',
  padding: '15px',
  marginBottom: '10px',
  borderRadius: '5px',
}

const buttonStyle = {
  padding: '10px 20px',
  backgroundColor: '#28a745',
  color: 'white',
  textDecoration: 'none',
  borderRadius: '5px',
  display: 'inline-block',
  border: 'none',
  cursor: 'pointer',
}

const removeButtonStyle = {
  padding: '5px 10px',
  backgroundColor: '#dc3545',
  color: 'white',
  border: 'none',
  borderRadius: '3px',
  cursor: 'pointer',
}

const clearButtonStyle = {
  padding: '10px 20px',
  backgroundColor: '#6c757d',
  color: 'white',
  border: 'none',
  borderRadius: '5px',
  cursor: 'pointer',
}

export default Cart
