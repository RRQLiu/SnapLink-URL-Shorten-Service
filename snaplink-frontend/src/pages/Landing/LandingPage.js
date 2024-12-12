import React from 'react';
import { Container, Typography, Button, Box } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

const LandingPage = () => {
  const navigate = useNavigate();
  const { t } = useTranslation();

  return (
    <Container maxWidth="md">
      <Box
        sx={{
          mt: 8,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          gap: 4
        }}
      >
        <Typography variant="h2" component="h1" align="center">
          {t('landing.title')}
        </Typography>
        <Typography variant="h5" align="center" color="text.secondary">
          {t('landing.subtitle')}
        </Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button
            variant="contained"
            size="large"
            onClick={() => navigate('/auth', { state: { mode: 'login' } })}
          >
            {t('landing.login')}
          </Button>
          <Button
            variant="outlined"
            size="large"
            onClick={() => navigate('/auth', { state: { mode: 'register' } })}
          >
            {t('landing.register')}
          </Button>
        </Box>
      </Box>
    </Container>
  );
};

export default LandingPage; 