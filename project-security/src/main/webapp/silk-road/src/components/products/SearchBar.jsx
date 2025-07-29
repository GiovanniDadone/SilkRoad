import React, { useState, useEffect } from 'react'
import { useProducts } from '../../hooks/useProducts'
import './SearchBar.css'

const SearchBar = () => {
  const { searchQuery, searchProducts } = useProducts()
  const [localQuery, setLocalQuery] = useState(searchQuery)

  useEffect(() => {
    setLocalQuery(searchQuery)
  }, [searchQuery])

  const handleSubmit = (e) => {
    e.preventDefault()
    searchProducts(localQuery)
  }

  const handleClear = () => {
    setLocalQuery('')
    searchProducts('')
  }

  return (
    <form onSubmit={handleSubmit} className='search-bar'>
      <div className='search-input-container'>
        <input
          type='text'
          value={localQuery}
          onChange={(e) => setLocalQuery(e.target.value)}
          placeholder='Search products...'
          className='search-input'
        />
        {localQuery && (
          <button type='button' onClick={handleClear} className='search-clear'>
            ×
          </button>
        )}
      </div>
      <button type='submit' className='search-button'>
        🔍
      </button>
    </form>
  )
}

export default SearchBar
