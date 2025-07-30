// src/components/Product/ProductCard.jsx
import React from 'react';
import {
  Card,
  CardMedia,
  CardContent,
  CardActions,
  Typography,
  Button,
  Box,
  Chip,
} from '@mui/material';
import { ShoppingCart, Visibility } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { cartService } from '../../services/cartService';
import { toast } from 'react-toastify';

const ProductCard = ({ product }) => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  
  const addToCartMutation = useMutation({
    mutationFn: (productData) => cartService.addToCart(productData),
    onSuccess: () => {
      toast.success('Prodotto aggiunto al carrello!');
      queryClient.invalidateQueries(['cartCount']);
      queryClient.invalidateQueries(['cart']);
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Errore nell\'aggiunta al carrello');
    },
  });

  const handleAddToCart = (e) => {
    e.stopPropagation();
    addToCartMutation.mutate({
      productId: product.id,
      quantity: 1,
    });
  };

  const handleViewProduct = () => {
    navigate(`/products/${product.id}`);
  };

  return (
    <Card
      sx={{
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        cursor: 'pointer',
        '&:hover': {
          boxShadow: (theme) => theme.shadows[4],
        },
      }}
      onClick={handleViewProduct}
    >
      <CardMedia
        component="img"
        height="200"
        image={product.imageUrl || '/placeholder-image.jpg'}
        alt={product.name}
        sx={{ objectFit: 'cover' }}
      />
      
      <CardContent sx={{ flexGrow: 1 }}>
        <Typography gutterBottom variant="h6" component="h2" noWrap>
          {product.name}
        </Typography>
        
        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
          {product.description?.substring(0, 100)}
          {product.description?.length > 100 && '...'}
        </Typography>
        
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
          <Typography variant="h6" color="primary">
            €{product.price}
          </Typography>
          
          {product.isAvailable ? (
            <Chip label="Disponibile" color="success" size="small" />
          ) : (
            <Chip label="Esaurito" color="error" size="small" />
          )}
        </Box>
        
        {product.categoryName && (
          <Typography variant="caption" color="text.secondary">
            Categoria: {product.categoryName}
          </Typography>
        )}
      </CardContent>
      
      <CardActions sx={{ justifyContent: 'space-between' }}>
        <Button
          size="small"
          startIcon={<Visibility />}
          onClick={handleViewProduct}
        >
          Dettagli
        </Button>
        
        <Button
          size="small"
          variant="contained"
          startIcon={<ShoppingCart />}
          disabled={!product.isAvailable || addToCartMutation.isLoading}
          onClick={handleAddToCart}
        >
          {addToCartMutation.isLoading ? 'Aggiunta...' : 'Aggiungi'}
        </Button>
      </CardActions>
    </Card>
  );
};
