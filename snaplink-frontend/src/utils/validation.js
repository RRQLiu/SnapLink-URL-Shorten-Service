export const isValidUrl = (url) => {
  try {
    new URL(url);
    return true;
  } catch (error) {
    return false;
  }
};

export const isValidCustomName = (name) => {
  // Allow alphanumeric characters and hyphens, 3-20 characters
  const regex = /^[a-zA-Z0-9-]{3,20}$/;
  return regex.test(name);
};
