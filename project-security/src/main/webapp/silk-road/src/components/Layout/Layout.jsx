import React from 'react'
import { Link, useLocation } from 'react-router-dom'
import { useCart } from '../../hooks/useCart'
import { useUser } from '../../hooks/useUser'

const Layout = ({ children }) => {
  const location = useLocation()
  const { getItemCount } = useCart()
  const { user, isAuthenticated } = useUser()

  return (
    <div style={layoutStyle}>
      <header style={headerStyle}>
        <div style={headerContentStyle}>
          <Link to='/' style={logoStyle}>
            🛣️ Silk Road
          </Link>

          <nav style={navStyle}>
            <Link to='/' style={location.pathname === '/' ? activeNavLinkStyle : navLinkStyle}>
              Home
            </Link>
            <Link
              to='/products' // Nuovo link per i prodotti
              style={location.pathname === '/products' ? activeNavLinkStyle : navLinkStyle}>
              Products
            </Link>
            <Link
              to='/cart'
              style={location.pathname === '/cart' ? activeNavLinkStyle : navLinkStyle}>
              Cart ({getItemCount()})
            </Link>
            <Link
              to='/orders'
              style={location.pathname === '/orders' ? activeNavLinkStyle : navLinkStyle}>
              Orders
            </Link>
            <Link
              to='/user'
              style={location.pathname === '/user' ? activeNavLinkStyle : navLinkStyle}>
              {isAuthenticated ? user?.firstName || 'Profile' : 'Login'}
            </Link>
          </nav>
        </div>
      </header>

      <main style={mainStyle}>{children}</main>

      <footer style={footerStyle}>
        <div style={footerContentStyle}>
          <p>&copy; 2024 Silk Road. All rights reserved.</p>
          <div style={footerLinksStyle}>
            <a href='#' style={footerLinkStyle}>
              Privacy Policy
            </a>
            <a href='#' style={footerLinkStyle}>
              Terms of Service
            </a>
            <a href='#' style={footerLinkStyle}>
              Contact Us
            </a>
          </div>
        </div>
      </footer>
    </div>
  )
}

const layoutStyle = {
  minHeight: '100vh',
  display: 'flex',
  flexDirection: 'column',
}

const headerStyle = {
  backgroundColor: '#2c3e50',
  color: 'white',
  padding: '1rem 0',
  boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
}

const headerContentStyle = {
  maxWidth: '1200px',
  margin: '0 auto',
  padding: '0 20px',
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
}

const logoStyle = {
  fontSize: '24px',
  fontWeight: 'bold',
  color: 'white',
  textDecoration: 'none',
}

const navStyle = {
  display: 'flex',
  gap: '30px',
}

const navLinkStyle = {
  color: 'white',
  textDecoration: 'none',
  padding: '8px 16px',
  borderRadius: '4px',
  transition: 'background-color 0.3s',
}

const activeNavLinkStyle = {
  ...navLinkStyle,
  backgroundColor: '#34495e',
}

const mainStyle = {
  flex: 1,
  maxWidth: '1200px',
  margin: '0 auto',
  width: '100%',
}

const footerStyle = {
  backgroundColor: '#34495e',
  color: 'white',
  padding: '20px 0',
  marginTop: '40px',
}

const footerContentStyle = {
  maxWidth: '1200px',
  margin: '0 auto',
  padding: '0 20px',
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
}

const footerLinksStyle = {
  display: 'flex',
  gap: '20px',
}

const footerLinkStyle = {
  color: 'white',
  textDecoration: 'none',
  fontSize: '14px',
}

export default Layout
