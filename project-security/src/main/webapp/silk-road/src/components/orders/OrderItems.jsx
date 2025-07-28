import React from 'react'
import { Link } from 'react-router-dom'
import './OrderItems.css'

const OrderItems = ({ items }) => {
  if (!items || items.length === 0) {
    return (
      <div className='order-items-empty'>
        <p>No items in this order</p>
      </div>
    )
  }

  const calculateItemTotal = (item) => {
    return (item.price * item.quantity).toFixed(2)
  }

  return (
    <div className='order-items'>
      <h3>Order Items</h3>

      <div className='order-items-list'>
        {items.map((item, index) => (
          <div key={index} className='order-item'>
            <div className='order-item-image'>
              {item.image ? (
                <img src={item.image} alt={item.name} />
              ) : (
                <div className='placeholder-image'>No Image</div>
              )}
            </div>

            <div className='order-item-details'>
              <h4 className='item-name'>
                {item.productId ? (
                  <Link to={`/products/${item.productId}`}>{item.name}</Link>
                ) : (
                  item.name
                )}
              </h4>

              {item.size && <p className='item-size'>Size: {item.size}</p>}

              <div className='item-price-info'>
                <span className='item-price'>${parseFloat(item.price).toFixed(2)}</span>
                <span className='item-quantity'>Qty: {item.quantity}</span>
              </div>
            </div>

            <div className='order-item-total'>${calculateItemTotal(item)}</div>
          </div>
        ))}
      </div>
    </div>
  )
}

export default OrderItems
