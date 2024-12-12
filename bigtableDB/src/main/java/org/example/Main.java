package org.example;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.gson.JsonObject;

import java.io.IOException;

/***
 * This main is just for testing if the connection with bigtable is working!!!
 */
public class Main {
    public static void main(String[] args) {
        try {
            BigtableDataClient dataClient = BigtableConnector.connect();
            UrlShortener urlShortener = new UrlShortener(dataClient);

            String userId = "user002";
            String name = "Test User";
            String email = "test@example.com";
            String userCreationDate = "2024-12-01";
            String salt = "don't guess";
            String hashedPassword = "snfllsnglewnglew";
            urlShortener.addUser(userId, name, email, userCreationDate, salt, hashedPassword);
            System.out.println("User added: " + name);
            JsonObject result = urlShortener.getUser("user002");
            System.out.println(result.toString());
            String[] longUrls = {
                    "https://1st-resource",
                    "https://2nd-resource",
                    "https://3third-resource"
            };
            for (String longUrl : longUrls) {
                String shortUrl = urlShortener.shortenUrl(userId, longUrl, "2024-12-01");
                System.out.println("Created short URL: " + shortUrl);
            }

            String shortUrlForAnalytics = urlShortener.shortenUrl(userId, "https://example.com/analytics-test", "2024-12-03");
            urlShortener.updateAnalytics(shortUrlForAnalytics, "20241203", "US");
            urlShortener.updateAnalytics(shortUrlForAnalytics, "20241203", "US");
            urlShortener.updateAnalytics(shortUrlForAnalytics, "20241203", "IN");
            System.out.println("Analytics updated for URL clicks.");

            System.out.println("Analytics for 20241203:");
            urlShortener.queryAnalytics(shortUrlForAnalytics, "20241203", "20241203");

            System.out.println("Analytics for range 20241201 to 20241231:");
            urlShortener.queryAnalytics(shortUrlForAnalytics, "20241201", "20241231");

            dataClient.close();
            System.out.println("Bigtable client connection closed.");

        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
