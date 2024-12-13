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
  Box,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import AnalyticsIcon from '@mui/icons-material/Analytics';
import { useTranslation } from 'react-i18next';
import axios from 'axios';
import { API_BASE_URL } from "../../config/api.js";
import DeleteIcon from '@mui/icons-material/Delete';

const MyLinksPage = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [links, setLinks] = useState([]);
  const [error, setError] = useState("");
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [linkToDelete, setLinkToDelete] = useState(null);

  useEffect(() => {
    const fetchLinks = async () => {
      try {
        const userId = localStorage.getItem("userID");
        if (!userId) {
          setError(t("myLinks.loginRequired"));
          return;
        }

        const response = await axios.get(`${API_BASE_URL}/users/${userId}/links`);
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

  const handleDeleteClick = (link) => {
    setLinkToDelete(link);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    try {
      const userId = localStorage.getItem("userID");
      await axios.delete(`${API_BASE_URL}/links/${linkToDelete.id}?userId=${userId}`);
      
      // Refetch the links
      const response = await axios.get(`${API_BASE_URL}/users/${userId}/links`);
      setLinks(response.data.links.map(link => ({
        id: link.shortUrl,
        originalUrl: link.longUrl,
        shortUrl: `${API_BASE_URL}/url/${link.shortUrl}`,
        createdAt: link.creationDate,
        clicks: link.totalClicks,
        active: link.active
      })));
    } catch (err) {
      setError(err.response?.data?.message || t("myLinks.deleteError"));
    }
    setDeleteDialogOpen(false);
    setLinkToDelete(null);
  };

  return (
    <Container maxWidth="lg">
      <Box sx={{ mt: 4, mb: 6 }}>
        <Typography variant="h4" gutterBottom>
          {t('myLinks.title')}
        </Typography>

        <TableContainer component={Paper}>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell sx={{ width: '30%', whiteSpace: 'normal', wordBreak: 'break-word' }}>
                  {t('myLinks.originalUrl')}
                </TableCell>
                <TableCell sx={{ width: '25%', whiteSpace: 'normal', wordBreak: 'break-word' }}>
                  {t('myLinks.shortUrl')}
                </TableCell>
                <TableCell sx={{ width: '15%', whiteSpace: 'normal' }}>
                  {t('myLinks.createdAt')}
                </TableCell>
                <TableCell sx={{ width: '10%' }}>
                  {t('myLinks.clicks')}
                </TableCell>
                <TableCell sx={{ width: '10%' }}>
                  {t('myLinks.actions')}
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {links.map((link) => (
                <TableRow key={link.id}>
                  <TableCell sx={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
                    {link.originalUrl}
                  </TableCell>
                  <TableCell sx={{ whiteSpace: 'normal', wordBreak: 'break-word' }}>
                    {link.shortUrl}
                  </TableCell>
                  <TableCell sx={{ whiteSpace: 'normal' }}>
                    {link.createdAt}
                  </TableCell>
                  <TableCell>
                    {link.clicks}
                  </TableCell>
                  <TableCell>
                    <IconButton 
                      color="primary"
                      onClick={() => navigate(`/analytics/${link.id}`)}
                    >
                      <AnalyticsIcon />
                    </IconButton>
                    <IconButton 
                      color="error"
                      onClick={() => handleDeleteClick(link)}
                    >
                      <DeleteIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Box>
      <Dialog
        open={deleteDialogOpen}
        onClose={() => setDeleteDialogOpen(false)}
      >
        <DialogTitle>{t('myLinks.deleteConfirmTitle')}</DialogTitle>
        <DialogContent>
          <DialogContentText>
            {t('myLinks.deleteConfirmMessage')}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialogOpen(false)}>
            {t('common.cancel')}
          </Button>
          <Button onClick={handleDeleteConfirm} color="error" autoFocus>
            {t('common.delete')}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default MyLinksPage; 