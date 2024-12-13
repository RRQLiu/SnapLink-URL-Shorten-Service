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
import axios from 'axios';
import { API_BASE_URL } from "../../config/api.js";

const MyLinksPage = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [links, setLinks] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchLinks = async () => {
      try {
        const userId = localStorage.getItem("userID");
        if (!userId) {
          setError(t("myLinks.loginRequired"));
          return;
        }

        const response = await axios.get(`${API_BASE_URL}/api/users/${userId}/links`);
        setLinks(response.data.links.map(link => ({
          id: link.shortUrl, // Using shortUrl as id since it's unique
          originalUrl: link.longUrl,
          shortUrl: `${API_BASE_URL}/url/${link.shortUrl}`,
          createdAt: link.creationDate,
          clicks: link.totalClicks,
          active: link.active
        })));
      } catch (err) {
        setError(err.response?.data?.message || t("myLinks.fetchError"));
      }
    };

    fetchLinks();
  }, [t]);

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