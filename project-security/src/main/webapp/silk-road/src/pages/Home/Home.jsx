import { Link } from 'react-router-dom'
import ProductCard from '../../components/products/ProductCard' // Ensure the path is correct
import './Home.css' // Import the CSS file

const Home = () => {
  const featuredProducts = [
    // Sample data for featured products
    { id: 1, name: 'Product 1', image: '/path/to/image1.jpg', price: 29.99 },
    { id: 2, name: 'Product 2', image: '/path/to/image2.jpg', price: 39.99 },
    { id: 3, name: 'Product 3', image: '/path/to/image3.jpg', price: 49.99 },
    // Add more products as needed
  ]

  return (
    <div className='home-container'>
      <h1 className='home-title'>Welcome to Silk Road</h1>
      <p className='home-description'>Your marketplace for authentic goods</p>

      <div>
        <h2 className='featured-products-title'>Featured Products</h2>
        <div className='product-grid'>
          {featuredProducts.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>

        <div className='view-all-products'>
          <Link to='/products' className='view-all-products-link'>
            View All Products
          </Link>
        </div>
      </div>
    </div>
  )
}

export default Home
