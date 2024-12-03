package org.example;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;
import java.io.IOException;
import java.util.Base64;

public class UrlShortener {
    private final BigtableDataClient dataClient;

    // Column families and columns for USER and LINK tables
    private static final String USER_COLUMN_FAMILY = "user_data";
    private static final String LINK_COLUMN_FAMILY = "link_data";
    private static final String LONG_URL_COLUMN = "long_url";
    private static final String USER_ID_COLUMN = "user_id";
    private static final String CREATION_DATE_COLUMN = "creation_date";

    public UrlShortener(BigtableDataClient dataClient) {
        this.dataClient = dataClient;
    }

    // Method to add a new user
    public void addUser(String userId, String name, String email, String creationDate) throws IOException {
        RowMutation rowMutation = RowMutation.create(BigtableConnector.USER_TABLE_ID, userId)
                .setCell(USER_COLUMN_FAMILY, "name", name)
                .setCell(USER_COLUMN_FAMILY, "email", email)
                .setCell(USER_COLUMN_FAMILY, CREATION_DATE_COLUMN, creationDate);
        dataClient.mutateRow(rowMutation);
    }

    // Method to add a new shortened URL for a user
    public String shortenUrl(String userId, String longUrl, String creationDate) throws IOException {
        String shortUrl = generateShortUrl(longUrl);

        RowMutation rowMutation = RowMutation.create(BigtableConnector.LINK_TABLE_ID, shortUrl)
                .setCell(LINK_COLUMN_FAMILY, LONG_URL_COLUMN, longUrl)
                .setCell(LINK_COLUMN_FAMILY, USER_ID_COLUMN, userId)
                .setCell(LINK_COLUMN_FAMILY, CREATION_DATE_COLUMN, creationDate);
        dataClient.mutateRow(rowMutation);

        return shortUrl;
    }

    // Method to retrieve the original URL from a short URL
    public String getOriginalUrl(String shortUrl) throws IOException {
        Row row = dataClient.readRow(BigtableConnector.LINK_TABLE_ID, shortUrl);
        if (row == null) {
            return null;
        }
        for (RowCell cell : row.getCells(LINK_COLUMN_FAMILY, LONG_URL_COLUMN)) {
            return cell.getValue().toStringUtf8();
        }
        return null;
    }

    // Helper method to generate a short URL (simple Base64 encoding example)
    private String generateShortUrl(String longUrl) {
        return Base64.getUrlEncoder().encodeToString(longUrl.getBytes()).substring(0, 7);
    }
}
