import React, { useState } from 'react';
import { 
  Container, 
  Paper, 
  TextField, 
  Button, 
  Typography, 
  Box,
  Divider,
  Alert 
} from '@mui/material';
import { useTranslation } from 'react-i18next';

const MainPage = () => {
  const { t } = useTranslation();
  const [originalUrl, setOriginalUrl] = useState('');
  const [customName, setCustomName] = useState('');
  const [shortUrl, setShortUrl] = useState('');
  const [retrieveUrl, setRetrieveUrl] = useState('');
  const [retrievedOriginalUrl, setRetrievedOriginalUrl] = useState('');
  const [error, setError] = useState('');

  // Mock function to generate short URL
  const handleGenerate = () => {
    setError('');
    if (!originalUrl) {
      setError(t('main.urlRequired'));
      return;
    }
    
    // Mock response
    const mockShortUrl = `http://snaplink.ly/${customName || Math.random().toString(36).substr(2, 6)}`;
    setShortUrl(mockShortUrl);
  };

  // Mock function to retrieve original URL
  const handleRetrieve = () => {
    setError('');
    if (!retrieveUrl) {
      setError(t('main.retrieveUrlRequired'));
      return;
    }

    // Mock response
    setRetrievedOriginalUrl('https://www.example.com/very/long/original/url');
  };

  return (
    <Container maxWidth="md">
      <Box sx={{ mt: 4, mb: 6 }}>
        <Typography variant="h4" gutterBottom>
          {t('main.title')}
        </Typography>

        {/* URL Shortening Section */}
        <Paper sx={{ p: 3, mb: 4 }}>
          <Typography variant="h6" gutterBottom>
            {t('main.createSection')}
          </Typography>
          
          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
          
          <TextField
            fullWidth
            label={t('main.originalUrl')}
            value={originalUrl}
            onChange={(e) => setOriginalUrl(e.target.value)}
            margin="normal"
          />
          <TextField
            fullWidth
            label={t('main.customName')}
            value={customName}
            onChange={(e) => setCustomName(e.target.value)}
            margin="normal"
            helperText={t('main.customNameHelper')}
          />
          <Button
            variant="contained"
            onClick={handleGenerate}
            sx={{ mt: 2 }}
          >
            {t('main.generate')}
          </Button>
          
          {shortUrl && (
            <Box sx={{ mt: 2 }}>
              <Typography variant="subtitle1">{t('main.result')}:</Typography>
              <Typography>{shortUrl}</Typography>
            </Box>
          )}
        </Paper>

        <Divider sx={{ my: 4 }} />

        {/* URL Retrieval Section */}
        <Paper sx={{ p: 3 }}>
          <Typography variant="h6" gutterBottom>
            {t('main.retrieveSection')}
          </Typography>
          
          <TextField
            fullWidth
            label={t('main.shortUrl')}
            value={retrieveUrl}
            onChange={(e) => setRetrieveUrl(e.target.value)}
            margin="normal"
          />
          <Button
            variant="contained"
            onClick={handleRetrieve}
            sx={{ mt: 2 }}
          >
            {t('main.retrieve')}
          </Button>
          
          {retrievedOriginalUrl && (
            <Box sx={{ mt: 2 }}>
              <Typography variant="subtitle1">{t('main.originalUrl')}:</Typography>
              <Typography>{retrievedOriginalUrl}</Typography>
            </Box>
          )}
        </Paper>
      </Box>
    </Container>
  );
};

export default MainPage; 