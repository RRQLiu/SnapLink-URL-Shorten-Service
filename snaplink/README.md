# Guide for Implementing URL Shortening and Resolution with Google Cloud Bigtable

## Step 1: Set Up Google Cloud Project and Enable Bigtable

1. **Create a Google Cloud Project**
   - Visit [Google Cloud Console](https://console.cloud.google.com/) and create a new project.

2. **Enable Bigtable API**
   - Go to the "APIs & Services" page, search for `Bigtable API`, and enable it.

3. **Set Up Bigtable Instance**
   - In the Cloud Console, navigate to the Bigtable page and create a Bigtable instance to store URL mapping data.
   - Choose **SSD** for lower latency and higher read throughput, which is suitable for real-time delivery scenarios.
   - Configure auto-scaling and replication nodes (e.g., primary node in Dallas, replica in South Carolina) with settings for CPU utilization and storage usage.

4. **Create Tables and Column Families**
   - Use the `cbt` command to create necessary tables and column families.

5. **Create a Service Account**
   - Go to `IAM & Admin > Service Accounts`, create a service account, and assign the `Bigtable User` role.
   - Download the service account JSON key file and save it securely for later use.

---

## Step 2: Create a Java Project in IntelliJ and Add Dependencies

1. **Open IntelliJ**
   - Create a new Java project and select **Maven** as the build tool.

2. **Add Bigtable Dependencies**
   - In the project's `pom.xml`, add the following dependencies (adjust versions based on your JDK):

     ```xml
     <dependencies>
         <dependency>
             <groupId>com.google.cloud</groupId>
             <artifactId>google-cloud-bigtable</artifactId>
             <version>2.46.0</version> <!-- Check the latest version on the official site -->
         </dependency>

         <dependency>
             <groupId>com.google.cloud</groupId>
             <artifactId>google-cloud-core</artifactId>
             <version>2.17.0</version> <!-- Use the latest version -->
         </dependency>
     </dependencies>
     ```

3. **Load Dependencies**
   - Save the file and let IntelliJ load the dependencies.

---

## Step 3: Configure Bigtable Connection

1. **Add the Service Account Key**
   - Place the downloaded JSON key file in your project directory and reference it in your code to connect to Bigtable.

2. **Write Code for Connecting to Bigtable**
   - Create a `BigtableConnector` class to initialize the Bigtable client. Specify the `PROJECT_ID`, `INSTANCE_ID`, and `TABLE_ID`.

---

## Step 4: Implement URL Shortening and Resolution

1. **Write URL Shortening Code**
   - Implement logic for generating short URLs and store the mappings in Bigtable.

2. **Write URL Resolution Code**
   - Retrieve the original URL corresponding to a short URL from Bigtable.

---

## Step 5: Test the Project

1. **Write Test Code**
   - Use JUnit or write a `main` method to test the URL shortening and resolution functionalities.
