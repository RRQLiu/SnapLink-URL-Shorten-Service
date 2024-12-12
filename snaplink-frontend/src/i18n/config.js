import i18n from "i18next";
import { initReactI18next } from "react-i18next";

import enTranslations from "./translations/en.json";
import esTranslations from "./translations/es.json";
import zhTranslations from "./translations/zh.json";
import ruTranslations from "./translations/ru.json";

i18n.use(initReactI18next).init({
  resources: {
    en: {
      translation: enTranslations,
    },
    es: {
      translation: esTranslations,
    },
    zh: {
      translation: zhTranslations,
    },
    ru: {
      translation: ruTranslations,
    },
  },
  lng: "en", // default language
  fallbackLng: "en",
  interpolation: {
    escapeValue: false,
  },
});

export default i18n;
