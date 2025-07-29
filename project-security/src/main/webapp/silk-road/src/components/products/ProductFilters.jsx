import React from 'react'
import { useProducts } from '../../hooks/useProducts'
import Input from '../ui/Input'
import Button from '../ui/Button'
import './ProductFilters.css'

const ProductFilters = () => {
  const { filters, setFilters, clearFilters } = useProducts()

  const categories = [
    'Electronics',
    'Clothing',
    'Books',
    'Home & Garden',
    'Sports',
    'Beauty',
    'Toys',
    'Automotive',
  ]

  const handleFilterChange = (filterName, value) => {
    setFilters({ [filterName]: value })
  }

  const handlePriceChange = (field, value) => {
    const numericValue = value === '' ? '' : parseFloat(value)
    setFilters({ [field]: numericValue })
  }

  return (
    <div className='product-filters'>
      <div className='filters-header'>
        <h3>Filters</h3>
        <Button variant='outline' size='small' onClick={clearFilters}>
          Clear All
        </Button>
      </div>

      <div className='filter-group'>
        <h4>Category</h4>
        <select
          value={filters.category}
          onChange={(e) => handleFilterChange('category', e.target.value)}
          className='category-select'>
          <option value=''>All Categories</option>
          {categories.map((category) => (
            <option key={category} value={category}>
              {category}
            </option>
          ))}
        </select>
      </div>

      <div className='filter-group'>
        <h4>Price Range</h4>
        <div className='price-inputs'>
          <Input
            type='number'
            placeholder='Min'
            value={filters.minPrice}
            onChange={(e) => handlePriceChange('minPrice', e.target.value)}
            min='0'
            step='0.01'
          />
          <span className='price-separator'>to</span>
          <Input
            type='number'
            placeholder='Max'
            value={filters.maxPrice}
            onChange={(e) => handlePriceChange('maxPrice', e.target.value)}
            min='0'
            step='0.01'
          />
        </div>
      </div>

      <div className='filter-group'>
        <label className='checkbox-label'>
          <input
            type='checkbox'
            checked={filters.inStock}
            onChange={(e) => handleFilterChange('inStock', e.target.checked)}
          />
          <span>In Stock Only</span>
        </label>
      </div>
    </div>
  )
}

export default ProductFilters
