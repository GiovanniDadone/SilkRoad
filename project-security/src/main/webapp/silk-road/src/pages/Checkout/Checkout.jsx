import React, { useState } from 'react';
import { useCart } from '../../contexts/CartContext';
import { useOrder } from '../../contexts/OrderContext';
import { useNavigate } from 'react-router-dom';

const Checkout = () => {
    const { cartItems, getTotal, clearCart } = useCart();
    const { createOrder } = useOrder();
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        address: '',
        city: '',
        zipCode: '',
        cardNumber: '',
        expiryDate: '',
        cvv: ''
    });

    const handleInputChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const orderData = {
            items: cartItems,
            total: getTotal(),
            shippingInfo: formData,
            orderDate: new Date().toISOString()
        };

        try {
            await createOrder(orderData);
            clearCart();
            alert('Order placed successfully!');
            navigate('/orders');
        } catch (error) {
            alert('Error placing order. Please try again.');
        }
    };

    if (cartItems.length === 0) {
        return (
            <div style={{ padding: '20px', textAlign: 'center' }}>
                <h1>Checkout</h1>
                <p>Your cart is empty</p>
                <button onClick={() => navigate('/cart')} style={buttonStyle}>
                    Go to Cart
                </button>
            </div>
        );
    }

    return (
        <div style={{ padding: '20px' }}>
            <h1>Checkout</h1>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '40px' }}>
                <div>
                    <h2>Shipping Information</h2>
                    <form onSubmit={handleSubmit}>
                        <div style={formGroupStyle}>
                            <input
                                type="text"
                                name="firstName"
                                placeholder="First Name"
                                value={formData.firstName}
                                onChange={handleInputChange}
                                style={inputStyle}
                                required
                            />
                        </div>

                        <div style={formGroupStyle}>
                            <input
                                type="text"
                                name="lastName"
                                placeholder="Last Name"
                                value={formData.lastName}
                                onChange={handleInputChange}
                                style={inputStyle}
                                required
                            />
                        </div>

                        <div style={formGroupStyle}>
                            <input
                                type="email"
                                name="email"
                                placeholder="Email"
                                value={formData.email}
                                onChange={handleInputChange}
                                style={inputStyle}
                                required
                            />
                        </div>

                        <div style={formGroupStyle}>
                            <input
                                type="text"
                                name="address"
                                placeholder="Address"
                                value={formData.address}
                                onChange={handleInputChange}
                                style={inputStyle}
                                required
                            />
                        </div>

                        <div style={formGroupStyle}>
                            <input
                                type="text"
                                name="city"
                                placeholder="City"
                                value={formData.city}
                                onChange={handleInputChange}
                                style={inputStyle}
                                required
                            />
                        </div>

                        <div style={formGroupStyle}>
                            <input
                                type="text"
                                name="zipCode"
                                placeholder="ZIP Code"
                                value={formData.zipCode}
                                onChange={handleInputChange}
                                style={inputStyle}
                                required
                            />
                        </div>

                        <h3>Payment Information</h3>

                        <div style={formGroupStyle}>
                            <input
                                type="text"
                                name="cardNumber"
                                placeholder="Card Number"
                                value={formData.cardNumber}
                                onChange={handleInputChange}
                                style={inputStyle}
                                required
                            />
                        </div>

                        <div style={formGroupStyle}>
                            <input
                                type="text"
                                name="expiryDate"
                                placeholder="MM/YY"
                                value={formData.expiryDate}
                                onChange={handleInputChange}
                                style={inputStyle}
                                required
                            />
                        </div>

                        <div style={formGroupStyle}>
                            <input
                                type="text"
                                name="cvv"
                                placeholder="CVV"
                                value={formData.cvv}
                                onChange={handleInputChange}
                                style={inputStyle}
                                required
                            />
                        </div>

                        <button type="submit" style={buttonStyle}>
                            Place Order (${getTotal()})
                        </button>
                    </form>
                </div>

                <div>
                    <h2>Order Summary</h2>
                    {cartItems.map((item) => (
                        <div key={item.id} style={summaryItemStyle}>
                            <span>{item.name} x {item.quantity}</span>
                            <span>${(item.price * item.quantity).toFixed(2)}</span>
                        </div>
                    ))}
                    <div style={{ ...summaryItemStyle, borderTop: '1px solid #ccc', fontWeight: 'bold' }}>
                        <span>Total</span>
                        <span>${getTotal()}</span>
                    </div>
                </div>
            </div>
        </div>
    );
};

const formGroupStyle = {
    marginBottom: '15px'
};

const inputStyle = {
    width: '100%',
    padding: '10px',
    border: '1px solid #ddd',
    borderRadius: '5px',
    fontSize: '16px'
};

const buttonStyle = {
    padding: '12px 24px',
    backgroundColor: '#28a745',
    color: 'white',
    border: 'none',
    borderRadius: '5px',
    cursor: 'pointer',
    fontSize: '16px',
    width: '100%'
};

const summaryItemStyle = {
    display: 'flex',
    justifyContent: 'space-between',
    padding: '10px 0',
    borderBottom: '1px solid #eee'
};

export default Checkout;
