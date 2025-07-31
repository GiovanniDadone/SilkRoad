// src/pages/ProductDetail.jsx
import React, { useState } from 'react';
import {
  Container,
  Grid,
  Typography,
  Box,
  Button,
  TextField,
  Paper,
  Chip,
  Breadcrumbs,
  Link,
  Alert,
} from '@mui/material';
import { Add, Remove, ShoppingCart, ArrowBack } from '@mui/icons-material';
import { useParams, useNavigate, Link as RouterLink } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useSelector } from 'react-redux';
import { productService } from '../services/productService';
import { cartService } from '../services/cartService';
import LoadingSpinner from '../components/UI/LoadingSpinner';
import { toast } from 'react-toastify';

const ProductDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { isAuthenticated } = useSelector((state) => state.auth);
  const [quantity, setQuantity] = useState(1);

  // Query per prodotto
  const { data: product, isLoading, error } = useQuery({
    queryKey: ['product', id],
    queryFn: () => productService.getProductById(id),
  });

  // Mutation per aggiungere al carrello
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

  const handleQuantityChange = (delta) => {
    const newQuantity = quantity + delta;
    if (newQuantity >= 1 && newQuantity <= (product?.stockQuantity || 0)) {
      setQuantity(newQuantity);
    }
  };

  const handleAddToCart = () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    addToCartMutation.mutate({
      productId: product.id,
      quantity: quantity,
    });
  };

  if (isLoading) return <LoadingSpinner />;
  if (error) return <Typography color="error">Errore nel caricamento del prodotto</Typography>;
  if (!product) return <Typography>Prodotto non trovato</Typography>;

  return (
    <Container maxWidth="lg">
      {/* Breadcrumbs */}
      <Breadcrumbs sx={{ mb: 3 }}>
        <Link component={RouterLink} to="/" underline="hover">
          Home
        </Link>
        <Link component={RouterLink} to="/products" underline="hover">
          Prodotti
        </Link>
        {product.categoryName && (
          <Link
            component={RouterLink}
            to={`/products?category=${product.categoryId}`}
            underline="hover"
          >
            {product.categoryName}
          </Link>
        )}
        <Typography color="text.primary">{product.name}</Typography>
      </Breadcrumbs>

      <Button
        startIcon={<ArrowBack />}
        onClick={() => navigate(-1)}
        sx={{ mb: 3 }}
      >
        Indietro
      </Button>

      <Grid container spacing={4}>
        {/* Immagine Prodotto */}
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 2 }}>
            <Box
              component="img"
              src={product.imageUrl || '/placeholder-image.jpg'}
              alt={product.name}
              sx={{
                width: '100%',
                height: 400,
                objectFit: 'cover',
                borderRadius: 1,
              }}
            />
          </Paper>
        </Grid>

        {/* Dettagli Prodotto */}
        <Grid item xs={12} md={6}>
          <Typography variant="h4" component="h1" gutterBottom>
            {product.name}
          </Typography>

          <Typography variant="h5" color="primary" sx={{ mb: 2 }}>
            €{product.price}
          </Typography>

          {product.categoryName && (
            <Chip
              label={product.categoryName}
              variant="outlined"
              sx={{ mb: 2 }}
            />
          )}

          <Box sx={{ mb: 3 }}>
            {product.isAvailable ? (
              <Chip label="Disponibile" color="success" />
            ) : (
              <Chip label="Esaurito" color="error" />
            )}
            <Typography variant="body2" sx={{ mt: 1 }}>
              {product.stockQuantity} pezzi disponibili
            </Typography>
          </Box>

          <Typography variant="body1" sx={{ mb: 3 }}>
            {product.description}
          </Typography>

          {product.sku && (
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
              SKU: {product.sku}
            </Typography>
          )}

          {/* Selettore Quantità */}
          {product.isAvailable && (
            <Box sx={{ mb: 3 }}>
              <Typography variant="body1" gutterBottom>
                Quantità:
              </Typography>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Button
                  variant="outlined"
                  size="small"
                  onClick={() => handleQuantityChange(-1)}
                  disabled={quantity <= 1}
                >
                  <Remove />
                </Button>
                <TextField
                  value={quantity}
                  size="small"
                  sx={{ width: 80 }}
                  inputProps={{
                    min: 1,
                    max: product.stockQuantity,
                    type: 'number',
                    style: { textAlign: 'center' },
                  }}
                  onChange={(e) => {
                    const value = parseInt(e.target.value) || 1;
                    if (value >= 1 && value <= product.stockQuantity) {
                      setQuantity(value);
                    }
                  }}
                />
                <Button
                  variant="outlined"
                  size="small"
                  onClick={() => handleQuantityChange(1)}
                  disabled={quantity >= product.stockQuantity}
                >
                  <Add />
                </Button>
              </Box>
            </Box>
          )}

          {/* Pulsante Aggiungi al Carrello */}
          <Box sx={{ mb: 3 }}>
            <Button
              variant="contained"
              size="large"
              startIcon={<ShoppingCart />}
              onClick={handleAddToCart}
              disabled={!product.isAvailable || addToCartMutation.isLoading}
              fullWidth
            >
              {addToCartMutation.isLoading
                ? 'Aggiunta in corso...'
                : 'Aggiungi al Carrello'
              }
            </Button>

            {!isAuthenticated && (
              <Alert severity="info" sx={{ mt: 2 }}>
                <RouterLink to="/login">Effettua il login</RouterLink> per aggiungere prodotti al carrello
              </Alert>
            )}
          </Box>
        </Grid>
      </Grid>
    </Container>
  );
};

export default ProductDetail;