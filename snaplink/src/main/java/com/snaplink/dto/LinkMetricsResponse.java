package com.snaplink.dto;

import java.util.ArrayList;

public class LinkMetricsResponse {

    private String link;
    private ArrayList<Metric> metrics;

    // Getters and Setters
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public ArrayList<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(ArrayList<Metric> metrics) {
        this.metrics = metrics;
    }

    public static class Metric {
        private String date;
        private int click;
        private ArrayList<Country> country;

        // Getters and Setters
        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getClick() {
            return click;
        }

        public void setClick(int click) {
            this.click = click;
        }

        public ArrayList<Country> getCountry() {
            return country;
        }

        public void setCountry(ArrayList<Country> country) {
            this.country = country;
        }
    }

    public static class Country {
        private String name;
        private int click; // Fix the typo if needed to "click"

        public Country(String name, int click) {
            this.name = name;
            this.click = click;
        }

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getClick() { // Fix the typo if needed to "getClick"
            return click;
        }

        public void setClick(int click) { // Fix the typo if needed to "setClick"
            this.click = click;
        }
    }
}