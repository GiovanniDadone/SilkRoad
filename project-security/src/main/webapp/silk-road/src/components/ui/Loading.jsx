import React from 'react'

const Loading = ({ size = 'medium', text = 'Loading...' }) => {
  const sizes = {
    small: { width: '20px', height: '20px' },
    medium: { width: '40px', height: '40px' },
    large: { width: '60px', height: '60px' },
  }

  const spinnerStyle = {
    ...sizes[size],
    border: '3px solid #f3f3f3',
    borderTop: '3px solid #007bff',
    borderRadius: '50%',
    animation: 'spin 1s linear infinite',
    margin: '0 auto',
  }

  const containerStyle = {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '20px',
  }

  // Add keyframes for spin animation
  React.useEffect(() => {
    const style = document.createElement('style')
    style.textContent = `
      @keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
      }
    `
    document.head.appendChild(style)

    return () => {
      document.head.removeChild(style)
    }
  }, [])

  return (
    <div style={containerStyle}>
      <div style={spinnerStyle}></div>
      {text && <p style={{ marginTop: '10px', color: '#666' }}>{text}</p>}
    </div>
  )
}

export default Loading
