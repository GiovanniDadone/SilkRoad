import React from 'react'
import { formatDate } from '../../utils/dateUtils'
import './OrderStatus.css'

const OrderStatus = ({ status, updatedAt }) => {
  const statusSteps = [
    { key: 'pending', label: 'Order Placed', icon: '📝' },
    { key: 'processing', label: 'Processing', icon: '⚙️' },
    { key: 'shipped', label: 'Shipped', icon: '🚚' },
    { key: 'delivered', label: 'Delivered', icon: '📦' },
  ]

  const currentStepIndex = statusSteps.findIndex((step) => step.key === status)
  const isCancelled = status === 'cancelled'

  const getStatusClass = (stepIndex) => {
    if (isCancelled) return 'cancelled'
    if (stepIndex === currentStepIndex) return 'current'
    if (stepIndex < currentStepIndex) return 'completed'
    return 'pending'
  }

  return (
    <div className={`order-status ${isCancelled ? 'order-cancelled' : ''}`}>
      {isCancelled ? (
        <div className='cancelled-status'>
          <span className='status-icon'>❌</span>
          <div className='status-info'>
            <h4>Order Cancelled</h4>
            {updatedAt && (
              <p className='status-date'>
                {formatDate(updatedAt, { hour: undefined, minute: undefined })}
              </p>
            )}
          </div>
        </div>
      ) : (
        <div className='status-steps'>
          {statusSteps.map((step, index) => (
            <div key={step.key} className={`status-step ${getStatusClass(index)}`}>
              <div className='step-icon'>{step.icon}</div>
              <div className='step-label'>{step.label}</div>
              <div className='step-connector'></div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default OrderStatus
