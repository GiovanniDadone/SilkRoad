import React, { useState } from "react";
import { useUser } from "../../contexts/UserContext";
import Button from "../ui/Button";
import Input from "../ui/Input";
import "./Settings.css";

const Settings = () => {
  const { user, updateSettings, changePassword } = useUser();
  const [settings, setSettings] = useState({
    emailNotifications: user?.settings?.emailNotifications || true,
    smsNotifications: user?.settings?.smsNotifications || false,
    marketingEmails: user?.settings?.marketingEmails || false,
    orderUpdates: user?.settings?.orderUpdates || true,
  });
  const [passwordData, setPasswordData] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });
  const [passwordErrors, setPasswordErrors] = useState({});
  const [saving, setSaving] = useState(false);

  const handleSettingsChange = (e) => {
    const { name, checked } = e.target;
    setSettings((prev) => ({ ...prev, [name]: checked }));
  };

  const handlePasswordChange = (e) => {
    const { name, value } = e.target;
    setPasswordData((prev) => ({ ...prev, [name]: value }));

    if (passwordErrors[name]) {
      setPasswordErrors((prev) => ({ ...prev, [name]: "" }));
    }
  };

  const handleSettingsSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      await updateSettings(settings);
    } catch (error) {
      console.error("Failed to update settings:", error);
    } finally {
      setSaving(false);
    }
  };

  const validatePasswordForm = () => {
    const errors = {};

    if (!passwordData.currentPassword) {
      errors.currentPassword = "Current password is required";
    }
    if (!passwordData.newPassword) {
      errors.newPassword = "New password is required";
    } else if (passwordData.newPassword.length < 8) {
      errors.newPassword = "Password must be at least 8 characters";
    }
    if (passwordData.newPassword !== passwordData.confirmPassword) {
      errors.confirmPassword = "Passwords do not match";
    }

    setPasswordErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handlePasswordSubmit = async (e) => {
    e.preventDefault();
    if (validatePasswordForm()) {
      try {
        await changePassword(
          passwordData.currentPassword,
          passwordData.newPassword
        );
        setPasswordData({
          currentPassword: "",
          newPassword: "",
          confirmPassword: "",
        });
        alert("Password changed successfully!");
      } catch (error) {
        setPasswordErrors({ currentPassword: "Current password is incorrect" });
      }
    }
  };

  return (
    <div className="settings">
      <div className="settings-section">
        <h2>Notification Preferences</h2>
        <form onSubmit={handleSettingsSubmit} className="settings-form">
          <div className="setting-item">
            <label>
              <input
                type="checkbox"
                name="emailNotifications"
                checked={settings.emailNotifications}
                onChange={handleSettingsChange}
              />
              Email notifications
            </label>
            <p className="setting-description">
              Receive general notifications via email
            </p>
          </div>

          <div className="setting-item">
            <label>
              <input
                type="checkbox"
                name="smsNotifications"
                checked={settings.smsNotifications}
                onChange={handleSettingsChange}
              />
              SMS notifications
            </label>
            <p className="setting-description">
              Receive notifications via text message
            </p>
          </div>

          <div className="setting-item">
            <label>
              <input
                type="checkbox"
                name="orderUpdates"
                checked={settings.orderUpdates}
                onChange={handleSettingsChange}
              />
              Order updates
            </label>
            <p className="setting-description">
              Get notified about order status changes
            </p>
          </div>

          <div className="setting-item">
            <label>
              <input
                type="checkbox"
                name="marketingEmails"
                checked={settings.marketingEmails}
                onChange={handleSettingsChange}
              />
              Marketing emails
            </label>
            <p className="setting-description">
              Receive promotional offers and updates
            </p>
          </div>

          <Button type="submit" disabled={saving}>
            {saving ? "Saving..." : "Save Settings"}
          </Button>
        </form>
      </div>

      <div className="settings-section">
        <h2>Change Password</h2>
        <form onSubmit={handlePasswordSubmit} className="password-form">
          <Input
            label="Current Password"
            type="password"
            name="currentPassword"
            value={passwordData.currentPassword}
            onChange={handlePasswordChange}
            error={passwordErrors.currentPassword}
            required
          />

          <Input
            label="New Password"
            type="password"
            name="newPassword"
            value={passwordData.newPassword}
            onChange={handlePasswordChange}
            error={passwordErrors.newPassword}
            required
          />

          <Input
            label="Confirm New Password"
            type="password"
            name="confirmPassword"
            value={passwordData.confirmPassword}
            onChange={handlePasswordChange}
            error={passwordErrors.confirmPassword}
            required
          />

          <Button type="submit">Change Password</Button>
        </form>
      </div>
    </div>
  );
};

export default Settings;
