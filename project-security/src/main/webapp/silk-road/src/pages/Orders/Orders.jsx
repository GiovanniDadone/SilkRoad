import React from 'react'
import { useOrder } from '../../hooks/useOrder'
import { Link } from 'react-router-dom'

const Orders = () => {
  const { orders } = useOrder()

  return (
    <div style={{ padding: '20px' }}>
      <h1>My Orders</h1>

      {orders.length === 0 ? (
        <div style={{ textAlign: 'center', marginTop: '50px' }}>
          <p>No orders found</p>
          <Link to='/' style={buttonStyle}>
            Start Shopping
          </Link>
        </div>
      ) : (
        <div>
          {orders.map((order) => (
            <div key={order.id} style={orderStyle}>
              <div style={orderHeaderStyle}>
                <h3>Order #{order.id}</h3>
                <span style={statusStyle}>Status: {order.status || 'Processing'}</span>
              </div>

              <div style={orderDetailsStyle}>
                <p>
                  <strong>Date:</strong> {new Date(order.orderDate).toLocaleDateString()}
                </p>
                <p>
                  <strong>Total:</strong> ${order.total}
                </p>
                <p>
                  <strong>Items:</strong> {order.items.length}
                </p>
              </div>

              <div style={orderItemsStyle}>
                <h4>Items:</h4>
                {order.items.map((item, index) => (
                  <div key={index} style={itemRowStyle}>
                    <span>
                      {item.name} x {item.quantity}
                    </span>
                    <span>${(item.price * item.quantity).toFixed(2)}</span>
                  </div>
                ))}
              </div>

              <div style={orderActionsStyle}>
                <button style={viewButtonStyle}>View Details</button>
                <button style={trackButtonStyle}>Track Order</button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

const orderStyle = {
  border: '1px solid #ddd',
  borderRadius: '8px',
  padding: '20px',
  marginBottom: '20px',
  backgroundColor: '#f9f9f9',
}

const orderHeaderStyle = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  marginBottom: '15px',
  borderBottom: '1px solid #eee',
  paddingBottom: '10px',
}

const statusStyle = {
  padding: '5px 10px',
  backgroundColor: '#28a745',
  color: 'white',
  borderRadius: '15px',
  fontSize: '12px',
}

const orderDetailsStyle = {
  marginBottom: '15px',
}

const orderItemsStyle = {
  marginBottom: '15px',
}

const itemRowStyle = {
  display: 'flex',
  justifyContent: 'space-between',
  padding: '5px 0',
  borderBottom: '1px solid #eee',
}

const orderActionsStyle = {
  display: 'flex',
  gap: '10px',
}

const viewButtonStyle = {
  padding: '8px 16px',
  backgroundColor: '#007bff',
  color: 'white',
  border: 'none',
  borderRadius: '5px',
  cursor: 'pointer',
}

const trackButtonStyle = {
  padding: '8px 16px',
  backgroundColor: '#6c757d',
  color: 'white',
  border: 'none',
  borderRadius: '5px',
  cursor: 'pointer',
}

const buttonStyle = {
  padding: '10px 20px',
  backgroundColor: '#28a745',
  color: 'white',
  textDecoration: 'none',
  borderRadius: '5px',
  display: 'inline-block',
}

export default Orders
