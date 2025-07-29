import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import ProductCard from '../../components/products/ProductCard' // Assicurati che il percorso sia corretto
import { useProducts } from '../../hooks/useProducts' // Importa il context dei prodotti

const Home = () => {
  const [featuredProducts, setFeaturedProducts] = useState([])
  const { products, loading, error } = useProducts() // Usa il context dei prodotti

  // Filtra i prodotti in evidenza (puoi modificare la logica secondo le tue esigenze)
  useEffect(() => {
    if (products && products.length > 0) {
      const featured = products.slice(0, 6) // Mostra i primi 6 prodotti
      setFeaturedProducts(featured)
    }
  }, [products])

  if (loading)
    return <div style={{ padding: '20px', textAlign: 'center' }}>Loading products...</div>
  if (error)
    return (
      <div style={{ padding: '20px', textAlign: 'center', color: 'red' }}>
        Error loading products
      </div>
    )

  return (
    <div style={{ padding: '20px' }}>
      <h1 style={{ textAlign: 'center', marginBottom: '30px' }}>Welcome to Silk Road</h1>
      <p style={{ textAlign: 'center', fontSize: '1.2rem', marginBottom: '40px' }}>
        Your marketplace for authentic goods
      </p>

      <div>
        <h2 style={{ textAlign: 'center', marginBottom: '30px' }}>Featured Products</h2>
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))',
            gap: '20px',
            padding: '0 20px',
          }}>
          {featuredProducts.map((product) => (
            <ProductCard key={product.id} product={product} style={{ height: '100%' }} />
          ))}
        </div>

        <div style={{ textAlign: 'center', marginTop: '40px' }}>
          <Link
            to='/products'
            style={{
              padding: '12px 24px',
              backgroundColor: '#2c3e50',
              color: 'white',
              textDecoration: 'none',
              borderRadius: '5px',
              fontSize: '1.1rem',
              display: 'inline-block',
            }}>
            View All Products
          </Link>
        </div>
      </div>
    </div>
  )
}

export default Home
