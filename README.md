# Spring Boot Feature Flags Application

A Spring Boot monolith application integrated with Harness Feature Flags Java SDK. This application demonstrates how to evaluate feature flags in a Spring Boot application.

## Prerequisites

- **JDK 17** or newer
- **Maven 3.6+** or newer
- **Harness Feature Flags Account** with:
  - A Feature Flag created (e.g., `harnessappdemodarkmode`)
  - A Server SDK Key generated

## Getting Started

### 1. Clone or Navigate to the Project

```bash
cd spring-boot-ff
```

### 2. Configure Harness Feature Flags API Key

You need to set your Harness Feature Flags Server SDK Key. You can do this in one of the following ways:

#### Option A: Environment Variable (Recommended)

```bash
export FF_API_KEY="your-server-sdk-key-here"
```

#### Option B: Application Properties

Edit `src/main/resources/application.properties` or `application.yml`:

```properties
harness.ff.api-key=your-server-sdk-key-here
```

#### Option C: System Property

```bash
mvn spring-boot:run -DFF_API_KEY=your-server-sdk-key-here
```

### 3. Build the Application

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

Or if you've built the JAR:

```bash
java -jar target/spring-boot-ff-1.0.0.jar
```

The application will start on `http://localhost:8080`

## API Endpoints

### Health Check

```bash
GET http://localhost:8080/api/feature-flags/health
```

### Evaluate Boolean Feature Flag

```bash
POST http://localhost:8080/api/feature-flags/evaluate/boolean
Content-Type: application/json

{
  "flagIdentifier": "harnessappdemodarkmode",
  "targetIdentifier": "user123",
  "targetName": "John Doe",
  "targetAttributes": {
    "email": "john.doe@example.com",
    "location": "US"
  },
  "defaultValue": "false"
}
```

### Evaluate String Feature Flag

```bash
POST http://localhost:8080/api/feature-flags/evaluate/string
Content-Type: application/json

{
  "flagIdentifier": "my-string-flag",
  "targetIdentifier": "user123",
  "targetName": "John Doe",
  "defaultValue": "default-value"
}
```

### Evaluate Number Feature Flag

```bash
POST http://localhost:8080/api/feature-flags/evaluate/number
Content-Type: application/json

{
  "flagIdentifier": "my-number-flag",
  "targetIdentifier": "user123",
  "defaultValue": "0"
}
```

### Evaluate JSON Feature Flag

```bash
POST http://localhost:8080/api/feature-flags/evaluate/json
Content-Type: application/json

{
  "flagIdentifier": "my-json-flag",
  "targetIdentifier": "user123",
  "targetAttributes": {
    "plan": "premium"
  }
}
```

## Example cURL Commands

### Boolean Flag Evaluation

```bash
curl -X POST http://localhost:8080/api/feature-flags/evaluate/boolean \
  -H "Content-Type: application/json" \
  -d '{
    "flagIdentifier": "harnessappdemodarkmode",
    "targetIdentifier": "javasdk",
    "targetName": "JavaSDK",
    "targetAttributes": {
      "location": "emea"
    },
    "defaultValue": "false"
  }'
```

### String Flag Evaluation

```bash
curl -X POST http://localhost:8080/api/feature-flags/evaluate/string \
  -H "Content-Type: application/json" \
  -d '{
    "flagIdentifier": "my-string-flag",
    "targetIdentifier": "user123",
    "defaultValue": "default"
  }'
```

## Configuration Options

The application supports the following configuration properties (can be set via environment variables or application.properties):

| Property | Environment Variable | Default | Description |
|----------|---------------------|---------|-------------|
| `harness.ff.api-key` | `FF_API_KEY` | (required) | Harness FF Server SDK Key |
| `harness.ff.config-url` | `FF_CONFIG_URL` | `https://config.ff.harness.io/api/1.0` | Config URL for fetching flags |
| `harness.ff.event-url` | `FF_EVENT_URL` | `https://events.ff.harness.io/api/1.0` | Event URL for posting metrics |
| `harness.ff.poll-interval-seconds` | `FF_POLL_INTERVAL` | `60` | Polling interval in seconds (when stream disabled) |
| `harness.ff.stream-enabled` | `FF_STREAM_ENABLED` | `true` | Enable streaming mode |
| `harness.ff.analytics-enabled` | `FF_ANALYTICS_ENABLED` | `true` | Enable analytics |
| `harness.ff.frequency` | `FF_FREQUENCY` | `60` | Metrics posting frequency in seconds |

## Using Relay Proxy

If you're using the Harness Relay Proxy, update the configuration:

```properties
harness.ff.config-url=http://localhost:7000
harness.ff.event-url=http://localhost:7000
```

Or via environment variables:

```bash
export FF_CONFIG_URL=http://localhost:7000
export FF_EVENT_URL=http://localhost:7000
```

## Project Structure

```
spring-boot-ff/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/ff/
│   │   │       ├── SpringBootFeatureFlagsApplication.java
│   │   │       ├── config/
│   │   │       │   └── HarnessFFConfig.java
│   │   │       ├── controller/
│   │   │       │   └── FeatureFlagController.java
│   │   │       ├── dto/
│   │   │       │   ├── FeatureFlagEvaluationRequest.java
│   │   │       │   └── FeatureFlagEvaluationResponse.java
│   │   │       └── service/
│   │   │           └── FeatureFlagService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application.yml
│   └── test/
├── pom.xml
└── README.md
```

## Using Feature Flags in Your Code

You can inject the `FeatureFlagService` into your services or controllers:

```java
@Service
@RequiredArgsConstructor
public class MyService {
    
    private final FeatureFlagService featureFlagService;
    
    public void doSomething(String userId) {
        Target target = featureFlagService.createTarget(
            userId,
            "User Name",
            Map.of("email", "user@example.com")
        );
        
        boolean isFeatureEnabled = featureFlagService.getBooleanVariation(
            "my-feature-flag",
            target,
            false
        );
        
        if (isFeatureEnabled) {
            // New feature logic
        } else {
            // Old feature logic
        }
    }
}
```

## Troubleshooting

### SDK Initialization Errors

- **Error 1001**: Authentication failed - Check your API key
- **Error 1002**: Missing or empty API key - Ensure `FF_API_KEY` is set
- **Error 2001**: Authentication failed with non-recoverable error - Verify API key is correct

### Common Issues

1. **API Key not found**: Make sure you've set the `FF_API_KEY` environment variable or configured it in `application.properties`

2. **Connection timeout**: Check your network connection and ensure the Harness FF endpoints are accessible

3. **Flag not found**: Ensure the flag identifier matches exactly what's configured in Harness

## References

- [Harness Feature Flags Java SDK Documentation](https://developer.harness.io/docs/feature-flags/use-ff/ff-sdks/server-sdks/integrate-feature-flag-with-java-sdk/)
- [Harness Feature Flags Overview](https://developer.harness.io/docs/feature-flags/get-started/ff-getting-started-harness/)
- [Java SDK GitHub Repository](https://github.com/harness/ff-java-server-sdk)

## License

This project is provided as-is for demonstration purposes.

