import React from 'react';
import { AppBar, Toolbar, Button, Box } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

const Navigation = ({ onLogout }) => {
  const navigate = useNavigate();
  const { t } = useTranslation();

  return (
    <AppBar position="static">
      <Toolbar>
        <Box sx={{ flexGrow: 1, display: 'flex', gap: 2 }}>
          <Button 
            color="inherit" 
            onClick={() => navigate('/main')}
          >
            {t('nav.shortlink')}
          </Button>
          <Button 
            color="inherit" 
            onClick={() => navigate('/my-links')}
          >
            {t('nav.myLinks')}
          </Button>
          <Button 
            color="inherit" 
            onClick={() => navigate('/about')}
          >
            {t('nav.about')}
          </Button>
        </Box>
        <Button 
          color="inherit" 
          onClick={onLogout}
        >
          {t('nav.logout')}
        </Button>
      </Toolbar>
    </AppBar>
  );
};

export default Navigation; 