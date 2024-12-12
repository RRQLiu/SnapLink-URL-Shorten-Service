import axios from "axios";

const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || "http://localhost:3000/api",
});

// Add auth token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("mockToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const mockData = {
  links: [
    {
      id: 1,
      originalUrl: "https://www.example.com/very/long/url/1",
      shortUrl: "http://short.ly/abc123",
      createdAt: "2024-02-01",
      clicks: 150,
    },
    // Add more mock data
  ],
  analytics: {
    id: 1,
    totalClicks: 150,
    uniqueVisitors: 120,
    topCountries: [
      { country: "United States test test", clicks: 50123 },
      { country: "Canada", clicks: 30 },
      { country: "United Kingdom", clicks: 20 },
    ],
    browsers: [
      { name: "Chrome", count: 80 },
      { name: "Firefox", count: 40 },
      { name: "Safari", count: 30 },
    ],
  },
};

export const urlService = {
  createShortUrl: async (originalUrl, customName) => {
    // Mock API call
    return {
      id: Date.now(),
      originalUrl,
      shortUrl: `http://short.ly/${
        customName || Math.random().toString(36).substr(2, 6)
      }`,
      createdAt: new Date().toISOString(),
    };
  },

  getOriginalUrl: async (shortUrl) => {
    // Mock API call
    return {
      originalUrl: "https://www.example.com/very/long/original/url",
    };
  },

  getUserLinks: async () => {
    // Mock API call
    return mockData.links;
  },

  getLinkAnalytics: async (id) => {
    // Mock API call
    return mockData.analytics;
  },
};

export const authService = {
  login: async (email, password) => {
    // Mock API call
    return {
      token: "mock-token-123",
      user: {
        id: 1,
        email,
        name: "Test User",
      },
    };
  },

  register: async (email, password) => {
    // Mock API call
    return {
      token: "mock-token-123",
      user: {
        id: 1,
        email,
        name: "Test User",
      },
    };
  },

  logout: async () => {
    localStorage.removeItem("mockToken");
  },
};

export default api;
