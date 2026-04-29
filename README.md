# Notification Service with Apache Kafka

Event-driven notification microservice with Email, Push and SMS support, retry logic, and Dead Letter Queue.

> Java 17 · Spring Boot 3.2 · Apache Kafka · Spring Mail · PostgreSQL · Docker

## Features
- Send notifications via Kafka topics (EMAIL, PUSH, SMS)
- Automatic retry with exponential backoff (3 attempts)
- Dead Letter Queue for permanently failed messages
- All notifications logged to PostgreSQL
- Kafka UI at http://localhost:8090
- Swagger docs at http://localhost:8080/swagger-ui.html

## Architecture
```
POST /api/notifications
       ↓
  NotificationProducer → Kafka Topic (notification.email / .push / .sms)
       ↓
  NotificationConsumer → Send Email / Push / SMS
       ↓ (on failure)
  Retry x3 with backoff
       ↓ (still fails)
  Dead Letter Topic (notification.email.dead-letter)
```

## Quick start
```bash
docker-compose up --build
```

## Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/notifications | Send notification via Kafka |
| GET | /api/notifications | All notifications |
| GET | /api/notifications/recipient/{r} | By recipient |
| GET | /api/notifications/status/{s} | By status (PENDING/SENT/FAILED) |

## Example request
```json
POST /api/notifications
{
  "type": "EMAIL",
  "recipient": "user@example.com",
  "subject": "Welcome!",
  "message": "Your account has been created."
}
```

