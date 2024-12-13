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
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, LineChart, Line } from 'recharts';

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

        const response = await axios.post(`${API_BASE_URL}/analysis`, {
          shortUrl: id,
        });

        // Process the metrics data
        const totalClicks = response.data.metrics.reduce((sum, day) => sum + day.click, 0);
        
        // Process daily clicks data for line chart
        const dailyClicks = response.data.metrics.map(day => ({
          date: formatDate(day.date),
          clicks: day.click
        }));

        // Process country data for bar chart
        const countryData = {};
        response.data.metrics.forEach(day => {
          day.country.forEach(country => {
            if (country.name !== 'IP address unavailable' && country.name !== 'UNKNOWN') {
              countryData[country.name] = (countryData[country.name] || 0) + country.click;
            }
          });
        });

        const topCountries = Object.entries(countryData)
          .map(([name, clicks]) => ({ name, clicks }))
          .sort((a, b) => b.clicks - a.clicks)
          .slice(0, 5);

        setAnalytics({
          totalClicks,
          dailyClicks,
          topCountries
        });
      } catch (err) {
        setError(err.response?.data?.message || t("analytics.fetchError"));
      }
    };

    if (id) {
      fetchAnalytics();
    }
  }, [id, t]);

  // Helper function to format date from YYYYMMDD to MM/DD
  const formatDate = (dateStr) => {
    const month = dateStr.substring(4, 6);
    const day = dateStr.substring(6, 8);
    return `${month}/${day}`;
  };

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
          {/* Total Clicks Card */}
          <Grid item xs={12}>
            <Card>
              <CardContent>
                <Typography variant="h6">{t("analytics.totalClicks")}</Typography>
                <Typography variant="h3">{analytics?.totalClicks || 0}</Typography>
              </CardContent>
            </Card>
          </Grid>

          {/* Daily Clicks Line Chart */}
          <Grid item xs={12}>
            <Paper sx={{ p: 2 }}>
              <Typography variant="h6" gutterBottom>
                {t("analytics.dailyClicks")}
              </Typography>
              <Box sx={{ height: 300 }}>
                <ResponsiveContainer width="100%" height="100%">
                  <LineChart data={analytics?.dailyClicks}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="date" />
                    <YAxis />
                    <Tooltip />
                    <Line type="monotone" dataKey="clicks" stroke="#8884d8" />
                  </LineChart>
                </ResponsiveContainer>
              </Box>
            </Paper>
          </Grid>

          {/* Top Countries Bar Chart */}
          <Grid item xs={12}>
            <Paper sx={{ p: 2 }}>
              <Typography variant="h6" gutterBottom>
                {t("analytics.topCountries")}
              </Typography>
              <Box sx={{ height: 300 }}>
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={analytics?.topCountries}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="name" />
                    <YAxis />
                    <Tooltip />
                    <Bar dataKey="clicks" fill="#8884d8" />
                  </BarChart>
                </ResponsiveContainer>
              </Box>
            </Paper>
          </Grid>
        </Grid>
      </Box>
    </Container>
  );
};

export default AnalyticsPage;
