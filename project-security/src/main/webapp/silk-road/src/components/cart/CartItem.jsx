import React from "react";
import { useCart } from "../../contexts/CartContext";
import Button from "../ui/Button";
import "./CartItem.css";

const CartItem = ({ item }) => {
  const { updateQuantity, removeFromCart } = useCart();

  const handleQuantityChange = (newQuantity) => {
    if (newQuantity === 0) {
      removeFromCart(item.id, item.size);
    } else {
      updateQuantity(item.id, newQuantity, item.size);
    }
  };

  return (
    <div className="cart-item">
      <div className="cart-item-image">
        <img src={item.image} alt={item.name} />
      </div>

      <div className="cart-item-details">
        <h3 className="cart-item-name">{item.name}</h3>
        {item.size && <p className="cart-item-size">Size: {item.size}</p>}
        <p className="cart-item-price">${item.price}</p>
      </div>

      <div className="cart-item-quantity">
        <button
          className="quantity-btn"
          onClick={() => handleQuantityChange(item.quantity - 1)}
        >
          -
        </button>
        <span className="quantity">{item.quantity}</span>
        <button
          className="quantity-btn"
          onClick={() => handleQuantityChange(item.quantity + 1)}
        >
          +
        </button>
      </div>

      <div className="cart-item-total">
        <p className="item-total">${(item.price * item.quantity).toFixed(2)}</p>
        <Button
          variant="outline"
          size="small"
          onClick={() => removeFromCart(item.id, item.size)}
        >
          Remove
        </Button>
      </div>
    </div>
  );
};

export default CartItem;
