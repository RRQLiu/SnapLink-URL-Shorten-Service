const API_BASE_URL = process.env.REACT_APP_API_URL || "http://localhost:8080";

export const authService = {
  async login(email, password) {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, password }),
    });

    if (!response.ok) {
      throw new Error("Login failed");
    }

    const data = await response.json();
    localStorage.setItem("token", data.token);
    localStorage.setItem("userID", data.userId);
    return data;
  },

  async register(email, password) {
    const response = await fetch(`${API_BASE_URL}/auth/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, password }),
    });

    if (!response.ok) {
      throw new Error("Registration failed");
    }

    const data = await response.json();
    localStorage.setItem("token", data.token);
    localStorage.setItem("userID", data.userId);
    return data;
  },

  async logout() {
    const token = localStorage.getItem("token");
    const response = await fetch(`${API_BASE_URL}/auth/logout`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    localStorage.removeItem("token");
    localStorage.removeItem("userID");

    if (!response.ok) {
      throw new Error("Logout failed");
    }
  },
};
