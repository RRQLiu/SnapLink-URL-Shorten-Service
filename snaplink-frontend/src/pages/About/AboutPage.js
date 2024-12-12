import React from "react";
import {
  Container,
  Typography,
  Box,
  Grid,
  Card,
  CardContent,
  CardMedia,
} from "@mui/material";
import { useTranslation } from "react-i18next";

const AboutPage = () => {
  const { t } = useTranslation();

  const teamMembers = [
    {
      name: "Feifei Li",
      role: "Frontend Developer",
      image: "https://imgur.com/CxdR4Xv.jpg",
      description: t("lalalala"),
    },
    {
      name: "Ruiqi Liu",
      role: "Backend Developer",
      image: "https://imgur.com/ZCmk4st.jpg",
      description: t("lalalala"),
    },
    {
      name: "Ray Zhang",
      role: "Backend Developer",
      image: "https://imgur.com/uQImhWk.jpg",
      description: t("lalalala"),
    },
    {
      name: "Yutong Feng",
      role: "Backend Developer",
      image: "https://imgur.com/3Y9ybQG.jpg",
      description: t("lalalala"),
    },
  ];

  return (
    <Container maxWidth="lg">
      <Box sx={{ mt: 4, mb: 6 }}>
        <Typography variant="h4" gutterBottom align="center">
          {t("about.title")}
        </Typography>

        <Box sx={{ my: 4 }}>
          <Typography variant="h5" gutterBottom>
            {t("about.missionTitle")}
          </Typography>
          <Typography paragraph>{t("about.missionText")}</Typography>
        </Box>

        <Typography variant="h5" gutterBottom>
          {t("about.teamTitle")}
        </Typography>

        <Grid container spacing={4}>
          {teamMembers.map((member, index) => (
            <Grid item xs={12} sm={6} md={3} key={index}>
              <Card>
                <CardMedia
                  component="img"
                  height="300"
                  image={member.image}
                  alt={member.name}
                  sx={{ objectFit: "cover", width: "100%" }}
                />
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    {member.name}
                  </Typography>
                  <Typography
                    variant="subtitle1"
                    color="text.secondary"
                    gutterBottom
                  >
                    {member.role}
                  </Typography>
                  <Typography variant="body2">{member.description}</Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </Box>
    </Container>
  );
};

export default AboutPage;
