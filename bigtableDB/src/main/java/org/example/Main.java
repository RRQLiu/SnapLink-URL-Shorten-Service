package org.example;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            // Connect to Bigtable
            BigtableDataClient dataClient = BigtableConnector.connect();
            UrlShortener urlShortener = new UrlShortener(dataClient);

            // Create a new user
            String userId = "user001";
            String name = "Test User";
            String email = "test@example.com";
            String userCreationDate = "2024-11-12";
            String longUrl = "https://www.example.com/some/long/path/to/resource";

            urlShortener.addUser(userId, name, email, userCreationDate);
            System.out.println("User added: " + name);

            // Shorten a URL for the user
            String linkCreationDate = "2024-11-12";
            String shortUrl = urlShortener.shortenUrl(userId, longUrl, linkCreationDate);
            System.out.println("Shortened URL: " + shortUrl);

            // Retrieve the original URL using the short URL
            String retrievedUrl = urlShortener.getOriginalUrl(shortUrl);
            System.out.println("Retrieved original URL: " + retrievedUrl);

            // Close Bigtable client connection
            dataClient.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
