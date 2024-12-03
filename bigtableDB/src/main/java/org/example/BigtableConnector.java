package org.example;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.auth.oauth2.ServiceAccountCredentials;

import java.io.FileInputStream;
import java.io.IOException;

public class BigtableConnector {
//    private static final String PROJECT_ID = "enduring-amp-440421-t0";
    private static final String PROJECT_ID = "loyal-flames-443615-r0";

    private static final String INSTANCE_ID = "snaplinkdb";
    public static final String USER_TABLE_ID = "usertable";
    public static final String LINK_TABLE_ID = "linktable";

    public static BigtableDataClient connect() throws IOException {
        BigtableDataSettings settings = BigtableDataSettings.newBuilder()
                .setProjectId(PROJECT_ID)
                .setInstanceId(INSTANCE_ID)
                .setCredentialsProvider(() -> ServiceAccountCredentials.fromStream(new FileInputStream("src/main/resources/bigtable-key.json")))
                .build();
        return BigtableDataClient.create(settings);
    }
}
