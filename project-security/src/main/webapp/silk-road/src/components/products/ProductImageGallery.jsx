import React, { useState } from 'react'
import './ProductImageGallery.css'

const ProductImageGallery = ({ images = [] }) => {
  const [selectedImage, setSelectedImage] = useState(0)

  // Ensure we have at least one image
  const imageList = images.length > 0 ? images : ['/placeholder-image.jpg']

  return (
    <div className='product-image-gallery'>
      <div className='main-image'>
        <img
          src={imageList[selectedImage]}
          alt='Product'
          onError={(e) => {
            e.target.src = '/placeholder-image.jpg'
          }}
        />
      </div>

      {imageList.length > 1 && (
        <div className='image-thumbnails'>
          {imageList.map((image, index) => (
            <button
              key={index}
              className={`thumbnail ${selectedImage === index ? 'active' : ''}`}
              onClick={() => setSelectedImage(index)}>
              <img
                src={image}
                alt={`Product ${index + 1}`}
                onError={(e) => {
                  e.target.src = '/placeholder-image.jpg'
                }}
              />
            </button>
          ))}
        </div>
      )}
    </div>
  )
}

export default ProductImageGallery
