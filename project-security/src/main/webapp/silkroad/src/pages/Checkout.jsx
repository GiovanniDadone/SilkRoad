// src/pages/Checkout.jsx
import React, { useState } from 'react';
import {
  Container,
  Paper,
  Typography,
  Box,
  Stepper,
  Step,
  StepLabel,
  Button,
  Grid,
  TextField,
  FormControl,
  FormLabel,
  RadioGroup,
  FormControlLabel,
  Radio,
  Card,
  CardContent,
  Divider,
  Alert,
  List,
  ListItem,
  ListItemText,
  ListItemAvatar,
  Avatar,
} from '@mui/material';
import {
  ShoppingCart,
  LocalShipping,
  Payment,
  CheckCircle,
  ArrowBack,
  ArrowForward,
} from '@mui/icons-material';
import { useForm, Controller } from 'react-hook-form';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { useQuery, useMutation } from '@tanstack/react-query';
import { cartService } from '../services/cartService';
import { orderService } from '../services/orderService';
import LoadingSpinner from '../components/UI/LoadingSpinner';
import { toast } from 'react-toastify';

const steps = ['Riepilogo Carrello', 'Informazioni Spedizione', 'Pagamento', 'Conferma'];

const Checkout = () => {
  const navigate = useNavigate();
  const { user } = useSelector((state) => state.auth);
  const [activeStep, setActiveStep] = useState(0);
  const [orderData, setOrderData] = useState({
    shippingAddress: user?.address || '',
    billingAddress: '',
    paymentMethod: 'CREDIT_CARD',
    notes: '',
  });

  const {
    control,
    handleSubmit,
    formState: { errors },
    watch,
    setValue,
  } = useForm({
    defaultValues: orderData,
  });

  const sameAsShipping = watch('sameAsShipping');

  // Query per il carrello
  const { data: cart, isLoading: cartLoading } = useQuery({
    queryKey: ['cart'],
    queryFn: () => cartService.getCart(),
  });

  // Mutation per creare ordine
  const createOrderMutation = useMutation({
    mutationFn: (orderData) => orderService.createOrder(orderData),
    onSuccess: (data) => {
      toast.success('Ordine creato con successo!');
      navigate(`/orders/${data.id}`);
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Errore nella creazione dell\'ordine');
    },
  });

  const handleNext = () => {
    if (activeStep === steps.length - 1) {
      // Conferma ordine
      handleSubmit(onSubmitOrder)();
    } else {
      setActiveStep((prevStep) => prevStep + 1);
    }
  };

  const handleBack = () => {
    setActiveStep((prevStep) => prevStep - 1);
  };

  const onSubmitOrder = (data) => {
    const finalOrderData = {
      shippingAddress: data.shippingAddress,
      billingAddress: data.sameAsShipping ? data.shippingAddress : data.billingAddress,
      paymentMethod: data.paymentMethod,
      notes: data.notes,
    };
    
    setOrderData(finalOrderData);
    createOrderMutation.mutate(finalOrderData);
  };

  if (cartLoading) return <LoadingSpinner />;
  if (!cart || cart.items?.length === 0) {
    return (
      <Container maxWidth="sm">
        <Paper sx={{ p: 4, textAlign: 'center' }}>
          <Typography variant="h6" gutterBottom>
            Il tuo carrello è vuoto
          </Typography>
          <Button
            variant="contained"
            onClick={() => navigate('/products')}
            sx={{ mt: 2 }}
          >
            Continua lo Shopping
          </Button>
        </Paper>
      </Container>
    );
  }

  const getStepContent = (step) => {
    switch (step) {
      case 0:
        return (
          <Box>
            <Typography variant="h6" gutterBottom>
              Riepilogo Ordine
            </Typography>
            <List>
              {cart.items.map((item) => (
                <ListItem key={item.id} divider>
                  <ListItemAvatar>
                    <Avatar
                      src={item.productImageUrl}
                      alt={item.productName}
                      variant="rounded"
                    />
                  </ListItemAvatar>
                  <ListItemText
                    primary={item.productName}
                    secondary={`Quantità: ${item.quantity} × €${item.unitPrice}`}
                  />
                  <Typography variant="body1">
                    €{item.subtotal}
                  </Typography>
                </ListItem>
              ))}
            </List>
            <Divider sx={{ my: 2 }} />
            <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
              <Typography variant="h6">Totale:</Typography>
              <Typography variant="h6" color="primary">
                €{cart.totalPrice}
              </Typography>
            </Box>
          </Box>
        );

      case 1:
        return (
          <Box>
            <Typography variant="h6" gutterBottom>
              Informazioni di Spedizione
            </Typography>
            <Grid container spacing={3}>
              <Grid item xs={12}>
                <Controller
                  name="shippingAddress"
                  control={control}
                  rules={{ required: 'Indirizzo di spedizione è obbligatorio' }}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      fullWidth
                      label="Indirizzo di Spedizione"
                      multiline
                      rows={3}
                      error={!!errors.shippingAddress}
                      helperText={errors.shippingAddress?.message}
                    />
                  )}
                />
              </Grid>
              <Grid item xs={12}>
                <Controller
                  name="sameAsShipping"
                  control={control}
                  render={({ field }) => (
                    <FormControlLabel
                      control={<Radio {...field} />}
                      label="Usa lo stesso indirizzo per la fatturazione"
                    />
                  )}
                />
              </Grid>
              {!sameAsShipping && (
                <Grid item xs={12}>
                  <Controller
                    name="billingAddress"
                    control={control}
                    rules={{ 
                      required: sameAsShipping ? false : 'Indirizzo di fatturazione è obbligatorio' 
                    }}
                    render={({ field }) => (
                      <TextField
                        {...field}
                        fullWidth
                        label="Indirizzo di Fatturazione"
                        multiline
                        rows={3}
                        error={!!errors.billingAddress}
                        helperText={errors.billingAddress?.message}
                      />
                    )}
                  />
                </Grid>
              )}
              <Grid item xs={12}>
                <Controller
                  name="notes"
                  control={control}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      fullWidth
                      label="Note per l'ordine (opzionale)"
                      multiline
                      rows={2}
                    />
                  )}
                />
              </Grid>
            </Grid>
          </Box>
        );

      case 2:
        return (
          <Box>
            <Typography variant="h6" gutterBottom>
              Metodo di Pagamento
            </Typography>
            <Controller
              name="paymentMethod"
              control={control}
              rules={{ required: 'Seleziona un metodo di pagamento' }}
              render={({ field }) => (
                <FormControl component="fieldset" error={!!errors.paymentMethod}>
                  <RadioGroup {...field}>
                    <FormControlLabel
                      value="CREDIT_CARD"
                      control={<Radio />}
                      label="Carta di Credito"
                    />
                    <FormControlLabel
                      value="DEBIT_CARD"
                      control={<Radio />}
                      label="Carta di Debito"
                    />
                    <FormControlLabel
                      value="PAYPAL"
                      control={<Radio />}
                      label="PayPal"
                    />
                    <FormControlLabel
                      value="BANK_TRANSFER"
                      control={<Radio />}
                      label="Bonifico Bancario"
                    />
                  </RadioGroup>
                </FormControl>
              )}
            />
            <Alert severity="info" sx={{ mt: 2 }}>
              Il pagamento verrà processato dopo la conferma dell'ordine
            </Alert>
          </Box>
        );

      case 3:
        return (
          <Box>
            <Alert severity="success" icon={<CheckCircle />} sx={{ mb: 3 }}>
              Controlla i dettagli del tuo ordine prima di confermare
            </Alert>
            
            <Card sx={{ mb: 2 }}>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Riepilogo Ordine
                </Typography>
                <Typography variant="body2">
                  Totale articoli: {cart.totalItems}
                </Typography>
                <Typography variant="h6" color="primary">
                  Totale: €{cart.totalPrice}
                </Typography>
              </CardContent>
            </Card>

            <Card sx={{ mb: 2 }}>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Indirizzo di Spedizione
                </Typography>
                <Typography variant="body2">
                  {watch('shippingAddress')}
                </Typography>
              </CardContent>
            </Card>

            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Metodo di Pagamento
                </Typography>
                <Typography variant="body2">
                  {watch('paymentMethod').replace('_', ' ')}
                </Typography>
              </CardContent>
            </Card>
          </Box>
        );

      default:
        return 'Passaggio sconosciuto';
    }
  };

  return (
    <Container maxWidth="md">
      <Button
        startIcon={<ArrowBack />}
        onClick={() => navigate('/cart')}
        sx={{ mb: 3 }}
      >
        Torna al carrello
      </Button>

      <Paper sx={{ p: 3 }}>
        <Typography variant="h4" component="h1" gutterBottom align="center">
          Checkout
        </Typography>

        <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
          {steps.map((label) => (
            <Step key={label}>
              <StepLabel>{label}</StepLabel>
            </Step>
          ))}
        </Stepper>

        <Box sx={{ minHeight: 400 }}>
          {getStepContent(activeStep)}
        </Box>

        <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 4 }}>
          <Button
            disabled={activeStep === 0}
            onClick={handleBack}
            sx={{ mr: 1 }}
          >
            Indietro
          </Button>
          <Button
            variant="contained"
            onClick={handleNext}
            endIcon={activeStep === steps.length - 1 ? <CheckCircle /> : <ArrowForward />}
            disabled={createOrderMutation.isLoading}
          >
            {activeStep === steps.length - 1 
              ? (createOrderMutation.isLoading ? 'Conferma in corso...' : 'Conferma Ordine')
              : 'Avanti'
            }
          </Button>
        </Box>
      </Paper>
    </Container>
  );
};

export default Checkout;