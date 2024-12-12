import React from 'react';
import { Select, MenuItem } from '@mui/material';
import { useTranslation } from 'react-i18next';

const LanguageSelector = () => {
  const { i18n } = useTranslation();

  const changeLanguage = (event) => {
    i18n.changeLanguage(event.target.value);
  };

  return (
    <Select
      value={i18n.language}
      onChange={changeLanguage}
      sx={{ 
        position: 'absolute', 
        top: '0.2rem', 
        right: '8rem',
        color: 'black'
      }}
    >
      <MenuItem value="en">English</MenuItem>
      <MenuItem value="es">Español</MenuItem>
      <MenuItem value="zh">中文</MenuItem>
    </Select>
  );
};

export default LanguageSelector; 