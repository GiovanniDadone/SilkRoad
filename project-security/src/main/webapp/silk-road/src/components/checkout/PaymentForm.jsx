import React, { useState } from "react";
import Button from "../ui/Button";
import Input from "../ui/Input";
import { validateCreditCard, validateCVV } from "../../utils/validation";
import "./PaymentForm.css";

const PaymentForm = ({ orderData, onSubmit }) => {
  const [paymentData, setPaymentData] = useState({
    cardNumber: "",
    expiryDate: "",
    cvv: "",
    cardName: "",
    paymentMethod: "credit",
  });
  const [errors, setErrors] = useState({});
  const [processing, setProcessing] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    let formattedValue = value;

    // Format card number
    if (name === "cardNumber") {
      formattedValue = value
        .replace(/\s/g, "")
        .replace(/(.{4})/g, "$1 ")
        .trim();
    }

    // Format expiry date
    if (name === "expiryDate") {
      formattedValue = value.replace(/\D/g, "").replace(/(\d{2})(\d)/, "$1/$2");
    }

    setPaymentData((prev) => ({ ...prev, [name]: formattedValue }));

    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: "" }));
    }
  };

  const validateForm = () => {
    const newErrors = {};

    if (!paymentData.cardNumber) {
      newErrors.cardNumber = "Card number is required";
    } else if (!validateCreditCard(paymentData.cardNumber.replace(/\s/g, ""))) {
      newErrors.cardNumber = "Invalid card number";
    }

    if (!paymentData.expiryDate) {
      newErrors.expiryDate = "Expiry date is required";
    } else if (!/^\d{2}\/\d{2}$/.test(paymentData.expiryDate)) {
      newErrors.expiryDate = "Invalid expiry date format (MM/YY)";
    }

    if (!paymentData.cvv) {
      newErrors.cvv = "CVV is required";
    } else if (!validateCVV(paymentData.cvv)) {
      newErrors.cvv = "Invalid CVV";
    }

    if (!paymentData.cardName) {
      newErrors.cardName = "Cardholder name is required";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (validateForm()) {
      setProcessing(true);
      try {
        // Simulate payment processing
        await new Promise((resolve) => setTimeout(resolve, 2000));
        onSubmit(paymentData);
      } catch (error) {
        console.error("Payment failed:", error);
      } finally {
        setProcessing(false);
      }
    }
  };

  return (
    <form className="payment-form" onSubmit={handleSubmit}>
      <h2>Payment Information</h2>

      <div className="payment-methods">
        <label className="payment-method">
          <input
            type="radio"
            name="paymentMethod"
            value="credit"
            checked={paymentData.paymentMethod === "credit"}
            onChange={handleChange}
          />
          Credit Card
        </label>
      </div>

      <div className="form-section">
        <Input
          label="Card Number"
          name="cardNumber"
          value={paymentData.cardNumber}
          onChange={handleChange}
          error={errors.cardNumber}
          placeholder="1234 5678 9012 3456"
          maxLength={19}
          required
        />

        <div className="form-row">
          <Input
            label="Expiry Date"
            name="expiryDate"
            value={paymentData.expiryDate}
            onChange={handleChange}
            error={errors.expiryDate}
            placeholder="MM/YY"
            maxLength={5}
            required
          />
          <Input
            label="CVV"
            name="cvv"
            value={paymentData.cvv}
            onChange={handleChange}
            error={errors.cvv}
            placeholder="123"
            maxLength={4}
            required
          />
        </div>

        <Input
          label="Cardholder Name"
          name="cardName"
          value={paymentData.cardName}
          onChange={handleChange}
          error={errors.cardName}
          placeholder="John Doe"
          required
        />
      </div>

      <div className="order-summary-mini">
        <h3>Order Summary</h3>
        <p>
          Shipping to: {orderData.address}, {orderData.city}
        </p>
        <p>Total: ${orderData.total?.toFixed(2)}</p>
      </div>

      <Button
        type="submit"
        size="large"
        disabled={processing}
        className="place-order-btn"
      >
        {processing ? "Processing..." : "Place Order"}
      </Button>
    </form>
  );
};

export default PaymentForm;
