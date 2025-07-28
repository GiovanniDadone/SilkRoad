/**
 * Utility functions for date formatting and manipulation
 */

/**
 * Format a date string or Date object into a human-readable format
 * @param {string|Date} date - The date to format
 * @param {object} options - Formatting options
 * @returns {string} Formatted date string
 */
export const formatDate = (date, options = {}) => {
    const defaultOptions = {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        ...options
    };

    // Handle both string dates and Date objects
    const dateObj = date instanceof Date ? date : new Date(date);

    // Check if date is valid
    if (isNaN(dateObj.getTime())) {
        return 'Invalid date';
    }

    try {
        return new Intl.DateTimeFormat('en-US', defaultOptions).format(dateObj);
    } catch (error) {
        console.error('Error formatting date:', error);
        return String(date);
    }
};

/**
 * Calculate the time difference between now and a given date
 * @param {string|Date} date - The date to compare
 * @returns {string} Human-readable time difference
 */
export const timeAgo = (date) => {
    const dateObj = date instanceof Date ? date : new Date(date);
    const now = new Date();

    // Check if date is valid
    if (isNaN(dateObj.getTime())) {
        return 'Invalid date';
    }

    const seconds = Math.floor((now - dateObj) / 1000);

    // Less than a minute
    if (seconds < 60) {
        return 'just now';
    }

    // Less than an hour
    const minutes = Math.floor(seconds / 60);
    if (minutes < 60) {
        return `${minutes} minute${minutes !== 1 ? 's' : ''} ago`;
    }

    // Less than a day
    const hours = Math.floor(minutes / 60);
    if (hours < 24) {
        return `${hours} hour${hours !== 1 ? 's' : ''} ago`;
    }

    // Less than a month
    const days = Math.floor(hours / 24);
    if (days < 30) {
        return `${days} day${days !== 1 ? 's' : ''} ago`;
    }

    // Less than a year
    const months = Math.floor(days / 30);
    if (months < 12) {
        return `${months} month${months !== 1 ? 's' : ''} ago`;
    }

    // More than a year
    const years = Math.floor(months / 12);
    return `${years} year${years !== 1 ? 's' : ''} ago`;
};

/**
 * Format a date as a short date string (MM/DD/YYYY)
 * @param {string|Date} date - The date to format
 * @returns {string} Formatted short date
 */
export const formatShortDate = (date) => {
    return formatDate(date, {
        year: 'numeric',
        month: 'numeric',
        day: 'numeric',
        hour: undefined,
        minute: undefined
    });
};

/**
 * Format a date as a time string (HH:MM AM/PM)
 * @param {string|Date} date - The date to format
 * @returns {string} Formatted time
 */
export const formatTime = (date) => {
    return formatDate(date, {
        year: undefined,
        month: undefined,
        day: undefined,
        hour: '2-digit',
        minute: '2-digit'
    });
};