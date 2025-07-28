import React from 'react'

const Button = ({
  children,
  variant = 'primary',
  size = 'medium',
  disabled = false,
  onClick,
  type = 'button',
  style = {},
  ...props
}) => {
  const baseStyle = {
    border: 'none',
    borderRadius: '4px',
    cursor: disabled ? 'not-allowed' : 'pointer',
    fontWeight: '500',
    textAlign: 'center',
    textDecoration: 'none',
    display: 'inline-block',
    transition: 'all 0.3s ease',
    opacity: disabled ? 0.6 : 1,
    ...style,
  }

  const variants = {
    primary: {
      backgroundColor: '#007bff',
      color: 'white',
      border: '1px solid #007bff',
    },
    outline: {
      backgroundColor: 'transparent',
      color: '#007bff',
      border: '1px solid #007bff',
    },
    secondary: {
      backgroundColor: '#6c757d',
      color: 'white',
      border: '1px solid #6c757d',
    },
  }

  const sizes = {
    small: {
      padding: '6px 12px',
      fontSize: '12px',
    },
    medium: {
      padding: '8px 16px',
      fontSize: '14px',
    },
    large: {
      padding: '12px 24px',
      fontSize: '16px',
    },
  }

  const buttonStyle = {
    ...baseStyle,
    ...variants[variant],
    ...sizes[size],
  }

  return (
    <button type={type} style={buttonStyle} onClick={onClick} disabled={disabled} {...props}>
      {children}
    </button>
  )
}

export default Button
