import { Route, BrowserRouter as Router, Routes } from 'react-router-dom'
import OrderDetail from './components/orders/OrderDetail'
import Orders from './pages/Orders/Orders'
import Layout from './components/Layout/Layout'
import ProductDetail from './components/products/ProductDetail'
import Products from './pages/Products/Products'
import { CartProvider } from './contexts/CartContext'
import { OrderProvider } from './contexts/OrderContext'
import { ProductProvider } from './contexts/ProductContext'
import { UserProvider } from './contexts/UserContext'
import Cart from './pages/Cart/Cart'
import Checkout from './pages/Checkout/Checkout'
import Home from './pages/Home/Home'
import User from './pages/User/User'

const App = () => {
  return (
    <Router>
      <UserProvider>
        <CartProvider>
          <OrderProvider>
            <ProductProvider>
              <Layout>
                <Routes>
                  <Route path='/' element={<Home />} />
                  <Route path='/products' element={<Products />} />
                  <Route path='/products/:id' element={<ProductDetail />} />
                  <Route path='/cart' element={<Cart />} />
                  <Route path='/checkout' element={<Checkout />} />
                  <Route path='/orders' element={<Orders />} />
                  <Route path='/orders/:id' element={<OrderDetail />} />
                  <Route path='/user' element={<User />} />
                </Routes>
              </Layout>
            </ProductProvider>
          </OrderProvider>
        </CartProvider>
      </UserProvider>
    </Router>
  )
}

export default App
