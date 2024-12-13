import React, { useState } from 'react';
import { 
  Container, 
  Paper, 
  TextField, 
  Button, 
  Typography, 
  Box,
  Alert 
} from '@mui/material';
import { useLocation, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { authService } from '../../services/authService';

const AuthPage = ({ onLogin }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const { t } = useTranslation();
  const [mode, setMode] = useState(location.state?.mode || 'login');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      if (mode === 'login') {
        const success = await onLogin(email, password);
        if (success) {
          navigate('/main');
        } else {
          setError(t('auth.invalidCredentials'));
        }
      } else {
        await authService.register(email, password);
        const success = await onLogin(email, password);
        if (success) {
          navigate('/main');
        }
      }
    } catch (err) {
      setError(t('auth.error'));
    }
  };

  return (
    <Container maxWidth="sm">
      <Paper sx={{ mt: 8, p: 4 }}>
        <Typography variant="h4" align="center" mb={4}>
          {mode === 'login' ? t('auth.login') : t('auth.register')}
        </Typography>
        
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        
        <form onSubmit={handleSubmit}>
          <TextField
            fullWidth
            label={t('auth.email')}
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            margin="normal"
            required
          />
          <TextField
            fullWidth
            label={t('auth.password')}
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            margin="normal"
            required
          />
          <Button
            fullWidth
            variant="contained"
            type="submit"
            sx={{ mt: 3 }}
          >
            {mode === 'login' ? t('auth.loginButton') : t('auth.registerButton')}
          </Button>
        </form>
        
        <Box sx={{ mt: 2, textAlign: 'center' }}>
          <Button
            onClick={() => setMode(mode === 'login' ? 'register' : 'login')}
          >
            {mode === 'login' ? t('auth.switchToRegister') : t('auth.switchToLogin')}
          </Button>
        </Box>
      </Paper>
    </Container>
  );
};

export default AuthPage; 