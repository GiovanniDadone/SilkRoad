import React, { useState } from 'react';
import {
  Container,
  Typography,
  Box,
  Paper,
  Button,
  IconButton,
  Grid,
  Card,
  CardContent,
  TextField,
  Divider,
  Alert,
} from '@mui/material';
import {
  Add,
  Remove,
  Delete,
  ShoppingCartCheckout,
  ArrowBack,
} from '@mui/icons-material';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { cartService } from '../services/cartService';
import LoadingSpinner from '../components/UI/LoadingSpinner';
import { toast } from 'react-toastify';

const Cart = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  // Query per recuperare il carrello
  const { data: cart, isLoading, error } = useQuery({
    queryKey: ['cart'],
    queryFn: cartService.getCart,
  });

  // Mutation per aggiornare quantità
  const updateQuantityMutation = useMutation({
    mutationFn: ({ itemId, quantity }) => 
      cartService.updateCartItem(itemId, quantity),
    onSuccess: () => {
      queryClient.invalidateQueries(['cart']);
      queryClient.invalidateQueries(['cartCount']);
      toast.success('Carrello aggiornato');
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Errore nell\'aggiornamento');
    },
  });

  // Mutation per rimuovere item
  const removeItemMutation = useMutation({
    mutationFn: (itemId) => cartService.removeFromCart(itemId),
    onSuccess: () => {
      queryClient.invalidateQueries(['cart']);
      queryClient.invalidateQueries(['cartCount']);
      toast.success('Prodotto rimosso dal carrello');
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Errore nella rimozione');
    },
  });

  // Mutation per svuotare il carrello
  const clearCartMutation = useMutation({
    mutationFn: cartService.clearCart,
    onSuccess: () => {
      queryClient.invalidateQueries(['cart']);
      queryClient.invalidateQueries(['cartCount']);
      toast.success('Carrello svuotato');
    },
  });

  const handleQuantityChange = (itemId, currentQuantity, delta) => {
    const newQuantity = currentQuantity + delta;
    if (newQuantity >= 1) {
      updateQuantityMutation.mutate({ itemId, quantity: newQuantity });
    }
  };

  const handleRemoveItem = (itemId) => {
    if (window.confirm('Sei sicuro di voler rimuovere questo prodotto?')) {
      removeItemMutation.mutate(itemId);
    }
  };

  const handleClearCart = () => {
    if (window.confirm('Sei sicuro di voler svuotare il carrello?')) {
      clearCartMutation.mutate();
    }
  };

  const handleCheckout = () => {
    toast.info('Funzionalità checkout in sviluppo');
    // navigate('/checkout');
  };

  if (isLoading) return <LoadingSpinner />;
  if (error) return <Typography color="error">Errore nel caricamento del carrello</Typography>;

  const isEmpty = !cart?.items || cart.items.length === 0;

  return (
    <Container maxWidth="lg">
      <Box sx={{ mb: 3 }}>
        <Button
          startIcon={<ArrowBack />}
          onClick={() => navigate('/products')}
        >
          Continua lo shopping
        </Button>
      </Box>

      <Typography variant="h4" component="h1" gutterBottom>
        Il tuo carrello
      </Typography>

      {isEmpty ? (
        <Paper sx={{ p: 4, textAlign: 'center' }}>
          <Typography variant="h6" gutterBottom>
            Il tuo carrello è vuoto
          </Typography>
          <Button
            variant="contained"
            onClick={() => navigate('/products')}
            sx={{ mt: 2 }}
          >
            Vai ai prodotti
          </Button>
        </Paper>
      ) : (
        <Grid container spacing={3}>
          <Grid item xs={12} md={8}>
            <Paper sx={{ p: 2 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                <Typography variant="h6">
                  Prodotti ({cart.totalItems})
                </Typography>
                <Button
                  color="error"
                  onClick={handleClearCart}
                  disabled={clearCartMutation.isLoading}
                >
                  Svuota carrello
                </Button>
              </Box>

              {cart.items.map((item) => (
                <Card key={item.id} sx={{ mb: 2 }}>
                  <CardContent>
                    <Grid container spacing={2} alignItems="center">
                      <Grid item xs={12} sm={2}>
                        <Box
                          component="img"
                          src={item.productImageUrl || '/placeholder-image.jpg'}
                          alt={item.productName}
                          sx={{
                            width: '100%',
                            height: 80,
                            objectFit: 'cover',
                            borderRadius: 1,
                          }}
                        />
                      </Grid>

                      <Grid item xs={12} sm={4}>
                        <Typography variant="h6" gutterBottom>
                          {item.productName}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          SKU: {item.productSku}
                        </Typography>
                        {!item.isAvailable && (
                          <Alert severity="warning" sx={{ mt: 1 }}>
                            Prodotto non disponibile
                          </Alert>
                        )}
                      </Grid>

                      <Grid item xs={12} sm={3}>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                          <IconButton
                            size="small"
                            onClick={() => handleQuantityChange(item.id, item.quantity, -1)}
                            disabled={item.quantity <= 1 || updateQuantityMutation.isLoading}
                          >
                            <Remove />
                          </IconButton>
                          <TextField
                            value={item.quantity}
                            size="small"
                            sx={{ width: 60 }}
                            inputProps={{
                              min: 1,
                              max: item.stockQuantity,
                              type: 'number',
                              style: { textAlign: 'center' },
                            }}
                            onChange={(e) => {
                              const value = parseInt(e.target.value) || 1;
                              if (value >= 1 && value <= item.stockQuantity) {
                                updateQuantityMutation.mutate({
                                  itemId: item.id,
                                  quantity: value,
                                });
                              }
                            }}
                          />
                          <IconButton
                            size="small"
                            onClick={() => handleQuantityChange(item.id, item.quantity, 1)}
                            disabled={
                              item.quantity >= item.stockQuantity || 
                              updateQuantityMutation.isLoading
                            }
                          >
                            <Add />
                          </IconButton>
                        </Box>
                        <Typography variant="caption" color="text.secondary">
                          Disponibili: {item.stockQuantity}
                        </Typography>
                      </Grid>

                      <Grid item xs={12} sm={2}>
                        <Typography variant="h6">
                          €{item.subtotal}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          €{item.unitPrice} cad.
                        </Typography>
                      </Grid>

                      <Grid item xs={12} sm={1}>
                        <IconButton
                          color="error"
                          onClick={() => handleRemoveItem(item.id)}
                          disabled={removeItemMutation.isLoading}
                        >
                          <Delete />
                        </IconButton>
                      </Grid>
                    </Grid>
                  </CardContent>
                </Card>
              ))}
            </Paper>
          </Grid>

          <Grid item xs={12} md={4}>
            <Paper sx={{ p: 3, position: 'sticky', top: 20 }}>
              <Typography variant="h6" gutterBottom>
                Riepilogo ordine
              </Typography>

              <Box sx={{ my: 2 }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                  <Typography>Subtotale ({cart.totalItems} prodotti)</Typography>
                  <Typography>€{cart.totalPrice}</Typography>
                </Box>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                  <Typography>Spedizione</Typography>
                  <Typography color="success.main">Gratuita</Typography>
                </Box>
              </Box>

              <Divider sx={{ my: 2 }} />

              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
                <Typography variant="h6">Totale</Typography>
                <Typography variant="h6">€{cart.totalPrice}</Typography>
              </Box>

              <Button
                fullWidth
                variant="contained"
                size="large"
                startIcon={<ShoppingCartCheckout />}
                onClick={handleCheckout}
                disabled={isEmpty}
              >
                Procedi al checkout
              </Button>

              <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
                Accettiamo tutti i principali metodi di pagamento
              </Typography>
            </Paper>
          </Grid>
        </Grid>
      )}
    </Container>
  );
};

export default Cart;