package org.example;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import com.google.cloud.bigtable.data.v2.models.Query;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import com.google.cloud.bigtable.data.v2.models.Filters;
import com.google.cloud.bigtable.data.v2.models.Query;
public class UrlShortener {
    private final BigtableDataClient dataClient;

    // Column families and columns for USER and LINK tables
    private static final String USER_COLUMN_FAMILY = "user_data";
    private static final String LINK_COLUMN_FAMILY = "link_data";
    private static final String LONG_URL_COLUMN = "long_url";
    private static final String USER_ID_COLUMN = "user_id";
    private static final String CREATION_DATE_COLUMN = "creation_date";

    private static final String STATUS_COLUMN = "status";

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
        String shortUrl = generateShortUrl(longUrl, userId);

        RowMutation rowMutation = RowMutation.create(BigtableConnector.LINK_TABLE_ID, shortUrl)
                .setCell(LINK_COLUMN_FAMILY, LONG_URL_COLUMN, longUrl)
                .setCell(LINK_COLUMN_FAMILY, USER_ID_COLUMN, userId)
                .setCell(LINK_COLUMN_FAMILY, CREATION_DATE_COLUMN, creationDate)
                .setCell(LINK_COLUMN_FAMILY, STATUS_COLUMN, "true"); // Activate by default
        dataClient.mutateRow(rowMutation);

        return shortUrl;
    }

    public void updateLinkStatus(String shortUrl, boolean status) throws IOException {
        RowMutation rowMutation = RowMutation.create(BigtableConnector.LINK_TABLE_ID, shortUrl)
                .setCell(LINK_COLUMN_FAMILY, STATUS_COLUMN, String.valueOf(status));
        dataClient.mutateRow(rowMutation);
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
    private String generateShortUrl(String longUrl, String userId) {
        try {
            // Combine the long URL and user ID to ensure uniqueness
            String combinedInput = longUrl + userId;

            // Use SHA-256 to hash the combined input
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combinedInput.getBytes());

            // Encode the hash to Base64 and truncate to 10 characters
            return Base64.getUrlEncoder().encodeToString(hash).substring(0, 10);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash for short URL", e);
        }
    }
    public void deleteRow(String tableId, String rowKey) throws IOException {
        RowMutation rowMutation = RowMutation.create(tableId, rowKey).deleteRow();
        dataClient.mutateRow(rowMutation); // Executes the deletion
    }
    // Delete a user
    public void deleteUser(String userId) throws IOException {
        deleteRow(BigtableConnector.USER_TABLE_ID, userId);
    }

    // Delete a link
    public void deleteLink(String shortUrl) throws IOException {
        deleteRow(BigtableConnector.LINK_TABLE_ID, shortUrl);
    }


    // analytics
    private static final String ANALYTICS_TABLE_ID = "analytics";
    private static final String METRICS_COLUMN_FAMILY = "metrics";

    // Update analytics after a click, we need to do it manuelly w/o rotation
    public void updateAnalytics(String shortUrl, String date, String country) throws IOException {
        String rowKey = shortUrl + "#" + date;

        // Initialize counters
        long totalClicks = 0;
        long countryClicks = 0;

        // Fetch only the latest version of the row data
        Query query = Query.create(ANALYTICS_TABLE_ID).rowKey(rowKey)
                .filter(Filters.FILTERS.limit().cellsPerColumn(1)); // Limit to 1 version per column
        Iterable<Row> rows = dataClient.readRows(query);

        for (Row row : rows) { // Process the fetched row
            totalClicks = row.getCells(METRICS_COLUMN_FAMILY, "total_clicks").stream()
                    .findFirst()
                    .map(cell -> Long.parseLong(cell.getValue().toStringUtf8()))
                    .orElse(0L);

            countryClicks = row.getCells(METRICS_COLUMN_FAMILY, "country:" + country).stream()
                    .findFirst()
                    .map(cell -> Long.parseLong(cell.getValue().toStringUtf8()))
                    .orElse(0L);
        }

        // Increment the values
        totalClicks++;
        countryClicks++;

        // Write the updated values back to Bigtable
        RowMutation mutation = RowMutation.create(ANALYTICS_TABLE_ID, rowKey)
                .setCell(METRICS_COLUMN_FAMILY, "total_clicks", String.valueOf(totalClicks))  // Replace value
                .setCell(METRICS_COLUMN_FAMILY, "country:" + country, String.valueOf(countryClicks));
        dataClient.mutateRow(mutation);
    }

    // Query analytics for a single day or time range
    public Row getAnalytics(String shortUrl, String date) throws IOException {
        String rowKey = shortUrl + "#" + date;
        return dataClient.readRow(ANALYTICS_TABLE_ID, rowKey);
    }

    // Query multiple rows in a range (e.g., for a date range)
    public ArrayList<HashMap<String, String>> queryAnalytics(
            String shortUrl,
            String startDate,
            String endDate
    ) throws IOException {
        if (startDate.compareTo(endDate) > 0) {
            throw new IllegalArgumentException("Start date must be less than end date.");
        }

        String startKey = shortUrl + "#" + startDate;
        String endKey = shortUrl + "#" + (startDate.equals(endDate) ? startDate + "z" : endDate);

        Query query = Query.create(ANALYTICS_TABLE_ID)
                .range(startKey, endKey);

        ArrayList<HashMap<String, String>> rowsData = new ArrayList<>();

        dataClient.readRows(query).forEach(row -> {
            HashMap<String, String> rowMap = new HashMap<>();

            String rowKey = row.getKey().toStringUtf8();
            String dateStr = rowKey.split("#")[1];
//            rowMap.put("rowKey", rowKey); // Store the row key
            rowMap.put("date", dateStr);

            row.getCells().forEach(cell -> {
                // Store each cell's qualifier and value as key-value pairs
                String column = cell.getQualifier().toStringUtf8();
                rowMap.putIfAbsent(column, cell.getValue().toStringUtf8());
            });
            rowsData.add(rowMap); // Add the row data to the list
        });

        return rowsData;
    }


}
