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
import axios from 'axios';
import { API_BASE_URL } from '../../config/api.js';

const MainPage = () => {
  const { t } = useTranslation();
  const [originalUrl, setOriginalUrl] = useState('');
  const [customName, setCustomName] = useState('');
  const [shortUrl, setShortUrl] = useState('');
  const [retrieveUrl, setRetrieveUrl] = useState('');
  const [retrievedOriginalUrl, setRetrievedOriginalUrl] = useState('');
  const [error, setError] = useState('');
  const [bulkUrls, setBulkUrls] = useState('');
  const [bulkResults, setBulkResults] = useState(null);

  const getUserId = () => {
    console.log(localStorage.getItem('userID'));
    return localStorage.getItem('userID') || '';
  };

  const handleGenerate = async () => {
    setError('');
    const userId = getUserId();
    
    if (!userId) {
      setError(t('main.loginRequired'));
      return;
    }
    
    if (!originalUrl) {
      setError(t('main.urlRequired'));
      return;
    }
    
    try {
      const response = await axios.post(`${API_BASE_URL}/api/shorten`, {
        userId,
        longUrl: originalUrl
      });
      setShortUrl(response.data.shortUrl);
    } catch (err) {
      setError(err.response?.data?.message || t('main.generalError'));
    }
  };

  const handleRetrieve = async () => {
    setError('');
    if (!retrieveUrl) {
      setError(t('main.retrieveUrlRequired'));
      return;
    }

    try {
      const response = await axios.get(`${API_BASE_URL}/api/url/${retrieveUrl}`);
      setRetrievedOriginalUrl(response.headers.location);
    } catch (err) {
      setError(err.response?.data?.message || t('main.generalError'));
    }
  };

  const handleBulkGenerate = async () => {
    setError('');
    const userId = getUserId();
    
    if (!userId) {
      setError(t('main.loginRequired'));
      return;
    }
    
    if (!bulkUrls) {
      setError(t('main.bulkUrlsRequired'));
      return;
    }

    const urlList = bulkUrls.split('\n').filter(url => url.trim());
    if (urlList.length > 10) {
      setError(t('main.tooManyUrls'));
      return;
    }

    try {
      const response = await axios.post(`${API_BASE_URL}/api/bulk-shorten`, {
        userId,
        longUrls: urlList
      });
      setBulkResults(response.data);
    } catch (err) {
      setError(err.response?.data?.message || t('main.generalError'));
    }
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

        <Divider sx={{ my: 4 }} />

        {/* Add Bulk Operation Section */}
        <Paper sx={{ p: 3, mb: 4 }}>
          <Typography variant="h6" gutterBottom>
            {t('main.bulkSection')}
          </Typography>
          
          <TextField
            fullWidth
            multiline
            rows={4}
            label={t('main.bulkUrls')}
            value={bulkUrls}
            onChange={(e) => setBulkUrls(e.target.value)}
            margin="normal"
            helperText={t('main.bulkUrlsHelper')}
          />
          <Button
            variant="contained"
            onClick={handleBulkGenerate}
            sx={{ mt: 2 }}
          >
            {t('main.generateBulk')}
          </Button>
          
          {bulkResults && (
            <Box sx={{ mt: 2 }}>
              <Typography variant="subtitle1">{t('main.bulkResults')}:</Typography>
              {bulkResults.urlMappings.map((mapping, index) => (
                <Box key={index} sx={{ mt: 1 }}>
                  <Typography variant="body2" color={mapping.status === 'SUCCESS' ? 'success.main' : 'error.main'}>
                    {mapping.longUrl} → {mapping.shortUrl || mapping.error}
                  </Typography>
                </Box>
              ))}
            </Box>
          )}
        </Paper>
      </Box>
    </Container>
  );
};

export default MainPage; 