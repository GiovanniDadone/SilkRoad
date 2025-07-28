import React from "react";
import { useCart } from "../../contexts/CartContext";
import "./CartSummary.css";

const CartSummary = () => {
  const { getTotalPrice, getTotalItems } = useCart();

  const subtotal = getTotalPrice();
  const shipping = subtotal > 100 ? 0 : 10;
  const tax = subtotal * 0.08;
  const total = subtotal + shipping + tax;

  return (
    <div className="cart-summary">
      <h3>Order Summary</h3>

      <div className="summary-line">
        <span>Items ({getTotalItems()})</span>
        <span>${subtotal.toFixed(2)}</span>
      </div>

      <div className="summary-line">
        <span>Shipping</span>
        <span>{shipping === 0 ? "FREE" : `$${shipping.toFixed(2)}`}</span>
      </div>

      <div className="summary-line">
        <span>Tax</span>
        <span>${tax.toFixed(2)}</span>
      </div>

      <div className="summary-line total">
        <span>Total</span>
        <span>${total.toFixed(2)}</span>
      </div>

      {subtotal < 100 && (
        <p className="free-shipping-notice">
          Add ${(100 - subtotal).toFixed(2)} more for free shipping!
        </p>
      )}
    </div>
  );
};

export default CartSummary;
