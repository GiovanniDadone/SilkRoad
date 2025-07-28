import React from "react";
import Button from "../ui/Button";
import "./AddressCard.css";

const AddressCard = ({ address, onEdit, onDelete }) => {
  return (
    <div className={`address-card ${address.isDefault ? "default" : ""}`}>
      {address.isDefault && <div className="default-badge">Default</div>}

      <div className="address-content">
        <h4>
          {address.firstName} {address.lastName}
        </h4>
        <p>{address.address}</p>
        <p>
          {address.city}, {address.state} {address.zipCode}
        </p>
        {address.phone && <p>Phone: {address.phone}</p>}
      </div>

      <div className="address-actions">
        <Button variant="outline" size="small" onClick={onEdit}>
          Edit
        </Button>
        <Button variant="outline" size="small" onClick={onDelete}>
          Delete
        </Button>
      </div>
    </div>
  );
};

export default AddressCard;
