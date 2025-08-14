# Gradle Medium Project

This is a medium-sized Java project using Gradle as the build system.

## Project Structure

- `common` - Common utilities and base classes shared across the application

## Requirements

- Java 8+
- Gradle 6.0+

## Building the Project

To build the project, run:

```bash
./gradlew clean build
```

## Common Module

The common module contains shared utilities including:

- Validation framework
- Common utility classes
- Cross-cutting concerns

### Validation Framework

The validation framework provides a flexible way to validate objects and string values using:

1. Java Bean Validation API
2. Regular expression patterns

Example usage:

```java
// Validate an entity using Bean Validation
boolean isValid = validator.validate(userEntity);

// Validate specific values against patterns
boolean isValidEmail = validator.isValid("user@example.com", "email");
```

## License

This project is licensed under the MIT License.