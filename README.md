# 🚀 SmartFleet — Fleet Management Backend

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Microservices](https://img.shields.io/badge/Architecture-Microservices-blue.svg)](https://microservices.io/)


**SmartFleet** is a production-grade Fleet Management Backend System built using **Java 17** and **Spring Boot 3**. Designed with a fully scalable **microservices architecture**, it features enterprise-level components including API Gateway, Eureka Service Discovery, event-driven tracking, Redis caching, Kafka streaming, and JWT authentication.

This system efficiently manages vehicles, drivers, trips, real-time tracking, alerts, and analytics through independent, loosely-coupled services.

---

## 📌 Features

### 🧩 Microservices (7 Total)

| Service | Responsibilities |
|---------|-----------------|
| **Fleet-Service** | Vehicle CRUD, availability management |
| **Driver-Service** | Driver CRUD, status tracking |
| **Trip-Service** | Assign vehicle + driver, manage trip lifecycle |
| **Tracking-Service** | GPS simulation, Redis caching, Kafka event publishing |
| **Alert-Service** | Route deviation, delay detection, alert generation |
| **Analytics-Service** | Fleet-wide analytics, summary metrics |
| **Auth-Service** | JWT authentication & role-based access control |

### 🛰️ Infrastructure Services

#### 1. **Eureka Discovery Server**
- Central service registry for all microservices
- Auto-registration and health monitoring
- Enables dynamic routing without hardcoded URLs

#### 2. **API Gateway** (Spring Cloud Gateway)
Acts as the single entry point for all client requests.

**Responsibilities:**
- Intelligent routing and load balancing
- JWT validation and security
- CORS handling and request filtering
- Service discovery integration


**Sample Route Configuration:**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: fleet-service
          uri: lb://fleet-service
          predicates:
            - Path=/fleet/**
        - id: trip-service
          uri: lb://trip-service
          predicates:
            - Path=/trip/**
```

---

## 🏗️ Architecture Overview

```
                     ┌─────────────────────────┐
                     │      API Gateway        │
                     │    (Global Routing)     │
                     └─────────────┬───────────┘
                                   │
                     ┌─────────────▼─────────────┐
                     │    Eureka Discovery       │
                     │   (Service Registry)      │
                     └─────────────┬─────────────┘
      ┌────────────────────────────┼────────────────────────────┐
      │                            │                            │
┌─────▼──────┐             ┌───────▼───────┐             ┌──────▼──────┐
│ Fleet      │             │ Driver        │             │ Trip         │
│ Service    │             │ Service       │             │ Service      │
└────────────┘             └───────────────┘             └───────┬──────┘
                                                                   │
                                                           ┌───────▼────────┐
                                                           │ Tracking        │
                                                           │ Service         │
                                                           └───────┬────────┘
                                                                 Kafka
                                                           ┌───────┴────────┐
                                                           │  Alert Service  │
                                                           │ Analytics Svc   │
                                                           └────────┬────────┘
                                                                   Redis
```

---

## ⚙️ Tech Stack

| Category | Tools |
|----------|-------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3 |
| **Architecture** | Microservices, REST APIs, OpenFeign |
| **Databases** | MySQL (database per service pattern) |
| **Cache** | Redis |
| **Event Streaming** | Apache Kafka + Zookeeper |
| **Service Discovery** | Netflix Eureka |
| **API Gateway** | Spring Cloud Gateway |
| **Security** | Spring Security + JWT |
| **Build Tool** | Maven |
| **Containerization** | Docker & Docker Compose |

---

## 🗂️ Repository Structure

```
smartfleet/
 ├── api-gateway/              # Spring Cloud Gateway
 ├── eureka-server/            # Service Discovery
 ├── fleet-service/            # Vehicle management
 ├── driver-service/           # Driver management
 ├── trip-service/             # Trip coordination
 ├── tracking-service/         # Real-time GPS tracking
 ├── alert-service/            # Alert generation
 ├── analytics-service/        # Business analytics
 ├── auth-service/             # Authentication & authorization
 ├── docker-compose.yml        # Multi-container orchestration
 ├── postman/                  # API collections
 ├── docs/                     # Documentation & diagrams
 └── .env.example              # Environment template
```

---

## ⚡ Quick Start

### Prerequisites
- Docker & Docker Compose installed
- Git
- 8GB RAM recommended

### 1. Clone Repository
```bash
git clone https://github.com/yourusername/smartfleet.git
cd smartfleet
```

### 2. Configure Environment Variables
```bash
cp .env.example .env
```

**Required fields:**
- `MYSQL_ROOT_PASSWORD`
- `JWT_SECRET`
- Service ports (optional)

### 3. Start All Services
```bash
docker-compose up --build
```

This command starts:
- 7 microservices
- Eureka Server
- API Gateway
- Kafka + Zookeeper
- Redis
- MySQL instances


## 📘 API Documentation

### Fleet-Service
- `POST /fleet/add` - Add new vehicle
- `GET /fleet/all` - List all vehicles
- `PUT /fleet/{id}` - Update vehicle
- `DELETE /fleet/{id}` - Remove vehicle

### Driver-Service
- `POST /driver/add` - Add new driver
- `GET /driver/all` - List all drivers
- `PUT /driver/{id}` - Update driver
- `DELETE /driver/{id}` - Remove driver

### Trip-Service
- `POST /trip/create` - Create new trip
- `GET /trip/all` - List all trips

### Tracking-Service
- `POST /tracking/update` - Submit GPS coordinates

### Alert-Service
- `GET /alert/all` - Retrieve all alerts

### Analytics-Service
- `GET /analytics/summary` - Fleet analytics dashboard

### Auth-Service
- `POST /auth/register` - User registration
- `POST /auth/login` - User authentication

> **📬 Full Postman Collection:** Available at `/postman/SmartFleet.postman_collection.json`

---

## 🧪 Demo Flow

### Complete Workflow: Auth → Create Trip → Tracking → Alerts → Analytics

#### 1. Register User
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin","role":"ADMIN"}'
```

#### 2. Login & Capture Token
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}' \
  | jq -r '.token')
```

#### 3. Create Vehicle
```bash
curl -X POST http://localhost:8080/fleet/add \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"vehicleNumber":"MH12AB1234","type":"Truck"}'
```

#### 4. Create Driver
```bash
curl -X POST http://localhost:8080/driver/add \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Rohit","licenseNumber":"DL1234"}'
```

#### 5. Create Trip
```bash
curl -X POST http://localhost:8080/trip/create \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"tripName":"Pune-Mumbai","vehicleId":1,"driverId":1,"source":"Pune","destination":"Mumbai"}'
```

#### 6. Send Tracking Update
```bash
curl -X POST http://localhost:8080/tracking/update \
  -H "Content-Type: application/json" \
  -d '{"tripId":1,"latitude":18.5204,"longitude":73.8567}'
```

**Event Flow:** Tracking Service → Kafka → Alert Service → Analytics Service

---

## 📈 Sample Analytics Output

```json
{
  "totalTrips": 42,
  "avgTripDuration": "3h 55m",
  "onTimeDeliveryRate": "91%",
  "delayedTrips": 2
}
```

---

## 🎯 Project Goals Achieved

✅ Full microservices architecture with API Gateway  
✅ Dynamic service discovery via Eureka  
✅ Real-time tracking using Kafka + Redis  
✅ Secure, role-based access via Spring Security + JWT  
✅ Analytics & alerting engine  
✅ Dockerized deployment of entire ecosystem  
✅ Production-style architecture for learning & interviews  

---


## ⭐ Author

**Avadhut Bhosale**  
Backend Developer — Java | Spring Boot | Microservices


<div align="center">
  <p>If you found this project helpful, please consider giving it a ⭐️</p>
  <p>Made with ❤️ for the developer community</p>
</div>
