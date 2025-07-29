import React, { useState, useEffect } from 'react'
import { productService } from '../../services/productService'
import ProductCard from './ProductCard'
import Loading from '../ui/Loading'
import './RelatedProducts.css'

const RelatedProducts = ({ category, currentProductId }) => {
  const [relatedProducts, setRelatedProducts] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchRelatedProducts = async () => {
      if (!category) {
        setLoading(false)
        return
      }

      try {
        const response = await productService.getProducts({
          category,
          limit: 4,
        })

        const products = response.products || response
        // Filter out current product
        const filtered = products.filter((product) => product.id !== currentProductId)
        setRelatedProducts(filtered.slice(0, 4))
      } catch (error) {
        console.error('Failed to fetch related products:', error)
        setRelatedProducts([])
      } finally {
        setLoading(false)
      }
    }

    fetchRelatedProducts()
  }, [category, currentProductId])

  if (loading) {
    return <Loading text='Loading related products...' />
  }

  if (relatedProducts.length === 0) {
    return null
  }

  return (
    <div className='related-products'>
      <h3>Related Products</h3>
      <div className='related-products-grid'>
        {relatedProducts.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </div>
  )
}

export default RelatedProducts
