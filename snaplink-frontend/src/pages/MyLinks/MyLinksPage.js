import React, { useState, useEffect } from 'react';
import { 
  Container, 
  Paper, 
  Typography, 
  Table, 
  TableBody, 
  TableCell, 
  TableContainer, 
  TableHead, 
  TableRow,
  Button,
  IconButton,
  Box
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import AnalyticsIcon from '@mui/icons-material/Analytics';
import { useTranslation } from 'react-i18next';

const MyLinksPage = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [links, setLinks] = useState([]);

  useEffect(() => {
    // Mock data for testing
    setLinks([
      {
        id: 1,
        originalUrl: 'https://www.example.com/very/long/url/1',
        shortUrl: 'http://short.ly/abc123',
        createdAt: '2024-02-01',
        clicks: 150
      },
      {
        id: 2,
        originalUrl: 'https://www.example.com/another/long/url/2',
        shortUrl: 'http://short.ly/def456',
        createdAt: '2024-02-02',
        clicks: 75
      },
      // Add more mock data as needed
    ]);
  }, []);

  return (
    <Container maxWidth="lg">
      <Box sx={{ mt: 4, mb: 6 }}>
        <Typography variant="h4" gutterBottom>
          {t('myLinks.title')}
        </Typography>

        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>{t('myLinks.originalUrl')}</TableCell>
                <TableCell>{t('myLinks.shortUrl')}</TableCell>
                <TableCell>{t('myLinks.createdAt')}</TableCell>
                <TableCell>{t('myLinks.clicks')}</TableCell>
                <TableCell>{t('myLinks.actions')}</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {links.map((link) => (
                <TableRow key={link.id}>
                  <TableCell>{link.originalUrl}</TableCell>
                  <TableCell>{link.shortUrl}</TableCell>
                  <TableCell>{link.createdAt}</TableCell>
                  <TableCell>{link.clicks}</TableCell>
                  <TableCell>
                    <IconButton 
                      color="primary"
                      onClick={() => navigate(`/analytics/${link.id}`)}
                    >
                      <AnalyticsIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Box>
    </Container>
  );
};

export default MyLinksPage; 