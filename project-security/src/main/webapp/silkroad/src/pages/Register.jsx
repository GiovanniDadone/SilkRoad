import React, { useEffect } from 'react';
import {
  Container,
  Paper,
  TextField,
  Button,
  Typography,
  Box,
  Link,
  Alert,
  Grid,
} from '@mui/material';
import { useForm } from 'react-hook-form';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { authService } from '../services/authService';
import { toast } from 'react-toastify';

const Register = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = React.useState(false);
  const [error, setError] = React.useState('');

  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
  } = useForm();

  const password = watch('password');

  const onSubmit = async (data) => {
    setLoading(true);
    setError('');
    
    try {
      // Rimuovi confirmPassword prima di inviare
      const { confirmPassword, ...registrationData } = data;
      
      await authService.register(registrationData);
      toast.success('Registrazione completata! Effettua il login.');
      navigate('/login');
    } catch (error) {
      setError(error.response?.data?.message || 'Errore durante la registrazione');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container component="main" maxWidth="md">
      <Box
        sx={{
          marginTop: 8,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
        }}
      >
        <Paper elevation={3} sx={{ padding: 4, width: '100%' }}>
          <Typography component="h1" variant="h4" align="center" gutterBottom>
            Registrazione
          </Typography>

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <Box component="form" onSubmit={handleSubmit(onSubmit)} sx={{ mt: 1 }}>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Nome"
                  {...register('firstName', {
                    required: 'Il nome è richiesto',
                    minLength: {
                      value: 2,
                      message: 'Il nome deve avere almeno 2 caratteri',
                    },
                  })}
                  error={!!errors.firstName}
                  helperText={errors.firstName?.message}
                />
              </Grid>

              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Cognome"
                  {...register('lastName', {
                    required: 'Il cognome è richiesto',
                    minLength: {
                      value: 2,
                      message: 'Il cognome deve avere almeno 2 caratteri',
                    },
                  })}
                  error={!!errors.lastName}
                  helperText={errors.lastName?.message}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Email"
                  type="email"
                  {...register('email', {
                    required: 'Email è richiesta',
                    pattern: {
                      value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                      message: 'Email non valida',
                    },
                  })}
                  error={!!errors.email}
                  helperText={errors.email?.message}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Password"
                  type="password"
                  {...register('password', {
                    required: 'La password è richiesta',
                    minLength: {
                      value: 8,
                      message: 'La password deve avere almeno 8 caratteri',
                    },
                  })}
                  error={!!errors.password}
                  helperText={errors.password?.message}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Conferma Password"
                  type="password"
                  {...register('confirmPassword', {
                    required: 'Conferma la password',
                    validate: (value) =>
                      value === password || 'Le password non corrispondono',
                  })}
                  error={!!errors.confirmPassword}
                  helperText={errors.confirmPassword?.message}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Indirizzo"
                  {...register('address', {
                    required: 'L\'indirizzo è richiesto',
                    maxLength: {
                      value: 200,
                      message: 'L\'indirizzo non può superare i 200 caratteri',
                    },
                  })}
                  error={!!errors.address}
                  helperText={errors.address?.message}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Telefono"
                  {...register('telephone', {
                    required: 'Il telefono è richiesto',
                    pattern: {
                      value: /^[+]?[0-9]{10,15}$/,
                      message: 'Formato telefono non valido',
                    },
                  })}
                  error={!!errors.telephone}
                  helperText={errors.telephone?.message}
                />
              </Grid>
            </Grid>

            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
              disabled={loading}
            >
              {loading ? 'Registrazione in corso...' : 'Registrati'}
            </Button>

            <Box textAlign="center">
              <Link component={RouterLink} to="/login" variant="body2">
                Hai già un account? Accedi
              </Link>
            </Box>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
};

export default Register;