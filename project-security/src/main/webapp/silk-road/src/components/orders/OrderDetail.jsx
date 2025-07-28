import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useOrder } from '../../contexts/OrderContext'
import { orderService } from '../../services/api'
import OrderInfo from './OrderInfo'
import OrderItems from './OrderItems'
import OrderStatus from './OrderStatus'
import Button from '../ui/Button'
import Loading from '../ui/Loading'
import './OrderDetail.css'

const OrderDetail = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const { getOrderById } = useOrder()
  const [order, setOrder] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    const fetchOrderDetails = async () => {
      setLoading(true)
      try {
        // Prima controlla se l'ordine è già nel contesto
        const cachedOrder = getOrderById(id)

        if (cachedOrder) {
          setOrder(cachedOrder)
        } else {
          // Se non è nel contesto, fai una chiamata API
          const fetchedOrder = await orderService.getOrderById(id)
          setOrder(fetchedOrder)
        }
        setError(null)
      } catch (err) {
        console.error('Failed to fetch order details:', err)
        setError('Unable to load order details. Please try again later.')
      } finally {
        setLoading(false)
      }
    }

    fetchOrderDetails()
  }, [id, getOrderById])

  const handleTrackOrder = () => {
    // In un'implementazione reale, questo potrebbe aprire una pagina di tracking
    alert(`Tracking information for order #${id} will be displayed here.`)
  }

  const handleCancelOrder = async () => {
    if (window.confirm('Are you sure you want to cancel this order?')) {
      try {
        await orderService.updateOrderStatus(id, 'cancelled')
        // Aggiorna l'ordine localmente
        setOrder((prev) => ({
          ...prev,
          status: 'cancelled',
          updatedAt: new Date().toISOString(),
        }))
      } catch (err) {
        console.error('Failed to cancel order:', err)
        alert('Failed to cancel order. Please try again.')
      }
    }
  }

  const handlePrintReceipt = () => {
    window.print()
  }

  if (loading) {
    return <Loading text='Loading order details...' />
  }

  if (error) {
    return (
      <div className='order-detail-error'>
        <h2>Error</h2>
        <p>{error}</p>
        <Button onClick={() => navigate('/orders')}>Back to Orders</Button>
      </div>
    )
  }

  if (!order) {
    return (
      <div className='order-detail-not-found'>
        <h2>Order Not Found</h2>
        <p>The order you're looking for doesn't exist or has been removed.</p>
        <Button onClick={() => navigate('/orders')}>Back to Orders</Button>
      </div>
    )
  }

  const canCancel = ['pending', 'processing'].includes(order.status)

  return (
    <div className='order-detail'>
      <div className='order-detail-header'>
        <div className='order-detail-title'>
          <Button variant='outline' size='small' onClick={() => navigate('/orders')}>
            ← Back to Orders
          </Button>
          <h1>Order #{order.id}</h1>
          <p className='order-date'>Placed on {new Date(order.createdAt).toLocaleDateString()}</p>
        </div>
        <OrderStatus status={order.status} updatedAt={order.updatedAt} />
      </div>

      <div className='order-detail-content'>
        <div className='order-detail-main'>
          <OrderItems items={order.items} />

          <div className='order-detail-actions'>
            <Button onClick={handleTrackOrder}>Track Order</Button>
            <Button onClick={handlePrintReceipt} variant='outline'>
              Print Receipt
            </Button>
            {canCancel && (
              <Button
                onClick={handleCancelOrder}
                variant='outline'
                style={{
                  color: '#dc3545',
                  borderColor: '#dc3545',
                }}>
                Cancel Order
              </Button>
            )}
          </div>
        </div>

        <div className='order-detail-sidebar'>
          <OrderInfo order={order} />
        </div>
      </div>
    </div>
  )
}

export default OrderDetail
