// src/pages/Orders.jsx
import React, { useState } from 'react';
import {
  Container,
  Typography,
  Box,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TablePagination,
  Chip,
  IconButton,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Alert,
  Collapse,
  Grid,
  Card,
  CardContent,
  List,
  ListItem,
  ListItemText,
  TextField,
} from '@mui/material';
import {
  ExpandMore,
  ExpandLess,
  Visibility,
  Cancel,
  LocalShipping,
  CheckCircle,
  Error,
  AccessTime,
} from '@mui/icons-material';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { orderService } from '../services/orderService';
import LoadingSpinner from '../components/UI/LoadingSpinner';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';

const statusColors = {
  PENDING: 'warning',
  PAYMENT_CONFIRMED: 'info',
  PROCESSING: 'primary',
  SHIPPED: 'secondary',
  DELIVERED: 'success',
  CANCELLED: 'error',
  REFUNDED: 'default',
  RETURNED: 'default',
};

const statusIcons = {
  PENDING: <AccessTime />,
  PAYMENT_CONFIRMED: <CheckCircle />,
  PROCESSING: <AccessTime />,
  SHIPPED: <LocalShipping />,
  DELIVERED: <CheckCircle />,
  CANCELLED: <Error />,
  REFUNDED: <Error />,
  RETURNED: <Error />,
};

const OrderRow = ({ order, onViewDetails, onCancel }) => {
  const [open, setOpen] = useState(false);

  return (
    <>
      <TableRow hover>
        <TableCell>
          <IconButton
            aria-label="expand row"
            size="small"
            onClick={() => setOpen(!open)}
          >
            {open ? <ExpandLess /> : <ExpandMore />}
          </IconButton>
        </TableCell>
        <TableCell>#{order.id}</TableCell>
        <TableCell>{new Date(order.orderDate).toLocaleDateString('it-IT')}</TableCell>
        <TableCell>
          <Chip
            label={order.orderStatusDescription}
            color={statusColors[order.orderStatus]}
            size="small"
            icon={statusIcons[order.orderStatus]}
          />
        </TableCell>
        <TableCell>€{order.totalPrice}</TableCell>
        <TableCell>{order.totalItems} articoli</TableCell>
        <TableCell>
          <IconButton
            size="small"
            onClick={() => onViewDetails(order)}
            color="primary"
          >
            <Visibility />
          </IconButton>
          {order.isCancellable && (
            <IconButton
              size="small"
              onClick={() => onCancel(order)}
              color="error"
            >
              <Cancel />
            </IconButton>
          )}
        </TableCell>
      </TableRow>
      <TableRow>
        <TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={7}>
          <Collapse in={open} timeout="auto" unmountOnExit>
            <Box sx={{ margin: 1 }}>
              <Typography variant="h6" gutterBottom component="div">
                Dettagli Ordine
              </Typography>
              <Grid container spacing={2}>
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle2">Indirizzo di spedizione:</Typography>
                  <Typography variant="body2">{order.shippingAddress}</Typography>
                  {order.trackingNumber && (
                    <>
                      <Typography variant="subtitle2" sx={{ mt: 1 }}>
                        Tracking:
                      </Typography>
                      <Typography variant="body2">{order.trackingNumber}</Typography>
                    </>
                  )}
                </Grid>
                <Grid item xs={12} md={6}>
                  <Typography variant="subtitle2">Articoli:</Typography>
                  <List dense>
                    {order.items?.map((item) => (
                      <ListItem key={item.id}>
                        <ListItemText
                          primary={`${item.productName} x${item.quantity}`}
                          secondary={`€${item.unitPrice} cad.`}
                        />
                      </ListItem>
                    ))}
                  </List>
                </Grid>
              </Grid>
            </Box>
          </Collapse>
        </TableCell>
      </TableRow>
    </>
  );
};

const Orders = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [cancelDialogOpen, setCancelDialogOpen] = useState(false);
  const [cancelReason, setCancelReason] = useState('');

  // Query per gli ordini
  const { data: ordersData, isLoading, error } = useQuery({
    queryKey: ['myOrders', page, rowsPerPage],
    queryFn: () => orderService.getMyOrders({ page, size: rowsPerPage }),
  });

  // Query per il totale speso
  const { data: totalData } = useQuery({
    queryKey: ['myOrdersTotal'],
    queryFn: () => orderService.getMyOrdersTotal(),
  });

  // Mutation per cancellare ordine
  const cancelOrderMutation = useMutation({
    mutationFn: ({ orderId, reason }) => orderService.cancelMyOrder(orderId, reason),
    onSuccess: () => {
      toast.success('Ordine cancellato con successo');
      queryClient.invalidateQueries(['myOrders']);
      setCancelDialogOpen(false);
      setCancelReason('');
      setSelectedOrder(null);
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Errore nella cancellazione dell\'ordine');
    },
  });

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleViewDetails = (order) => {
    navigate(`/orders/${order.id}`);
  };

  const handleCancelOrder = (order) => {
    setSelectedOrder(order);
    setCancelDialogOpen(true);
  };

  const confirmCancelOrder = () => {
    if (selectedOrder) {
      cancelOrderMutation.mutate({
        orderId: selectedOrder.id,
        reason: cancelReason || 'Cancellato dall\'utente',
      });
    }
  };

  if (isLoading) return <LoadingSpinner />;
  if (error) return <Alert severity="error">Errore nel caricamento degli ordini</Alert>;

  return (
    <Container maxWidth="lg">
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          I miei Ordini
        </Typography>
        
        {totalData && (
          <Card sx={{ maxWidth: 300, mb: 3 }}>
            <CardContent>
              <Typography variant="subtitle2" color="text.secondary">
                Totale speso
              </Typography>
              <Typography variant="h5" color="primary">
                €{totalData.total}
              </Typography>
            </CardContent>
          </Card>
        )}
      </Box>

      {ordersData?.content?.length === 0 ? (
        <Paper sx={{ p: 4, textAlign: 'center' }}>
          <Typography variant="h6" gutterBottom>
            Non hai ancora effettuato ordini
          </Typography>
          <Button
            variant="contained"
            sx={{ mt: 2 }}
            onClick={() => navigate('/products')}
          >
            Inizia lo Shopping
          </Button>
        </Paper>
      ) : (
        <>
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell />
                  <TableCell>Ordine</TableCell>
                  <TableCell>Data</TableCell>
                  <TableCell>Stato</TableCell>
                  <TableCell>Totale</TableCell>
                  <TableCell>Articoli</TableCell>
                  <TableCell>Azioni</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {ordersData?.content?.map((order) => (
                  <OrderRow
                    key={order.id}
                    order={order}
                    onViewDetails={handleViewDetails}
                    onCancel={handleCancelOrder}
                  />
                ))}
              </TableBody>
            </Table>
          </TableContainer>

          <TablePagination
            component="div"
            count={ordersData?.totalElements || 0}
            page={page}
            onPageChange={handleChangePage}
            rowsPerPage={rowsPerPage}
            onRowsPerPageChange={handleChangeRowsPerPage}
            labelRowsPerPage="Ordini per pagina:"
          />
        </>
      )}

      {/* Dialog cancellazione */}
      <Dialog
        open={cancelDialogOpen}
        onClose={() => setCancelDialogOpen(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Cancella Ordine</DialogTitle>
        <DialogContent>
          <Alert severity="warning" sx={{ mb: 2 }}>
            Sei sicuro di voler cancellare l'ordine #{selectedOrder?.id}?
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
            {cancelOrderMutation.isLoading ? 'Cancellazione...' : 'Conferma Cancellazione'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default Orders;