import React from 'react'
import { useNavigate } from 'react-router-dom'
import Button from '../ui/Button'
import { formatDate } from '../../utils/dateUtils'
import './OrderCard.css'

const OrderCard = ({ order }) => {
  const navigate = useNavigate()

  const getStatusColor = (status) => {
    const colors = {
      pending: '#f59e0b',
      processing: '#3b82f6',
      shipped: '#8b5cf6',
      delivered: '#10b981',
      cancelled: '#ef4444',
    }
    return colors[status] || '#6b7280'
  }

  return (
    <div className='order-card'>
      <div className='order-header'>
        <div className='order-info'>
          <h3>Order #{order.id}</h3>
          <p className='order-date'>Placed on {formatDate(order.createdAt)}</p>
        </div>
        <div className='order-status' style={{ color: getStatusColor(order.status) }}>
          {order.status.charAt(0).toUpperCase() + order.status.slice(1)}
        </div>
      </div>

      <div className='order-items'>
        {order.items.slice(0, 3).map((item, index) => (
          <div key={index} className='order-item'>
            <img src={item.image} alt={item.name} />
            <div className='item-info'>
              <p className='item-name'>{item.name}</p>
              <p className='item-details'>Qty: {item.quantity}</p>
            </div>
          </div>
        ))}
        {order.items.length > 3 && (
          <p className='more-items'>+{order.items.length - 3} more items</p>
        )}
      </div>

      <div className='order-footer'>
        <div className='order-total'>
          <strong>Total: ${order.total.toFixed(2)}</strong>
        </div>
        <Button variant='outline' onClick={() => navigate(`/orders/${order.id}`)}>
          View Details
        </Button>
      </div>
    </div>
  )
}

export default OrderCard
