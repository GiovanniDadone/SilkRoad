import React from "react";
import "./ProductInfo.css";

const ProductInfo = ({ product }) => {
  const formatPrice = (price) => {
    return `$${parseFloat(price).toFixed(2)}`;
  };

  return (
    <div className="product-info">
      <h1 className="product-title">{product.name}</h1>

      {product.category && (
        <p className="product-category">{product.category}</p>
      )}

      <div className="product-price-section">
        {product.originalPrice && product.originalPrice > product.price ? (
          <div className="price-with-discount">
            <span className="current-price">{formatPrice(product.price)}</span>
            <span className="original-price">
              {formatPrice(product.originalPrice)}
            </span>
            <span className="discount-percentage">
              {Math.round(
                ((product.originalPrice - product.price) /
                  product.originalPrice) *
                  100
              )}
              % OFF
            </span>
          </div>
        ) : (
          <span className="current-price">{formatPrice(product.price)}</span>
        )}
      </div>

      {product.rating && (
        <div className="product-rating">
          <div className="stars">
            {[...Array(5)].map((_, i) => (
              <span
                key={i}
                className={`star ${
                  i < Math.floor(product.rating) ? "filled" : ""
                }`}
              >
                ★
              </span>
            ))}
          </div>
          <span className="rating-text">
            {product.rating.toFixed(1)} ({product.reviewCount || 0} reviews)
          </span>
        </div>
      )}

      <div className="product-description">
        <h3>Description</h3>
        <p>{product.description || "No description available."}</p>
      </div>

      {product.features && product.features.length > 0 && (
        <div className="product-features">
          <h3>Features</h3>
          <ul>
            {product.features.map((feature, index) => (
              <li key={index}>{feature}</li>
            ))}
          </ul>
        </div>
      )}

      <div className="product-stock">
        {product.inStock ? (
          <span className="in-stock">✓ In Stock</span>
        ) : (
          <span className="out-of-stock">✗ Out of Stock</span>
        )}
        {product.stock && (
          <span className="stock-count">({product.stock} available)</span>
        )}
      </div>
    </div>
  );
};

export default ProductInfo;
