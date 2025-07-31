// src/pages/Profile.jsx
import React, { useState } from 'react';
import {
  Container,
  Paper,
  Typography,
  Box,
  TextField,
  Button,
  Grid,
  Tab,
  Tabs,
  Alert,
  Divider,
  Card,
  CardContent,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Avatar,
} from '@mui/material';
import {
  Person,
  Email,
  Phone,
  Home,
  Lock,
  ShoppingBag,
  Edit,
  Save,
  Cancel,
} from '@mui/icons-material';
import { useForm } from 'react-hook-form';
import { useSelector, useDispatch } from 'react-redux';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { authService } from '../services/authService';
import { orderService } from '../services/orderService';
import { fetchUserProfile } from '../store/authSlice';
import LoadingSpinner from '../components/UI/LoadingSpinner';
import { toast } from 'react-toastify';

const TabPanel = ({ children, value, index, ...other }) => {
  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`profile-tabpanel-${index}`}
      aria-labelledby={`profile-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );
};

const Profile = () => {
  const dispatch = useDispatch();
  const queryClient = useQueryClient();
  const { user } = useSelector((state) => state.auth);
  const [tabValue, setTabValue] = useState(0);
  const [isEditing, setIsEditing] = useState(false);
  const [isChangingPassword, setIsChangingPassword] = useState(false);

  // Form per profilo
  const {
    register: registerProfile,
    handleSubmit: handleSubmitProfile,
    formState: { errors: profileErrors },
    reset: resetProfile,
  } = useForm({
    defaultValues: {
      firstName: user?.firstName || '',
      lastName: user?.lastName || '',
      email: user?.email || '',
      address: user?.address || '',
      telephone: user?.telephone || '',
    },
  });

  // Form per cambio password
  const {
    register: registerPassword,
    handleSubmit: handleSubmitPassword,
    formState: { errors: passwordErrors },
    reset: resetPassword,
    watch,
  } = useForm();

  // Query per ordini recenti
  const { data: recentOrders } = useQuery({
    queryKey: ['recentOrders'],
    queryFn: () => orderService.getMyOrders({ page: 0, size: 5 }),
  });

  // Mutation per aggiornare profilo
  const updateProfileMutation = useMutation({
    mutationFn: (userData) => authService.updateProfile(userData),
    onSuccess: () => {
      toast.success('Profilo aggiornato con successo!');
      dispatch(fetchUserProfile());
      setIsEditing(false);
      queryClient.invalidateQueries(['profile']);
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Errore nell\'aggiornamento del profilo');
    },
  });

  // Mutation per cambiare password
  const changePasswordMutation = useMutation({
    mutationFn: (passwordData) => authService.changePassword(passwordData),
    onSuccess: () => {
      toast.success('Password cambiata con successo!');
      setIsChangingPassword(false);
      resetPassword();
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Errore nel cambio password');
    },
  });

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const onSubmitProfile = (data) => {
    updateProfileMutation.mutate(data);
  };

  const onSubmitPassword = (data) => {
    changePasswordMutation.mutate({
      currentPassword: data.currentPassword,
      newPassword: data.newPassword,
    });
  };

  const handleCancelEdit = () => {
    setIsEditing(false);
    resetProfile({
      firstName: user?.firstName || '',
      lastName: user?.lastName || '',
      email: user?.email || '',
      address: user?.address || '',
      telephone: user?.telephone || '',
    });
  };

  if (!user) {
    return <LoadingSpinner />;
  }

  return (
    <Container maxWidth="lg">
      <Typography variant="h4" component="h1" gutterBottom sx={{ mb: 4 }}>
        Il mio Profilo
      </Typography>

      <Paper sx={{ width: '100%' }}>
        <Tabs
          value={tabValue}
          onChange={handleTabChange}
          indicatorColor="primary"
          textColor="primary"
          variant="fullWidth"
        >
          <Tab label="Informazioni Personali" icon={<Person />} />
          <Tab label="Ordini Recenti" icon={<ShoppingBag />} />
          <Tab label="Sicurezza" icon={<Lock />} />
        </Tabs>

        {/* Tab Informazioni Personali */}
        <TabPanel value={tabValue} index={0}>
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
            <Avatar
              sx={{
                width: 100,
                height: 100,
                bgcolor: 'primary.main',
                fontSize: 40,
                mr: 3,
              }}
            >
              {user.firstName?.[0]?.toUpperCase()}
              {user.lastName?.[0]?.toUpperCase()}
            </Avatar>
            <Box>
              <Typography variant="h5">
                {user.firstName} {user.lastName}
              </Typography>
              <Typography variant="body1" color="text.secondary">
                {user.email}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Membro dal {new Date().toLocaleDateString('it-IT')}
              </Typography>
            </Box>
          </Box>

          <Divider sx={{ mb: 3 }} />

          <Box component="form" onSubmit={handleSubmitProfile(onSubmitProfile)}>
            <Grid container spacing={3}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Nome"
                  {...registerProfile('firstName', {
                    required: 'Nome è richiesto',
                  })}
                  error={!!profileErrors.firstName}
                  helperText={profileErrors.firstName?.message}
                  disabled={!isEditing}
                  InputProps={{
                    startAdornment: <Person sx={{ mr: 1, color: 'action.active' }} />,
                  }}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Cognome"
                  {...registerProfile('lastName', {
                    required: 'Cognome è richiesto',
                  })}
                  error={!!profileErrors.lastName}
                  helperText={profileErrors.lastName?.message}
                  disabled={!isEditing}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Email"
                  {...registerProfile('email', {
                    required: 'Email è richiesta',
                    pattern: {
                      value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                      message: 'Email non valida',
                    },
                  })}
                  error={!!profileErrors.email}
                  helperText={profileErrors.email?.message}
                  disabled={!isEditing}
                  InputProps={{
                    startAdornment: <Email sx={{ mr: 1, color: 'action.active' }} />,
                  }}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Indirizzo"
                  {...registerProfile('address', {
                    required: 'Indirizzo è richiesto',
                  })}
                  error={!!profileErrors.address}
                  helperText={profileErrors.address?.message}
                  disabled={!isEditing}
                  multiline
                  rows={2}
                  InputProps={{
                    startAdornment: <Home sx={{ mr: 1, color: 'action.active' }} />,
                  }}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Telefono"
                  {...registerProfile('telephone', {
                    required: 'Telefono è richiesto',
                    pattern: {
                      value: /^[+]?[0-9]{10,15}$/,
                      message: 'Formato telefono non valido',
                    },
                  })}
                  error={!!profileErrors.telephone}
                  helperText={profileErrors.telephone?.message}
                  disabled={!isEditing}
                  InputProps={{
                    startAdornment: <Phone sx={{ mr: 1, color: 'action.active' }} />,
                  }}
                />
              </Grid>
            </Grid>

            <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
              {isEditing ? (
                <>
                  <Button
                    variant="outlined"
                    startIcon={<Cancel />}
                    onClick={handleCancelEdit}
                  >
                    Annulla
                  </Button>
                  <Button
                    type="submit"
                    variant="contained"
                    startIcon={<Save />}
                    disabled={updateProfileMutation.isLoading}
                  >
                    {updateProfileMutation.isLoading ? 'Salvataggio...' : 'Salva'}
                  </Button>
                </>
              ) : (
                <Button
                  variant="contained"
                  startIcon={<Edit />}
                  onClick={() => setIsEditing(true)}
                >
                  Modifica
                </Button>
              )}
            </Box>
          </Box>
        </TabPanel>

        {/* Tab Ordini Recenti */}
        <TabPanel value={tabValue} index={1}>
          <Typography variant="h6" gutterBottom>
            Ordini Recenti
          </Typography>

          {recentOrders?.content?.length === 0 ? (
            <Alert severity="info">Non hai ancora effettuato ordini</Alert>
          ) : (
            <List>
              {recentOrders?.content?.map((order) => (
                <Card key={order.id} sx={{ mb: 2 }}>
                  <CardContent>
                    <Grid container spacing={2}>
                      <Grid item xs={12} sm={6}>
                        <Typography variant="subtitle1">
                          Ordine #{order.id}
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          {new Date(order.orderDate).toLocaleDateString('it-IT')}
                        </Typography>
                      </Grid>
                      <Grid item xs={12} sm={3}>
                        <Typography variant="body2" color="text.secondary">
                          Stato
                        </Typography>
                        <Typography variant="body1" color="primary">
                          {order.orderStatusDescription}
                        </Typography>
                      </Grid>
                      <Grid item xs={12} sm={3}>
                        <Typography variant="body2" color="text.secondary">
                          Totale
                        </Typography>
                        <Typography variant="h6">
                          €{order.totalPrice}
                        </Typography>
                      </Grid>
                    </Grid>
                  </CardContent>
                </Card>
              ))}
            </List>
          )}

          <Button
            fullWidth
            variant="outlined"
            sx={{ mt: 2 }}
            onClick={() => window.location.href = '/orders'}
          >
            Vedi Tutti gli Ordini
          </Button>
        </TabPanel>

        {/* Tab Sicurezza */}
        <TabPanel value={tabValue} index={2}>
          <Typography variant="h6" gutterBottom>
            Cambio Password
          </Typography>

          {!isChangingPassword ? (
            <Box>
              <Alert severity="info" sx={{ mb: 3 }}>
                Si consiglia di cambiare la password regolarmente per mantenere il tuo account sicuro.
              </Alert>
              <Button
                variant="contained"
                startIcon={<Lock />}
                onClick={() => setIsChangingPassword(true)}
              >
                Cambia Password
              </Button>
            </Box>
          ) : (
            <Box component="form" onSubmit={handleSubmitPassword(onSubmitPassword)}>
              <Grid container spacing={3}>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    type="password"
                    label="Password Attuale"
                    {...registerPassword('currentPassword', {
                      required: 'Password attuale è richiesta',
                    })}
                    error={!!passwordErrors.currentPassword}
                    helperText={passwordErrors.currentPassword?.message}
                  />
                </Grid>

                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    type="password"
                    label="Nuova Password"
                    {...registerPassword('newPassword', {
                      required: 'Nuova password è richiesta',
                      minLength: {
                        value: 8,
                        message: 'Password deve essere di almeno 8 caratteri',
                      },
                    })}
                    error={!!passwordErrors.newPassword}
                    helperText={passwordErrors.newPassword?.message}
                  />
                </Grid>

                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    type="password"
                    label="Conferma Nuova Password"
                    {...registerPassword('confirmPassword', {
                      required: 'Conferma password è richiesta',
                      validate: (value) =>
                        value === watch('newPassword') || 'Le password non corrispondono',
                    })}
                    error={!!passwordErrors.confirmPassword}
                    helperText={passwordErrors.confirmPassword?.message}
                  />
                </Grid>
              </Grid>

              <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
                <Button
                  variant="outlined"
                  onClick={() => {
                    setIsChangingPassword(false);
                    resetPassword();
                  }}
                >
                  Annulla
                </Button>
                <Button
                  type="submit"
                  variant="contained"
                  disabled={changePasswordMutation.isLoading}
                >
                  {changePasswordMutation.isLoading ? 'Cambio...' : 'Cambia Password'}
                </Button>
              </Box>
            </Box>
          )}
        </TabPanel>
      </Paper>
    </Container>
  );
};

export default Profile;