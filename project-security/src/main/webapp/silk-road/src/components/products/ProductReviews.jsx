import React, { useState, useEffect } from 'react'
import Button from '../ui/Button'
import Loading from '../ui/Loading'
import './ProductReviews.css'

const ProductReviews = ({ productId }) => {
  const [reviews, setReviews] = useState([])
  const [loading, setLoading] = useState(true)
  const [showReviewForm, setShowReviewForm] = useState(false)
  const [newReview, setNewReview] = useState({
    rating: 5,
    title: '',
    comment: '',
  })

  useEffect(() => {
    // Mock reviews data - replace with actual API call
    const mockReviews = [
      {
        id: 1,
        userName: 'John Doe',
        rating: 5,
        title: 'Great product!',
        comment: 'Really happy with this purchase. Quality is excellent.',
        date: '2024-01-15',
        verified: true,
      },
      {
        id: 2,
        userName: 'Jane Smith',
        rating: 4,
        title: 'Good value',
        comment: 'Good product for the price. Fast shipping too.',
        date: '2024-01-10',
        verified: true,
      },
    ]

    setTimeout(() => {
      setReviews(mockReviews)
      setLoading(false)
    }, 500)
  }, [productId])

  const handleSubmitReview = (e) => {
    e.preventDefault()
    // Mock submission - replace with actual API call
    const review = {
      id: Date.now(),
      userName: 'Current User',
      rating: newReview.rating,
      title: newReview.title,
      comment: newReview.comment,
      date: new Date().toISOString().split('T')[0],
      verified: false,
    }

    setReviews([review, ...reviews])
    setNewReview({ rating: 5, title: '', comment: '' })
    setShowReviewForm(false)
  }

  const renderStars = (rating) => {
    return [...Array(5)].map((_, i) => (
      <span key={i} className={`star ${i < rating ? 'filled' : ''}`}>
        ★
      </span>
    ))
  }

  if (loading) {
    return <Loading text='Loading reviews...' />
  }

  return (
    <div className='product-reviews'>
      <div className='reviews-header'>
        <h3>Customer Reviews ({reviews.length})</h3>
        <Button variant='outline' onClick={() => setShowReviewForm(!showReviewForm)}>
          Write a Review
        </Button>
      </div>

      {showReviewForm && (
        <form onSubmit={handleSubmitReview} className='review-form'>
          <h4>Write Your Review</h4>

          <div className='form-group'>
            <label>Rating:</label>
            <div className='rating-input'>
              {[1, 2, 3, 4, 5].map((star) => (
                <button
                  key={star}
                  type='button'
                  className={`star-button ${newReview.rating >= star ? 'active' : ''}`}
                  onClick={() => setNewReview({ ...newReview, rating: star })}>
                  ★
                </button>
              ))}
            </div>
          </div>

          <div className='form-group'>
            <label>Title:</label>
            <input
              type='text'
              value={newReview.title}
              onChange={(e) => setNewReview({ ...newReview, title: e.target.value })}
              required
            />
          </div>

          <div className='form-group'>
            <label>Review:</label>
            <textarea
              value={newReview.comment}
              onChange={(e) => setNewReview({ ...newReview, comment: e.target.value })}
              rows='4'
              required
            />
          </div>

          <div className='form-actions'>
            <Button type='button' variant='outline' onClick={() => setShowReviewForm(false)}>
              Cancel
            </Button>
            <Button type='submit'>Submit Review</Button>
          </div>
        </form>
      )}

      <div className='reviews-list'>
        {reviews.length === 0 ? (
          <p className='no-reviews'>No reviews yet. Be the first to review this product!</p>
        ) : (
          reviews.map((review) => (
            <div key={review.id} className='review-item'>
              <div className='review-header'>
                <div className='reviewer-info'>
                  <span className='reviewer-name'>{review.userName}</span>
                  {review.verified && <span className='verified-badge'>Verified Purchase</span>}
                </div>
                <div className='review-date'>{review.date}</div>
              </div>

              <div className='review-rating'>{renderStars(review.rating)}</div>

              <h4 className='review-title'>{review.title}</h4>
              <p className='review-comment'>{review.comment}</p>
            </div>
          ))
        )}
      </div>
    </div>
  )
}

export default ProductReviews
