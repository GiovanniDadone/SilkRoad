import React from "react";
import "./OrderSummary.css";

const OrderSummary = ({ items }) => {
  const subtotal = items.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  );
  const shipping = subtotal > 100 ? 0 : 10;
  const tax = subtotal * 0.08;
  const total = subtotal + shipping + tax;

  return (
    <div className="order-summary">
      <h3>Order Summary</h3>

      <div className="summary-items">
        {items.map((item) => (
          <div
            key={`${item.id}-${item.size || "default"}`}
            className="summary-item"
          >
            <img src={item.image} alt={item.name} />
            <div className="item-details">
              <p className="item-name">{item.name}</p>
              {item.size && <p className="item-size">Size: {item.size}</p>}
              <p className="item-quantity">Qty: {item.quantity}</p>
            </div>
            <p className="item-price">
              ${(item.price * item.quantity).toFixed(2)}
            </p>
          </div>
        ))}
      </div>

      <div className="summary-totals">
        <div className="summary-line">
          <span>Subtotal</span>
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
      </div>
    </div>
  );
};

export default OrderSummary;
