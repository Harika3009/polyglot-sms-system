# Polyglot Distributed SMS Service

A distributed microservices-based SMS notification system built using:

- Java Spring Boot (SMS Sender Service)
- GoLang (SMS Store Service)
- Apache Kafka (Event Streaming)
- Redis (Blocked User Validation)
- MongoDB (Message Persistence)

---

# Architecture Overview

## Services

| Service | Technology | Responsibility |
|---|---|---|
| SMS Sender | Java + Spring Boot | Accepts SMS requests, validates users, mocks vendor API, publishes Kafka events |
| SMS Store | GoLang + net/http | Consumes Kafka events, stores SMS records in MongoDB, exposes history API |

---

# End-to-End Flow

1. Client sends request to Java SMS Sender API.
2. Java service checks blocked users in Redis.
3. Java service mocks third-party SMS vendor response (SUCCESS/FAIL).
4. Java service publishes SMS event to Kafka topic.
5. Go service consumes Kafka events.
6. Go service stores SMS records in MongoDB.
7. Client fetches SMS history using Go API.

---

# Tech Stack

- Java 17
- Spring Boot 3
- GoLang
- Apache Kafka
- Redis
- MongoDB
- Docker Compose

---

# Project Structure

```text
polyglot-sms-system/
│
├── docker-compose.yml
│
├── sms-sender-java/
│   ├── src/
│   ├── pom.xml
│   └── application.properties
│
└── sms-store-go/
    ├── main.go
    ├── go.mod
    └── go.sum
```

---

# Prerequisites

Install the following:

- Java 17+
- Maven
- GoLang
- Docker Desktop

---

# Infrastructure Setup

From project root:

```bash
docker compose up -d
```

This starts:

| Service | Port |
|---|---|
| Kafka | 9092 |
| Zookeeper | 2181 |
| Redis | 6379 |
| MongoDB | 27017 |

---

# Verify Docker Containers

```bash
docker ps
```

Expected containers:

- polyglot-sms-system-kafka-1
- polyglot-sms-system-zookeeper-1
- polyglot-sms-system-redis-1
- polyglot-sms-system-mongodb-1

---

# Run Java SMS Sender Service

Open a terminal:

```bash
cd sms-sender-java

mvn spring-boot:run
```

Service starts on:

```text
http://localhost:8080
```

---

# Run Go SMS Store Service

Open another terminal:

```bash
cd sms-store-go

go mod tidy

go run main.go
```

Service starts on:

```text
http://localhost:8081
```

---

# API Documentation

# 1. Send SMS

## Endpoint

```http
POST /v1/sms/send
```

## Request Body

```json
{
  "userId": "user123",
  "phoneNumber": "9876543210",
  "message": "Hello Testing"
}
```

## Example PowerShell Request

```powershell
Invoke-RestMethod `
-Uri "http://localhost:8080/v1/sms/send" `
-Method POST `
-ContentType "application/json" `
-Body '{
  "userId":"user123",
  "phoneNumber":"9876543210",
  "message":"Hello Testing"
}'
```

## Sample Response

```json
{
  "status": "SUCCESS",
  "message": "SMS processed successfully"
}
```

---

# 2. Get SMS History

## Endpoint

```http
GET /v1/user/{userId}/messages
```

## Example

```http
GET /v1/user/user123/messages
```

## Example PowerShell Request

```powershell
Invoke-RestMethod `
-Uri "http://localhost:8081/v1/user/user123/messages" `
-Method GET | ConvertTo-Json -Depth 5
```

## Sample Response

```json
[
  {
    "userId": "user123",
    "phoneNumber": "9876543210",
    "message": "Hello Testing",
    "status": "SUCCESS",
    "createdAt": "2026-05-11T15:50:22Z"
  }
]
```

---

# Redis Blocked User Support

Blocked users are stored in Redis.

If a user exists in the blocked list:
- SMS sending is skipped
- API returns blocked response

---

# Kafka Topic

Kafka topic used:

```text
sms-topic
```

Java service publishes events.

Go service consumes events asynchronously.

---

# MongoDB Storage

Database:

```text
smsdb
```

Collection:

```text
messages
```

---

# MongoDB Verification

Open Mongo shell:

```bash
docker exec -it polyglot-sms-system-mongodb-1 mongosh
```

Then:

```javascript
use smsdb

db.messages.find().pretty()
```

---

# Unit Testing

## Java

Run:

```bash
mvn test
```

## Go

Run:

```bash
go test ./...
```

---

# Error Handling

Implemented:
- Kafka producer exception handling
- MongoDB connection validation
- Redis validation
- Invalid URL handling
- Mock vendor FAIL responses
- Graceful consumer logging

---


# Demonstration Flow

## Step 1
Start Docker services.

```bash
docker compose up -d
```

## Step 2
Run Go service.

```bash
go run main.go
```

## Step 3
Run Java service.

```bash
mvn spring-boot:run
```

## Step 4
Send SMS request.

## Step 5
Observe Kafka consumer logs in Go terminal.

## Step 6
Fetch stored SMS history.

---
