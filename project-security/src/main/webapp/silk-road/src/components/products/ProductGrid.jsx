import React from 'react'
import ProductCard from './ProductCard'
import './ProductGrid.css'

const ProductGrid = ({ products }) => {
  if (!products || products.length === 0) {
    return (
      <div className='product-grid-empty'>
        <div className='empty-state'>
          <h3>No products found</h3>
          <p>Try adjusting your filters or search terms</p>
        </div>
      </div>
    )
  }

  return (
    <div className='product-grid'>
      {products.map((product) => (
        <ProductCard key={product.id} product={product} />
      ))}
    </div>
  )
}

export default ProductGrid
