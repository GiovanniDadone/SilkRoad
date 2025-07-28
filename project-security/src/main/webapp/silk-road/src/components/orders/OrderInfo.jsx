import React from 'react'
import { formatDate } from '../../utils/dateUtils'
import './OrderInfo.css'

const OrderInfo = ({ order }) => {
  if (!order) return null

  const { id, createdAt, total, shipping, payment, estimatedDelivery } = order

  // Format payment info for display
  const formatCardNumber = (cardNumber) => {
    if (!cardNumber) return 'N/A'
    // Show only last 4 digits
    return `•••• •••• •••• ${cardNumber.slice(-4)}`
  }

  return (
    <div className='order-info'>
      <h3>Order Information</h3>

      <div className='order-info-section'>
        <h4>Order Summary</h4>
        <div className='order-info-item'>
          <span>Order ID:</span>
          <span>#{id}</span>
        </div>
        <div className='order-info-item'>
          <span>Order Date:</span>
          <span>{formatDate(createdAt)}</span>
        </div>
        {estimatedDelivery && (
          <div className='order-info-item'>
            <span>Estimated Delivery:</span>
            <span>{formatDate(estimatedDelivery)}</span>
          </div>
        )}
        <div className='order-info-item total'>
          <span>Total:</span>
          <span>${parseFloat(total).toFixed(2)}</span>
        </div>
      </div>

      {shipping && (
        <div className='order-info-section'>
          <h4>Shipping Address</h4>
          <address className='order-address'>
            <p>
              {shipping.firstName} {shipping.lastName}
            </p>
            <p>{shipping.address}</p>
            <p>
              {shipping.city}, {shipping.state} {shipping.zipCode}
            </p>
            <p>{shipping.phone}</p>
          </address>
        </div>
      )}

      {payment && (
        <div className='order-info-section'>
          <h4>Payment Method</h4>
          <div className='payment-info'>
            <p className='card-number'>
              {payment.cardType || 'Credit Card'}: {formatCardNumber(payment.cardNumber)}
            </p>
            {payment.cardholderName && <p className='cardholder'>{payment.cardholderName}</p>}
          </div>
        </div>
      )}
    </div>
  )
}

export default OrderInfo
