// src/pages/OrderDetail.jsx
import React, { useState } from 'react';
import {
  Container,
  Paper,
  Typography,
  Box,
  Grid,
  Stepper,
  Step,
  StepLabel,
  Card,
  CardContent,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  Button,
  Divider,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
} from '@mui/material';
import {
  ArrowBack,
  LocalShipping,
  CheckCircle,
  AccessTime,
  Cancel as CancelIcon,
  Print,
  ContentCopy,
} from '@mui/icons-material';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { orderService } from '../services/orderService';
import LoadingSpinner from '../components/UI/LoadingSpinner';
import { toast } from 'react-toastify';

const orderSteps = [
  { label: 'Ordine Ricevuto', status: 'PENDING' },
  { label: 'Pagamento Confermato', status: 'PAYMENT_CONFIRMED' },
  { label: 'In Lavorazione', status: 'PROCESSING' },
  { label: 'Spedito', status: 'SHIPPED' },
  { label: 'Consegnato', status: 'DELIVERED' },
];

const getActiveStep = (status) => {
  const index = orderSteps.findIndex(step => step.status === status);
  return index >= 0 ? index : 0;
};

const OrderDetail = () => {
  const { orderId } = useParams();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [cancelDialogOpen, setCancelDialogOpen] = useState(false);
  const [cancelReason, setCancelReason] = useState('');

  // Query per dettagli ordine
  const { data: order, isLoading, error } = useQuery({
    queryKey: ['orderDetail', orderId],
    queryFn: () => orderService.getMyOrderById(orderId),
  });

  // Mutation per cancellare ordine
  const cancelOrderMutation = useMutation({
    mutationFn: (reason) => orderService.cancelMyOrder(orderId, reason),
    onSuccess: () => {
      toast.success('Ordine cancellato con successo');
      queryClient.invalidateQueries(['orderDetail', orderId]);
      queryClient.invalidateQueries(['myOrders']);
      setCancelDialogOpen(false);
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Errore nella cancellazione');
    },
  });

  const handleCopyTracking = () => {
    if (order?.trackingNumber) {
      navigator.clipboard.writeText(order.trackingNumber);
      toast.success('Numero di tracking copiato!');
    }
  };

  const handlePrintOrder = () => {
    window.print();
  };

  const confirmCancelOrder = () => {
    cancelOrderMutation.mutate(cancelReason || 'Cancellato dall\'utente');
  };

  if (isLoading) return <LoadingSpinner />;
  if (error) return <Alert severity="error">Errore nel caricamento dell'ordine</Alert>;
  if (!order) return <Alert severity="warning">Ordine non trovato</Alert>;

  const isCancelled = ['CANCELLED', 'REFUNDED', 'RETURNED'].includes(order.orderStatus);

  return (
    <Container maxWidth="lg">
      <Button
        startIcon={<ArrowBack />}
        onClick={() => navigate('/orders')}
        sx={{ mb: 3 }}
      >
        Torna agli ordini
      </Button>

      <Paper sx={{ p: 3, mb: 3 }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} md={6}>
            <Typography variant="h4" component="h1">
              Ordine #{order.id}
            </Typography>
            <Typography variant="body1" color="text.secondary">
              Effettuato il {new Date(order.orderDate).toLocaleDateString('it-IT')}
            </Typography>
          </Grid>
          <Grid item xs={12} md={6} sx={{ textAlign: { md: 'right' } }}>
            <Box sx={{ display: 'flex', gap: 1, justifyContent: { md: 'flex-end' } }}>
              <Button
                variant="outlined"
                startIcon={<Print />}
                onClick={handlePrintOrder}
              >
                Stampa
              </Button>
              {order.isCancellable && (
                <Button
                  variant="outlined"
                  color="error"
                  startIcon={<CancelIcon />}
                  onClick={() => setCancelDialogOpen(true)}
                >
                  Cancella Ordine
                </Button>
              )}
            </Box>
          </Grid>
        </Grid>
      </Paper>

      {/* Stato Ordine */}
      <Paper sx={{ p: 3, mb: 3 }}>
        <Typography variant="h6" gutterBottom>
          Stato Ordine
        </Typography>
        
        {isCancelled ? (
          <Alert severity="error" sx={{ mb: 2 }}>
            Ordine {order.orderStatusDescription}
          </Alert>
        ) : (
          <Stepper activeStep={getActiveStep(order.orderStatus)} sx={{ mb: 3 }}>
            {orderSteps.map((step) => (
              <Step key={step.status}>
                <StepLabel>{step.label}</StepLabel>
              </Step>
            ))}
          </Stepper>
        )}

        <Grid container spacing={2}>
          {order.trackingNumber && (
            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle2" color="text.secondary">
                Numero di Tracking
              </Typography>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Typography variant="body1">{order.trackingNumber}</Typography>
                <Button
                  size="small"
                  startIcon={<ContentCopy />}
                  onClick={handleCopyTracking}
                >
                  Copia
                </Button>
              </Box>
            </Grid>
          )}
          {order.estimatedDeliveryDate && (
            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle2" color="text.secondary">
                Consegna Prevista
              </Typography>
              <Typography variant="body1">
                {new Date(order.estimatedDeliveryDate).toLocaleDateString('it-IT')}
              </Typography>
            </Grid>
          )}
          {order.actualDeliveryDate && (
            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle2" color="text.secondary">
                Consegnato il
              </Typography>
              <Typography variant="body1">
                {new Date(order.actualDeliveryDate).toLocaleDateString('it-IT')}
              </Typography>
            </Grid>
          )}
        </Grid>
      </Paper>

      {/* Dettagli Ordine */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Articoli Ordinati
            </Typography>
            
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Prodotto</TableCell>
                    <TableCell align="center">Quantità</TableCell>
                    <TableCell align="right">Prezzo Unit.</TableCell>
                    <TableCell align="right">Subtotale</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {order.items?.map((item) => (
                    <TableRow key={item.id}>
                      <TableCell>
                        <Typography variant="body1">{item.productName}</Typography>
                        <Typography variant="caption" color="text.secondary">
                          SKU: {item.productSku}
                        </Typography>
                      </TableCell>
                      <TableCell align="center">{item.quantity}</TableCell>
                      <TableCell align="right">€{item.unitPrice}</TableCell>
                      <TableCell align="right">€{item.subtotal}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>

            <Box sx={{ mt: 3, textAlign: 'right' }}>
              <Typography variant="h6">
                Totale: €{order.totalPrice}
              </Typography>
            </Box>
          </Paper>
        </Grid>

        <Grid item xs={12} md={4}>
          {/* Informazioni Spedizione */}
          <Card sx={{ mb: 2 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                <LocalShipping sx={{ verticalAlign: 'middle', mr: 1 }} />
                Spedizione
              </Typography>
              <Divider sx={{ mb: 2 }} />
              <Typography variant="subtitle2" color="text.secondary">
                Indirizzo di spedizione
              </Typography>
              <Typography variant="body2" sx={{ mb: 2 }}>
                {order.shippingAddress}
              </Typography>
              
              {order.billingAddress && (
                <>
                  <Typography variant="subtitle2" color="text.secondary">
                    Indirizzo di fatturazione
                  </Typography>
                  <Typography variant="body2">
                    {order.billingAddress}
                  </Typography>
                </>
              )}
            </CardContent>
          </Card>

          {/* Informazioni Pagamento */}
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                <CheckCircle sx={{ verticalAlign: 'middle', mr: 1 }} />
                Pagamento
              </Typography>
              <Divider sx={{ mb: 2 }} />
              <Typography variant="subtitle2" color="text.secondary">
                Metodo di pagamento
              </Typography>
              <Typography variant="body2" sx={{ mb: 2 }}>
                {order.paymentMethod}
              </Typography>
              
              {order.paymentTransactionId && (
                <>
                  <Typography variant="subtitle2" color="text.secondary">
                    ID Transazione
                  </Typography>
                  <Typography variant="body2">
                    {order.paymentTransactionId}
                  </Typography>
                </>
              )}
            </CardContent>
          </Card>

          {/* Note */}
          {order.notes && (
            <Card sx={{ mt: 2 }}>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Note
                </Typography>
                <Divider sx={{ mb: 2 }} />
                <Typography variant="body2">
                  {order.notes}
                </Typography>
              </CardContent>
            </Card>
          )}
        </Grid>
      </Grid>

      {/* Dialog Cancellazione */}
      <Dialog
        open={cancelDialogOpen}
        onClose={() => setCancelDialogOpen(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Cancella Ordine</DialogTitle>
        <DialogContent>
          <Alert severity="warning" sx={{ mb: 2 }}>
            Sei sicuro di voler cancellare questo ordine?
            Questa azione non può essere annullata.
          </Alert>
          <TextField
            fullWidth
            label="Motivo cancellazione (opzionale)"
            multiline
            rows={3}
            value={cancelReason}
            onChange={(e) => setCancelReason(e.target.value)}
            sx={{ mt: 2 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCancelDialogOpen(false)}>
            Annulla
          </Button>
          <Button
            onClick={confirmCancelOrder}
            color="error"
            variant="contained"
            disabled={cancelOrderMutation.isLoading}
          >
            {cancelOrderMutation.isLoading ? 'Cancellazione...' : 'Conferma'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default OrderDetail;