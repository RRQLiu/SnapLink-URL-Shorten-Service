import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { ThemeProvider } from "@mui/material";
import { theme } from "./theme";
import Navigation from "./components/Navigation/Navigation";
import LandingPage from "./pages/Landing/LandingPage";
import AuthPage from "./pages/Auth/AuthPage";
import MainPage from "./pages/Main/MainPage";
import MyLinksPage from "./pages/MyLinks/MyLinksPage";
import AboutPage from "./pages/About/AboutPage";
import AnalyticsPage from "./pages/Analytics/AnalyticsPage";
import LanguageSelector from "./components/LanguageSelector/LanguageSelector";

import { I18nextProvider } from "react-i18next";
import i18n from "./i18n/config";
import { authService } from './services/authService';

function App() {
  const [isAuthenticated, setIsAuthenticated] = React.useState(
    !!localStorage.getItem('token')
  );

  const login = async (email, password) => {
    try {
      await authService.login(email, password);
      setIsAuthenticated(true);
      return true;
    } catch (error) {
      console.error('Login failed:', error);
      return false;
    }
  };

  const logout = async () => {
    try {
      await authService.logout();
      setIsAuthenticated(false);
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  return (
    <I18nextProvider i18n={i18n}>
      <ThemeProvider theme={theme}>
        <BrowserRouter>
          {isAuthenticated && <Navigation onLogout={logout} />}
          <LanguageSelector />
          <Routes>
            <Route
              path="/"
              element={
                !isAuthenticated ? <LandingPage /> : <Navigate to="/main" />
              }
            />
            <Route path="/auth" element={<AuthPage onLogin={login} />} />
            <Route
              path="/main"
              element={isAuthenticated ? <MainPage /> : <Navigate to="/" />}
            />
            <Route
              path="/my-links"
              element={isAuthenticated ? <MyLinksPage /> : <Navigate to="/" />}
            />
            <Route path="/about" element={<AboutPage />} />
            <Route
              path="/analytics/:id"
              element={
                isAuthenticated ? <AnalyticsPage /> : <Navigate to="/" />
              }
            />
          </Routes>
        </BrowserRouter>
      </ThemeProvider>
    </I18nextProvider>
  );
}

export default App;
