import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { productService } from '../../services/productService'
import { useCart } from '../../hooks/useCart'
import Button from '../ui/Button'
import Loading from '../ui/Loading'
import ProductImageGallery from './ProductImageGallery'
import ProductInfo from './ProductInfo'
import ProductReviews from './ProductReviews'
import RelatedProducts from './RelatedProducts'
import './ProductDetail.css'

const ProductDetail = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const { addToCart } = useCart()

  const [product, setProduct] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [selectedSize, setSelectedSize] = useState('')
  const [quantity, setQuantity] = useState(1)
  const [addingToCart, setAddingToCart] = useState(false)

  useEffect(() => {
    const fetchProduct = async () => {
      setLoading(true)
      try {
        const productData = await productService.getProductById(id)
        setProduct(productData)

        // Set default size if available
        if (productData.sizes && productData.sizes.length > 0) {
          setSelectedSize(productData.sizes[0])
        }

        setError(null)
      } catch (err) {
        console.error('Failed to fetch product:', err)
        setError('Product not found or failed to load.')
      } finally {
        setLoading(false)
      }
    }

    fetchProduct()
  }, [id])

  const handleAddToCart = async () => {
    if (product.sizes && product.sizes.length > 0 && !selectedSize) {
      alert('Please select a size')
      return
    }

    setAddingToCart(true)
    try {
      await addToCart(product, quantity, selectedSize)
      alert('Product added to cart!')
    } catch (error) {
      console.error('Failed to add to cart:', error)
      alert('Failed to add product to cart')
    } finally {
      setAddingToCart(false)
    }
  }

  const handleBuyNow = () => {
    handleAddToCart()
    navigate('/cart')
  }

  if (loading) {
    return <Loading text='Loading product details...' />
  }

  if (error || !product) {
    return (
      <div className='product-detail-error'>
        <h2>Product Not Found</h2>
        <p>{error || 'The product you are looking for does not exist.'}</p>
        <Button onClick={() => navigate('/products')}>Back to Products</Button>
      </div>
    )
  }

  return (
    <div className='product-detail'>
      <div className='product-detail-breadcrumb'>
        <Button variant='outline' size='small' onClick={() => navigate('/products')}>
          ← Back to Products
        </Button>
      </div>

      <div className='product-detail-content'>
        <div className='product-detail-images'>
          <ProductImageGallery images={product.images || [product.image]} />
        </div>

        <div className='product-detail-info'>
          <ProductInfo product={product} />

          <div className='product-options'>
            {product.sizes && product.sizes.length > 0 && (
              <div className='size-selector'>
                <label>Size:</label>
                <div className='size-options'>
                  {product.sizes.map((size) => (
                    <button
                      key={size}
                      className={`size-option ${selectedSize === size ? 'selected' : ''}`}
                      onClick={() => setSelectedSize(size)}>
                      {size}
                    </button>
                  ))}
                </div>
              </div>
            )}

            <div className='quantity-selector'>
              <label>Quantity:</label>
              <div className='quantity-controls'>
                <button
                  onClick={() => setQuantity(Math.max(1, quantity - 1))}
                  disabled={quantity <= 1}>
                  -
                </button>
                <span className='quantity-display'>{quantity}</span>
                <button
                  onClick={() => setQuantity(quantity + 1)}
                  disabled={quantity >= (product.stock || 10)}>
                  +
                </button>
              </div>
            </div>
          </div>

          <div className='product-actions'>
            <Button
              onClick={handleAddToCart}
              disabled={addingToCart || !product.inStock}
              size='large'>
              {addingToCart ? 'Adding...' : 'Add to Cart'}
            </Button>

            <Button
              onClick={handleBuyNow}
              variant='outline'
              disabled={addingToCart || !product.inStock}
              size='large'>
              Buy Now
            </Button>
          </div>

          {!product.inStock && (
            <div className='out-of-stock-notice'>
              <p>This product is currently out of stock</p>
            </div>
          )}
        </div>
      </div>

      <div className='product-detail-tabs'>
        <ProductReviews productId={product.id} />
      </div>

      <RelatedProducts category={product.category} currentProductId={product.id} />
    </div>
  )
}

export default ProductDetail
