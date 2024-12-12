import React, { useState, useEffect } from 'react';
import { 
  Container, 
  Paper, 
  Typography, 
  Box,
  Grid,
  Card,
  CardContent
} from '@mui/material';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

const AnalyticsPage = () => {
  const { id } = useParams();
  const { t } = useTranslation();
  const [analytics, setAnalytics] = useState(null);

  useEffect(() => {
    // Mock analytics data
    setAnalytics({
      totalClicks: 150,
      uniqueVisitors: 120,
      topCountries: [
        { country: 'United States', clicks: 50 },
        { country: 'Canada', clicks: 30 },
        { country: 'United Kingdom', clicks: 20 }
      ],
      dailyClicks: [
        { date: '2024-02-01', clicks: 25 },
        { date: '2024-02-02', clicks: 30 },
        { date: '2024-02-03', clicks: 45 }
      ],
      browsers: [
        { name: 'Chrome', count: 80 },
        { name: 'Firefox', count: 40 },
        { name: 'Safari', count: 30 }
      ]
    });
  }, [id]);

  if (!analytics) {
    return <Typography>Loading...</Typography>;
  }

  return (
    <Container maxWidth="lg">
      <Box sx={{ mt: 4, mb: 6 }}>
        <Typography variant="h4" gutterBottom>
          {t('analytics.title')}
        </Typography>

        <Grid container spacing={3}>
          {/* Overview Cards */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6">{t('analytics.totalClicks')}</Typography>
                <Typography variant="h3">{analytics.totalClicks}</Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6">{t('analytics.uniqueVisitors')}</Typography>
                <Typography variant="h3">{analytics.uniqueVisitors}</Typography>
              </CardContent>
            </Card>
          </Grid>

          {/* Top Countries */}
          <Grid item xs={12} md={6}>
            <Paper sx={{ p: 2 }}>
              <Typography variant="h6" gutterBottom>
                {t('analytics.topCountries')}
              </Typography>
              {analytics.topCountries.map((country, index) => (
                <Box key={index} sx={{ mb: 1 }}>
                  <Typography>
                    {country.country}: {country.clicks} {t('analytics.clicks')}
                  </Typography>
                </Box>
              ))}
            </Paper>
          </Grid>

          {/* Browsers */}
          <Grid item xs={12} md={6}>
            <Paper sx={{ p: 2 }}>
              <Typography variant="h6" gutterBottom>
                {t('analytics.browsers')}
              </Typography>
              {analytics.browsers.map((browser, index) => (
                <Box key={index} sx={{ mb: 1 }}>
                  <Typography>
                    {browser.name}: {browser.count} {t('analytics.users')}
                  </Typography>
                </Box>
              ))}
            </Paper>
          </Grid>
        </Grid>
      </Box>
    </Container>
  );
};

export default AnalyticsPage; 