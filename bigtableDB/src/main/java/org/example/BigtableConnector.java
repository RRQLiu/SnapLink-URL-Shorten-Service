package org.example;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.bigtable.data.v2.models.TableId;
import com.google.auth.oauth2.ServiceAccountCredentials;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

public class BigtableConnector {
    private static final String PROJECT_ID = "loyal-flames-443615-r0";
    private static final String INSTANCE_ID = "snaplinkdb";
    public static final String USER_TABLE_ID = "usertable";
    public static final String LINK_TABLE_ID = "linktable";

    public static BigtableDataClient connect() throws IOException {
        String keyPath = "../snaplink/src/main/resources/apiKey.json";
        File file = new File(keyPath);
        System.out.println("Looking for API key at: " + file.getAbsolutePath());
        System.out.println("File exists: " + file.exists());
        
        BigtableDataSettings settings = BigtableDataSettings.newBuilder()
                .setProjectId(PROJECT_ID)
                .setInstanceId(INSTANCE_ID)
                .setCredentialsProvider(() -> 
                    ServiceAccountCredentials.fromStream(new FileInputStream(keyPath)))
                .build();
        return BigtableDataClient.create(settings);
    }
    public static String getTablePath(String tableId) {
        return String.format("projects/%s/instances/%s/tables/%s", 
            PROJECT_ID, INSTANCE_ID, tableId);
    }
}