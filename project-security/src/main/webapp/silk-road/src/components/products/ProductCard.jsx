import React, { useState } from 'react'
import { Link } from 'react-router-dom'
import { useCart } from '../../contexts/CartContext'
import Button from '../ui/Button'
import './ProductCard.css'

const ProductCard = ({ product }) => {
  const { addToCart } = useCart()
  const [isAddingToCart, setIsAddingToCart] = useState(false)

  const handleAddToCart = async (e) => {
    e.preventDefault()
    e.stopPropagation()

    setIsAddingToCart(true)
    try {
      await addToCart(product, 1)
      // Optional: Show success message
    } catch (error) {
      console.error('Failed to add to cart:', error)
    } finally {
      setIsAddingToCart(false)
    }
  }

  const formatPrice = (price) => {
    return `$${parseFloat(price).toFixed(2)}`
  }

  return (
    <div className='product-card'>
      <Link to={`/products/${product.id}`} className='product-card-link'>
        <div className='product-card-image'>
          <img
            src={product.image || '/placeholder-image.jpg'}
            alt={product.name}
            onError={(e) => {
              e.target.src = '/placeholder-image.jpg'
            }}
          />
          {!product.inStock && (
            <div className='out-of-stock-overlay'>
              <span>Out of Stock</span>
            </div>
          )}
          {product.discount && <div className='discount-badge'>-{product.discount}%</div>}
        </div>

        <div className='product-card-content'>
          <h3 className='product-name'>{product.name}</h3>

          {product.category && <p className='product-category'>{product.category}</p>}

          <div className='product-price'>
            {product.originalPrice && product.originalPrice > product.price ? (
              <>
                <span className='price-current'>{formatPrice(product.price)}</span>
                <span className='price-original'>{formatPrice(product.originalPrice)}</span>
              </>
            ) : (
              <span className='price-current'>{formatPrice(product.price)}</span>
            )}
          </div>

          {product.rating && (
            <div className='product-rating'>
              <div className='stars'>
                {[...Array(5)].map((_, i) => (
                  <span
                    key={i}
                    className={`star ${i < Math.floor(product.rating) ? 'filled' : ''}`}>
                    ★
                  </span>
                ))}
              </div>
              <span className='rating-count'>({product.reviewCount || 0})</span>
            </div>
          )}
        </div>
      </Link>

      <div className='product-card-actions'>
        <Button
          onClick={handleAddToCart}
          disabled={isAddingToCart || !product.inStock}
          size='small'
          style={{ width: '100%' }}>
          {isAddingToCart ? 'Adding...' : 'Add to Cart'}
        </Button>
      </div>
    </div>
  )
}

export default ProductCard
