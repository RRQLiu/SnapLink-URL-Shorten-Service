# SnapLink URL Shortening Service

A URL shortening service built with Spring Boot and Google Cloud Bigtable.

## Prerequisites

- Java 17 or higher
- Gradle
- Google Cloud Account with Bigtable instance
- Google Cloud API key (JSON format)


## Setup

1. Clone the repository
```bash
git clone [repository-url]
cd SnapLink-URL-Shorten-Service
```

2. Place your Google Cloud API key
- Save your Google Cloud API key as `apiKey.json`
- Place it in `snaplink/src/main/resources/`


## Building the Project

Build both modules:
```bash
./gradlew clean build
```

## Running the Application

```bash
./gradlew :snaplink:bootRun
```

## Testing

### Test Bigtable Connection
Test the Bigtable connection and basic operations:
```bash
./gradlew :bigtableDB:run
```

### Test REST Endpoints

1. Create a shortened URL:
```bash
curl -X POST http://localhost:8080/api/shorten \
-H "Content-Type: application/json" \
-d '{
    "userId": "user123",
    "longUrl": "https://www.example.com/very/long/url"
}'
```

2. Retrieve the original URL:
```bash
curl http://localhost:8080/api/url/{shortUrl}
```
Replace {shortUrl} with the shortened URL received from the POST request.

