import React from 'react'
import { useProducts } from '../../hooks/useProducts'
import './ProductSort.css'

const ProductSort = () => {
  const { sortBy, sortOrder, setSorting } = useProducts()

  const sortOptions = [
    { value: 'name-asc', label: 'Name (A-Z)', sortBy: 'name', sortOrder: 'asc' },
    { value: 'name-desc', label: 'Name (Z-A)', sortBy: 'name', sortOrder: 'desc' },
    { value: 'price-asc', label: 'Price (Low to High)', sortBy: 'price', sortOrder: 'asc' },
    { value: 'price-desc', label: 'Price (High to Low)', sortBy: 'price', sortOrder: 'desc' },
    { value: 'rating-desc', label: 'Highest Rated', sortBy: 'rating', sortOrder: 'desc' },
    { value: 'newest', label: 'Newest First', sortBy: 'createdAt', sortOrder: 'desc' },
  ]

  const currentSortValue = `${sortBy}-${sortOrder}`

  const handleSortChange = (e) => {
    const selectedOption = sortOptions.find((option) => option.value === e.target.value)
    if (selectedOption) {
      setSorting(selectedOption.sortBy, selectedOption.sortOrder)
    }
  }

  return (
    <div className='product-sort'>
      <label htmlFor='sort-select'>Sort by:</label>
      <select
        id='sort-select'
        value={currentSortValue}
        onChange={handleSortChange}
        className='sort-select'>
        {sortOptions.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
    </div>
  )
}

export default ProductSort
