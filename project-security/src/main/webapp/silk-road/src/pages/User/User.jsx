import React, { useState } from 'react'
import { useUser } from '../../contexts/UserContext'

const User = () => {
  const { user, updateUser, logout } = useUser()
  const [isEditing, setIsEditing] = useState(false)
  const [formData, setFormData] = useState({
    firstName: user?.firstName || '',
    lastName: user?.lastName || '',
    email: user?.email || '',
    phone: user?.phone || '',
    address: user?.address || '',
  })

  const handleInputChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    })
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    updateUser(formData)
    setIsEditing(false)
    alert('Profile updated successfully!')
  }

  const handleCancel = () => {
    setFormData({
      firstName: user?.firstName || '',
      lastName: user?.lastName || '',
      email: user?.email || '',
      phone: user?.phone || '',
      address: user?.address || '',
    })
    setIsEditing(false)
  }

  return (
    <div style={{ padding: '20px' }}>
      <div style={headerStyle}>
        <h1>My Profile</h1>
        <button onClick={logout} style={logoutButtonStyle}>
          Logout
        </button>
      </div>

      <div style={profileContainerStyle}>
        <div style={profileSectionStyle}>
          <h2>Personal Information</h2>

          {!isEditing ? (
            <div>
              <div style={infoRowStyle}>
                <strong>First Name:</strong> {user?.firstName || 'Not provided'}
              </div>
              <div style={infoRowStyle}>
                <strong>Last Name:</strong> {user?.lastName || 'Not provided'}
              </div>
              <div style={infoRowStyle}>
                <strong>Email:</strong> {user?.email || 'Not provided'}
              </div>
              <div style={infoRowStyle}>
                <strong>Phone:</strong> {user?.phone || 'Not provided'}
              </div>
              <div style={infoRowStyle}>
                <strong>Address:</strong> {user?.address || 'Not provided'}
              </div>

              <button onClick={() => setIsEditing(true)} style={editButtonStyle}>
                Edit Profile
              </button>
            </div>
          ) : (
            <form onSubmit={handleSubmit}>
              <div style={formGroupStyle}>
                <label>First Name:</label>
                <input
                  type='text'
                  name='firstName'
                  value={formData.firstName}
                  onChange={handleInputChange}
                  style={inputStyle}
                />
              </div>

              <div style={formGroupStyle}>
                <label>Last Name:</label>
                <input
                  type='text'
                  name='lastName'
                  value={formData.lastName}
                  onChange={handleInputChange}
                  style={inputStyle}
                />
              </div>

              <div style={formGroupStyle}>
                <label>Email:</label>
                <input
                  type='email'
                  name='email'
                  value={formData.email}
                  onChange={handleInputChange}
                  style={inputStyle}
                />
              </div>

              <div style={formGroupStyle}>
                <label>Phone:</label>
                <input
                  type='tel'
                  name='phone'
                  value={formData.phone}
                  onChange={handleInputChange}
                  style={inputStyle}
                />
              </div>

              <div style={formGroupStyle}>
                <label>Address:</label>
                <textarea
                  name='address'
                  value={formData.address}
                  onChange={handleInputChange}
                  style={{ ...inputStyle, height: '80px' }}
                />
              </div>

              <div style={buttonGroupStyle}>
                <button type='submit' style={saveButtonStyle}>
                  Save Changes
                </button>
                <button type='button' onClick={handleCancel} style={cancelButtonStyle}>
                  Cancel
                </button>
              </div>
            </form>
          )}
        </div>

        <div style={profileSectionStyle}>
          <h2>Account Statistics</h2>
          <div style={statsStyle}>
            <div style={statItemStyle}>
              <h3>0</h3>
              <p>Total Orders</p>
            </div>
            <div style={statItemStyle}>
              <h3>$0.00</h3>
              <p>Total Spent</p>
            </div>
            <div style={statItemStyle}>
              <h3>Member</h3>
              <p>Account Status</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

const headerStyle = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  marginBottom: '30px',
}

const logoutButtonStyle = {
  padding: '8px 16px',
  backgroundColor: '#dc3545',
  color: 'white',
  border: 'none',
  borderRadius: '5px',
  cursor: 'pointer',
}

const profileContainerStyle = {
  display: 'grid',
  gridTemplateColumns: '2fr 1fr',
  gap: '30px',
}

const profileSectionStyle = {
  backgroundColor: '#f9f9f9',
  padding: '20px',
  borderRadius: '8px',
  border: '1px solid #ddd',
}

const infoRowStyle = {
  padding: '10px 0',
  borderBottom: '1px solid #eee',
}

const formGroupStyle = {
  marginBottom: '15px',
}

const inputStyle = {
  width: '100%',
  padding: '8px',
  border: '1px solid #ddd',
  borderRadius: '4px',
  marginTop: '5px',
}

const buttonGroupStyle = {
  display: 'flex',
  gap: '10px',
  marginTop: '20px',
}

const editButtonStyle = {
  padding: '10px 20px',
  backgroundColor: '#007bff',
  color: 'white',
  border: 'none',
  borderRadius: '5px',
  cursor: 'pointer',
  marginTop: '20px',
}

const saveButtonStyle = {
  padding: '10px 20px',
  backgroundColor: '#28a745',
  color: 'white',
  border: 'none',
  borderRadius: '5px',
  cursor: 'pointer',
}

const cancelButtonStyle = {
  padding: '10px 20px',
  backgroundColor: '#6c757d',
  color: 'white',
  border: 'none',
  borderRadius: '5px',
  cursor: 'pointer',
}

const statsStyle = {
  display: 'grid',
  gridTemplateColumns: '1fr',
  gap: '15px',
}

const statItemStyle = {
  textAlign: 'center',
  padding: '15px',
  backgroundColor: 'white',
  borderRadius: '5px',
  border: '1px solid #eee',
}

export default User
