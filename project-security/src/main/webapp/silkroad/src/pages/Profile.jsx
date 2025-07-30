// src/pages/Profile.jsx
import React, { useState } from 'react';
import {
  Container,
  Paper,
  Typography,
  Box,
  Grid,
  TextField,
  Button,
  Alert,
  Tabs,
  Tab,
  Divider,
} from '@mui/material';
import { useForm } from 'react-hook-form';
import { useSelector } from 'react-redux';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { authService } from '../services/authService';
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
      {value === index && <Box sx={{ pt: 3 }}>{children}</Box>}
    </div>
  );
};

const Profile = () => {
  const [tabValue, setTabValue] = useState(0);
  const { user } = useSelector((state) => state.auth);
  const queryClient = useQueryClient();

  // Query per ottenere il profilo aggiornato
  const { data: profile, isLoading, error } = useQuery({
    queryKey: ['userProfile'],
    queryFn: authService.getProfile,
    initialData: user,
  });

  // Form per aggiornamento profilo
  const {
    register: registerProfile,
    handleSubmit: handleSubmitProfile,
    formState: { errors: profileErrors },
    reset: resetProfile,
  } = useForm({
    defaultValues: profile || {},
  });

  // Form per cambio password
  const {
    register: registerPassword,
    handleSubmit: handleSubmitPassword,
    formState: { errors: passwordErrors },
    reset: resetPassword,
    watch,
  } = useForm();

  const newPassword = watch('newPassword');

  // Mutation per aggiornamento profilo
  const updateProfileMutation = useMutation({
    mutationFn: authService.updateProfile,
    onSuccess: (data) => {
      toast.success('Profilo aggiornato con successo!');
      queryClient.setQueryData(['userProfile'], data);
      resetProfile(data);
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Errore nell\'aggiornamento del profilo');
    },
  });

  // Mutation per cambio password
  const changePasswordMutation = useMutation({
    mutationFn: authService.changePassword,
    onSuccess: () => {
      toast.success('Password cambiata con successo!');
      resetPassword();
    },
    onError: (error) => {
      toast.error(error.response?.data?.message || 'Errore nel cambio password');
    },
  });

  React.useEffect(() => {
    if (profile) {
      resetProfile(profile);
    }
  }, [profile, resetProfile]);

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const onSubmitProfile = (data) => {
    updateProfileMutation.mutate(data);
  };

  const onSubmitPassword = (data) => {
    const { confirmPassword, ...passwordData } = data;
    changePasswordMutation.mutate(passwordData);
  };

  if (isLoading) return <LoadingSpinner />;
  if (error) return <Typography color="error">Errore nel caricamento del profilo</Typography>;

  return (
    <Container maxWidth="md">
      <Typography variant="h4" component="h1" gutterBottom>
        Il mio Profilo
      </Typography>

      <Paper sx={{ p: 3 }}>
        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <Tabs value={tabValue} onChange={handleTabChange}>
            <Tab label="Informazioni Personali" />
            <Tab label="Cambia Password" />
          </Tabs>
        </Box>

        {/* Tab Informazioni Personali */}
        <TabPanel value={tabValue} index={0}>
          <Box component="form" onSubmit={handleSubmitProfile(onSubmitProfile)}>
            <Grid container spacing={3}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Nome"
                  {...registerProfile('firstName', {
                    required: 'Il nome è richiesto',
                    minLength: {
                      value: 2,
                      message: 'Il nome deve avere almeno 2 caratteri',
                    },
                  })}
                  error={!!profileErrors.firstName}
                  helperText={profileErrors.firstName?.message}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Cognome"
                  {...registerProfile('lastName', {
                    required: 'Il cognome è richiesto',
                    minLength: {
                      value: 2,
                      message: 'Il cognome deve avere almeno 2 caratteri',
                    },
                  })}
                  error={!!profileErrors.lastName}
                  helperText={profileErrors.lastName?.message}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Email"
                  type="email"
                  {...registerProfile('email', {
                    required: 'Email è richiesta',
                    pattern: {
                      value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                      message: 'Email non valida',
                    },
                  })}
                  error={!!profileErrors.email}
                  helperText={profileErrors.email?.message}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Indirizzo"
                  multiline
                  rows={2}
                  {...registerProfile('address', {
                    required: 'L\'indirizzo è richiesto',
                    maxLength: {
                      value: 200,
                      message: 'L\'indirizzo non può superare i 200 caratteri',
                    },
                  })}
                  error={!!profileErrors.address}
                  helperText={profileErrors.address?.message}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Telefono"
                  {...registerProfile('telephone', {
                    required: 'Il telefono è richiesto',
                    pattern: {
                      value: /^[+]?[0-9]{10,15}$/,
                      message: 'Formato telefono non valido',
                    },
                  })}
                  error={!!profileErrors.telephone}
                  helperText={profileErrors.telephone?.message}
                />
              </Grid>

              <Grid item xs={12}>
                <Button
                  type="submit"
                  variant="contained"
                  size="large"
                  disabled={updateProfileMutation.isLoading}
                >
                  {updateProfileMutation.isLoading ? 'Aggiornamento...' : 'Aggiorna Profilo'}
                </Button>
              </Grid>
            </Grid>
          </Box>
        </TabPanel>

        {/* Tab Cambia Password */}
        <TabPanel value={tabValue} index={1}>
          <Box component="form" onSubmit={handleSubmitPassword(onSubmitPassword)}>
            <Grid container spacing={3}>
              <Grid item xs={12}>
                <Alert severity="info" sx={{ mb: 2 }}>
                  Per motivi di sicurezza, inserisci la tua password attuale e la nuova password.
                </Alert>
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Password Attuale"
                  type="password"
                  {...registerPassword('currentPassword', {
                    required: 'La password attuale è richiesta',
                  })}
                  error={!!passwordErrors.currentPassword}
                  helperText={passwordErrors.currentPassword?.message}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Nuova Password"
                  type="password"
                  {...registerPassword('newPassword', {
                    required: 'La nuova password è richiesta',
                    minLength: {
                      value: 8,
                      message: 'La password deve avere almeno 8 caratteri',
                    },
                  })}
                  error={!!passwordErrors.newPassword}
                  helperText={passwordErrors.newPassword?.message}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Conferma Nuova Password"
                  type="password"
                  {...registerPassword('confirmPassword', {
                    required: 'Conferma la nuova password',
                    validate: (value) =>
                      value === newPassword || 'Le password non corrispondono',
                  })}
                  error={!!passwordErrors.confirmPassword}
                  helperText={passwordErrors.confirmPassword?.message}
                />
              </Grid>

              <Grid item xs={12}>
                <Button
                  type="submit"
                  variant="contained"
                  size="large"
                  disabled={changePasswordMutation.isLoading}
                >
                  {changePasswordMutation.isLoading ? 'Cambio in corso...' : 'Cambia Password'}
                </Button>
              </Grid>
            </Grid>
          </Box>
        </TabPanel>
      </Paper>
    </Container>
  );
};

export default Profile;