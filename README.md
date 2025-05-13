# comVida Backend

Micronaut backend for the **comVida** community intervention platform.

## 📦 Stack

- Micronaut Framework
- PostgreSQL (via Docker)
- ActiveMQ Artemis (JMS)
- JWT-based Authentication
- Liquibase for DB migrations

## 🚀 Getting Started

### Requirements

- Java 17+
- Docker & Docker Compose
- Gradle

### Environment Variables

Create a `.env` file based on the following:

```env
DB_USERNAME=usename
DB_PASSWORD=password
DB_NAME=db_name
DB_HOST=host_address
DB_PORT=db_port
JWT_GENERATOR_SIGNATURE_SECRET=your-very-strong-secret
