import React, { useState } from "react";
import { useUser } from "../../contexts/UserContext";
import Button from "../ui/Button";
import Input from "../ui/Input";
import { validateEmail, validatePhone } from "../../utils/validation";
import "./CheckoutForm.css";

const CheckoutForm = ({ onSubmit }) => {
  const { user } = useUser();
  const [formData, setFormData] = useState({
    email: user?.email || "",
    firstName: user?.firstName || "",
    lastName: user?.lastName || "",
    address: "",
    city: "",
    state: "",
    zipCode: "",
    phone: user?.phone || "",
  });
  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));

    // Clear error when user starts typing
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: "" }));
    }
  };

  const validateForm = () => {
    const newErrors = {};

    if (!formData.email) newErrors.email = "Email is required";
    else if (!validateEmail(formData.email))
      newErrors.email = "Invalid email format";

    if (!formData.firstName) newErrors.firstName = "First name is required";
    if (!formData.lastName) newErrors.lastName = "Last name is required";
    if (!formData.address) newErrors.address = "Address is required";
    if (!formData.city) newErrors.city = "City is required";
    if (!formData.state) newErrors.state = "State is required";
    if (!formData.zipCode) newErrors.zipCode = "ZIP code is required";
    if (!formData.phone) newErrors.phone = "Phone is required";
    else if (!validatePhone(formData.phone))
      newErrors.phone = "Invalid phone format";

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validateForm()) {
      onSubmit(formData);
    }
  };

  return (
    <form className="checkout-form" onSubmit={handleSubmit}>
      <h2>Shipping Information</h2>

      <div className="form-section">
        <h3>Contact Information</h3>
        <Input
          label="Email"
          type="email"
          name="email"
          value={formData.email}
          onChange={handleChange}
          error={errors.email}
          required
        />
      </div>

      <div className="form-section">
        <h3>Shipping Address</h3>
        <div className="form-row">
          <Input
            label="First Name"
            name="firstName"
            value={formData.firstName}
            onChange={handleChange}
            error={errors.firstName}
            required
          />
          <Input
            label="Last Name"
            name="lastName"
            value={formData.lastName}
            onChange={handleChange}
            error={errors.lastName}
            required
          />
        </div>

        <Input
          label="Address"
          name="address"
          value={formData.address}
          onChange={handleChange}
          error={errors.address}
          required
        />

        <div className="form-row">
          <Input
            label="City"
            name="city"
            value={formData.city}
            onChange={handleChange}
            error={errors.city}
            required
          />
          <Input
            label="State"
            name="state"
            value={formData.state}
            onChange={handleChange}
            error={errors.state}
            required
          />
          <Input
            label="ZIP Code"
            name="zipCode"
            value={formData.zipCode}
            onChange={handleChange}
            error={errors.zipCode}
            required
          />
        </div>

        <Input
          label="Phone"
          type="tel"
          name="phone"
          value={formData.phone}
          onChange={handleChange}
          error={errors.phone}
          required
        />
      </div>

      <Button type="submit" size="large">
        Continue to Payment
      </Button>
    </form>
  );
};

export default CheckoutForm;
