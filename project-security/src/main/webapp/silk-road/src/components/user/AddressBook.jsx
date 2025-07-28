import React, { useState } from "react";
import { useUser } from "../../contexts/UserContext";
import Button from "../ui/Button";
import AddressForm from "./AddressForm";
import AddressCard from "./AddressCard";
import "./AddressBook.css";

const AddressBook = () => {
  const { user, addAddress, updateAddress, deleteAddress } = useUser();
  const [showForm, setShowForm] = useState(false);
  const [editingAddress, setEditingAddress] = useState(null);

  const addresses = user?.addresses || [];

  const handleAddAddress = async (addressData) => {
    try {
      await addAddress(addressData);
      setShowForm(false);
    } catch (error) {
      console.error("Failed to add address:", error);
    }
  };

  const handleUpdateAddress = async (addressData) => {
    try {
      await updateAddress(editingAddress.id, addressData);
      setEditingAddress(null);
    } catch (error) {
      console.error("Failed to update address:", error);
    }
  };

  const handleDeleteAddress = async (addressId) => {
    if (window.confirm("Are you sure you want to delete this address?")) {
      try {
        await deleteAddress(addressId);
      } catch (error) {
        console.error("Failed to delete address:", error);
      }
    }
  };

  const handleEdit = (address) => {
    setEditingAddress(address);
    setShowForm(false);
  };

  const handleCancel = () => {
    setShowForm(false);
    setEditingAddress(null);
  };

  return (
    <div className="address-book">
      <div className="address-header">
        <h2>Address Book</h2>
        {!showForm && !editingAddress && (
          <Button onClick={() => setShowForm(true)}>Add New Address</Button>
        )}
      </div>

      {showForm && (
        <div className="address-form-container">
          <h3>Add New Address</h3>
          <AddressForm onSubmit={handleAddAddress} onCancel={handleCancel} />
        </div>
      )}

      {editingAddress && (
        <div className="address-form-container">
          <h3>Edit Address</h3>
          <AddressForm
            initialData={editingAddress}
            onSubmit={handleUpdateAddress}
            onCancel={handleCancel}
          />
        </div>
      )}

      <div className="addresses-list">
        {addresses.length === 0 ? (
          <div className="no-addresses">
            <p>No addresses saved yet.</p>
          </div>
        ) : (
          addresses.map((address) => (
            <AddressCard
              key={address.id}
              address={address}
              onEdit={() => handleEdit(address)}
              onDelete={() => handleDeleteAddress(address.id)}
            />
          ))
        )}
      </div>
    </div>
  );
};

export default AddressBook;
