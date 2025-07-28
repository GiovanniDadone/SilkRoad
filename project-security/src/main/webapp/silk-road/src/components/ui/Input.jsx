import React from 'react'

const Input = ({
  label,
  type = 'text',
  name,
  value,
  onChange,
  error,
  required = false,
  disabled = false,
  placeholder,
  maxLength,
  ...props
}) => {
  return (
    <div style={containerStyle}>
      {label && (
        <label htmlFor={name} style={labelStyle}>
          {label} {required && <span style={{ color: '#dc3545' }}>*</span>}
        </label>
      )}
      <input
        id={name}
        type={type}
        name={name}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        maxLength={maxLength}
        required={required}
        disabled={disabled}
        style={{
          ...inputStyle,
          ...(error ? errorInputStyle : {}),
          ...(disabled ? disabledInputStyle : {}),
        }}
        {...props}
      />
      {error && <span style={errorStyle}>{error}</span>}
    </div>
  )
}

const containerStyle = {
  marginBottom: '15px',
}

const labelStyle = {
  display: 'block',
  marginBottom: '5px',
  fontWeight: '500',
  color: '#333',
}

const inputStyle = {
  width: '100%',
  padding: '10px 12px',
  border: '1px solid #ddd',
  borderRadius: '4px',
  fontSize: '14px',
  transition: 'border-color 0.3s, box-shadow 0.3s',
}

const errorInputStyle = {
  borderColor: '#dc3545',
}

const disabledInputStyle = {
  backgroundColor: '#f8f9fa',
  cursor: 'not-allowed',
}

const errorStyle = {
  display: 'block',
  marginTop: '5px',
  fontSize: '12px',
  color: '#dc3545',
}

export default Input
