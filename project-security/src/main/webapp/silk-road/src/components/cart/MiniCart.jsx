import React from "react";
import { useCart } from "../../contexts/CartContext";
import { useNavigate } from "react-router-dom";
import Button from "../ui/Button";
import "./MiniCart.css";

const MiniCart = ({ isOpen, onClose }) => {
  const { cartItems, getTotalPrice, getTotalItems } = useCart();
  const navigate = useNavigate();

  const handleViewCart = () => {
    navigate("/cart");
    onClose();
  };

  const handleCheckout = () => {
    navigate("/checkout");
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="mini-cart-overlay" onClick={onClose}>
      <div className="mini-cart" onClick={(e) => e.stopPropagation()}>
        <div className="mini-cart-header">
          <h3>Cart ({getTotalItems()})</h3>
          <button className="close-btn" onClick={onClose}>
            ×
          </button>
        </div>

        <div className="mini-cart-items">
          {cartItems.length === 0 ? (
            <p className="empty-message">Your cart is empty</p>
          ) : (
            cartItems.slice(0, 3).map((item) => (
              <div
                key={`${item.id}-${item.size || "default"}`}
                className="mini-cart-item"
              >
                <img src={item.image} alt={item.name} />
                <div className="item-info">
                  <p className="item-name">{item.name}</p>
                  <p className="item-details">
                    {item.size && `Size: ${item.size} • `}
                    Qty: {item.quantity}
                  </p>
                </div>
                <p className="item-price">
                  ${(item.price * item.quantity).toFixed(2)}
                </p>
              </div>
            ))
          )}
          {cartItems.length > 3 && (
            <p className="more-items">+{cartItems.length - 3} more items</p>
          )}
        </div>

        {cartItems.length > 0 && (
          <div className="mini-cart-footer">
            <div className="total">
              <strong>Total: ${getTotalPrice().toFixed(2)}</strong>
            </div>
            <div className="mini-cart-actions">
              <Button variant="outline" onClick={handleViewCart}>
                View Cart
              </Button>
              <Button onClick={handleCheckout}>Checkout</Button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default MiniCart;
