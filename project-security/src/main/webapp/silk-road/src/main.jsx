import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'

// Basic global styles
const globalStyles = `
  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }
  
  body {
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen',
      'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue',
      sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    background-color: #f8f9fa;
    color: #333;
    line-height: 1.6;
  }
  
  a {
    color: #007bff;
  }
  
  a:hover {
    color: #0056b3;
  }
  
  button:hover {
    opacity: 0.9;
  }
  
  input:focus, textarea:focus {
    outline: none;
    border-color: #007bff;
    box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
  }
`

// Inject global styles
const styleSheet = document.createElement('style')
styleSheet.textContent = globalStyles
document.head.appendChild(styleSheet)

const root = ReactDOM.createRoot(document.getElementById('root'))
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
)
