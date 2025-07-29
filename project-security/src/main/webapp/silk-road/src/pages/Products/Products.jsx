import React, { useEffect } from 'react'
import { Link } from 'react-router-dom'
import { ProductProvider } from '../../contexts/ProductContext'
import ProductFilters from '../../components/products/ProductFilters'
import ProductSort from '../../components/products/ProductSort'
import ProductGrid from '../../components/products/ProductGrid'
import SearchBar from '../../components/products/SearchBar'
import Pagination from '../../components/ui/Pagination'
import Loading from '../../components/ui/Loading'
import { useProducts } from '../../hooks/useProducts'
import './Products.css'

const ProductsContent = () => {
  const {
    products,
    totalProducts,
    loading,
    error,
    currentPage,
    productsPerPage,
    fetchProducts,
    setPage,
  } = useProducts()

  useEffect(() => {
    fetchProducts()
  }, [fetchProducts])

  const totalPages = Math.ceil(totalProducts / productsPerPage)

  if (loading && products.length === 0) {
    return (
      <div className='loading-container'>
        <Loading text='Loading products...' />
      </div>
    )
  }

  if (error) {
    return (
      <div className='error-container'>
        <div className='error-card'>
          <div className='error-icon'>⚠️</div>
          <h2>Oops! Something went wrong</h2>
          <p>{error}</p>
          <button className='retry-button' onClick={() => fetchProducts()}>
            Try Again
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className='products-container'>
      {/* Hero Header */}
      <div className='products-hero'>
        <div className='hero-content'>
          <div className='hero-text'>
            <h1 className='hero-title'>Discover Amazing Products</h1>
            <p className='hero-subtitle'>
              Find exactly what you're looking for in our curated collection
            </p>
          </div>
          <div className='hero-actions'>
            <div className='search-container'>
              <SearchBar />
            </div>
            <Link to='/cart' className='cart-link'>
              <div className='cart-button'>
                <span className='cart-icon'>🛒</span>
                <span className='cart-text'>Cart</span>
              </div>
            </Link>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className='products-layout'>
        {/* Sidebar Filters */}
        <aside className='filters-sidebar'>
          <div className='sidebar-header'>
            <h3>Filters</h3>
            <button className='clear-filters'>Clear all</button>
          </div>
          <div className='filters-content'>
            <ProductFilters />
          </div>
        </aside>

        {/* Products Area */}
        <main className='products-main-area'>
          {/* Toolbar */}
          <div className='products-toolbar'>
            <div className='toolbar-left'>
              <div className='products-count'>
                {totalProducts > 0 ? (
                  <span className='count-text'>
                    <strong>{totalProducts}</strong> products found
                  </span>
                ) : (
                  <span className='no-results'>No products found</span>
                )}
              </div>
            </div>

            <div className='toolbar-right'>
              <ProductSort />
            </div>
          </div>

          {/* Products Grid */}
          <div className='products-section'>
            {loading ? (
              <div className='inline-loading'>
                <Loading text='Loading more products...' />
              </div>
            ) : (
              <>
                <ProductGrid products={products} />

                {/* Pagination */}
                {totalPages > 1 && (
                  <div className='pagination-container'>
                    <Pagination
                      currentPage={currentPage}
                      totalPages={totalPages}
                      onPageChange={async (page) => {
                        await setPage(page)
                      }}
                    />
                  </div>
                )}
              </>
            )}
          </div>
        </main>
      </div>
    </div>
  )
}

const Products = () => {
  return (
    <ProductProvider>
      <ProductsContent />
    </ProductProvider>
  )
}

export default Products
