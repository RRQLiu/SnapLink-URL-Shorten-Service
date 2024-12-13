import React, { useState, useEffect } from "react";
import {
  Container,
  Paper,
  Typography,
  Box,
  Grid,
  Card,
  CardContent,
  Alert,
} from "@mui/material";
import { useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import axios from "axios";
import { API_BASE_URL } from "../../config/api.js";

const AnalyticsPage = () => {
  const { id } = useParams();
  const { t } = useTranslation();
  const [analytics, setAnalytics] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchAnalytics = async () => {
      try {
        const userId = localStorage.getItem("userID");
        if (!userId) {
          setError(t("analytics.loginRequired"));
          return;
        }

        // Get analytics data
        const response = await axios.get(`${API_BASE_URL}/api/analytics/${id}`);

        // Get detailed metrics
        // const metricsResponse = await axios.post(`${API_BASE_URL}/api/analysis`, {
        //   shortUrl: id,
        //   // You can add startDate and endDate here if needed
        // });

        // Combine both responses into a single analytics object
        setAnalytics({
          totalClicks: response.data.totalClicks,
          uniqueVisitors: response.data.totalClicks, // If unique visitors isn't provided separately
          topCountries: Object.entries(response.data.clicksByCountry)
            .map(([country, clicks]) => ({
              country,
              clicks,
            }))
            .sort((a, b) => b.clicks - a.clicks),
          dailyClicks: Object.entries(response.data.clicksByDate).map(
            ([date, clicks]) => ({
              date,
              clicks,
            })
          ),
          browsers: Object.entries(response.data.clicksByBrowser).map(
            ([name, count]) => ({
              name,
              count,
            })
          ),
        });
      } catch (err) {
        setError(err.response?.data?.message || t("analytics.fetchError"));
      }
    };

    if (id) {
      fetchAnalytics();
    }
  }, [id, t]);

  if (error) {
    return (
      <Container maxWidth="lg">
        <Alert severity="error" sx={{ mt: 4 }}>
          {error}
        </Alert>
      </Container>
    );
  }

  if (!analytics) {
    return <Typography>Loading...</Typography>;
  }

  return (
    <Container maxWidth="lg">
      <Box sx={{ mt: 4, mb: 6 }}>
        <Typography variant="h4" gutterBottom>
          {t("analytics.title")}
        </Typography>

        <Grid container spacing={3}>
          {/* Overview Cards */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6">
                  {t("analytics.totalClicks")}
                </Typography>
                <Typography variant="h3">{analytics.totalClicks}</Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} md={6}>
            <Card>
              <CardContent>
                <Typography variant="h6">
                  {t("analytics.uniqueVisitors")}
                </Typography>
                <Typography variant="h3">{analytics.uniqueVisitors}</Typography>
              </CardContent>
            </Card>
          </Grid>

          {/* Top Countries */}
          <Grid item xs={12} md={6}>
            <Paper sx={{ p: 2 }}>
              <Typography variant="h6" gutterBottom>
                {t("analytics.topCountries")}
              </Typography>
              {analytics.topCountries.map((country, index) => (
                <Box key={index} sx={{ mb: 1 }}>
                  <Typography>
                    {country.country}: {country.clicks} {t("analytics.clicks")}
                  </Typography>
                </Box>
              ))}
            </Paper>
          </Grid>

          {/* Browsers */}
          <Grid item xs={12} md={6}>
            <Paper sx={{ p: 2 }}>
              <Typography variant="h6" gutterBottom>
                {t("analytics.browsers")}
              </Typography>
              {analytics.browsers.map((browser, index) => (
                <Box key={index} sx={{ mb: 1 }}>
                  <Typography>
                    {browser.name}: {browser.count} {t("analytics.users")}
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
