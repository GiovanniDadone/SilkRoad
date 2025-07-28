import React from "react";
import Button from "../ui/Button";
import "./OrderConfirmation.css";

const OrderConfirmation = ({ orderId, onComplete }) => {
  return (
    <div className="order-confirmation">
      <div className="confirmation-icon">✅</div>

      <h2>Order Confirmed!</h2>
      <p className="order-number">Order #{orderId}</p>

      <div className="confirmation-details">
        <p>
          Thank you for your purchase! We've received your order and will begin
          processing it shortly.
        </p>
        <p>
          You'll receive an email confirmation with tracking information once
          your order ships.
        </p>
      </div>

      <div className="confirmation-actions">
        <Button onClick={onComplete}>View My Orders</Button>
        <Button variant="outline" onClick={() => (window.location.href = "/")}>
          Continue Shopping
        </Button>
      </div>
    </div>
  );
};

export default OrderConfirmation;
