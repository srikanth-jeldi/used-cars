
Overview
The Carverse Chat Service is a microservice built using Java 17, Spring Boot, Hibernate (for ORM), and MySQL as the backend database. It handles real-time messaging between buyers and sellers on the Carverse platform, enabling secure, efficient chat conversations for car listings. This service integrates with other microservices (e.g., notification-service) via REST calls and uses Server-Sent Events (SSE) for real-time updates like unread message badges.
Key focus: Scalable microservice architecture with JWT-based security, Flyway for database migrations, and Eureka for service discovery.
Features

Send and Receive Messages: Users can send messages in conversations, with automatic timestamping and read status tracking.
Conversation Management: Fetch messages by conversation ID, mark conversations as read.
Unread Counts: Get total unread messages or per-conversation counts for inbox badges.
Inbox View: Retrieve a user's inbox with last message previews, timestamps, and unread counts.
Real-Time Updates: SSE for pushing unread badge updates without polling.
Integration: Fires notifications to a separate service (e.g., for email/SMS alerts) on new messages.
Security: JWT authentication for all chat endpoints, with token validation and user ID extraction.
Error Handling: Global exception handling for validation, access denied, and generic errors.
Database: MySQL with optimized indexes for fast queries on conversations and unread counts.
Health Monitoring: Actuator endpoints for health and info.

Technologies Used

Language: Java 17
Framework: Spring Boot 3.2.5 (for microservices architecture)
ORM: Hibernate (via Spring Data JPA)
Database: MySQL (with Flyway for schema migrations)
Security: Spring Security + JWT (using JJWT library)
Real-Time: Server-Sent Events (SSE) with concurrent-safe emitter management
Service Discovery: Eureka Client
REST Client: RestTemplate for inter-service communication
Validation: Jakarta Validation for request payloads
Build Tool: Maven
Other: Lombok for boilerplate reduction, ConcurrentHashMap for thread-safe SSE handling

Project Structure
textchat-service/
├── src/
│   ├── main/
│   │   ├── java/com/epitomehub/carverse/chatservice/
│   │   │   ├── config/          # Configurations (e.g., RestClientConfig)
│   │   │   ├── controller/      # REST controllers (e.g., ChatController)
│   │   │   ├── dto/            # Data Transfer Objects (e.g., ChatMessageResponse, SendMessageRequest)
│   │   │   ├── entity/         # JPA Entities (e.g., ChatMessage)
│   │   │   ├── exception/      # Custom exceptions and handlers (e.g., GlobalExceptionHandler)
│   │   │   ├── integration/    # External service clients (e.g., NotificationClient)
│   │   │   ├── repository/     # JPA Repositories (e.g., ChatMessageRepository with custom queries)
│   │   │   ├── securityఅంతరాయం కలిగిందిbro not only chat services i have few other services alsoCarverse Backend
Overview
Carverse Backend is a scalable, microservices-based platform built using Java 17, Spring Boot, Hibernate (for ORM), and MySQL as the primary database. It powers the Carverse application, a car marketplace where users can buy, sell, and interact with car listings. The architecture follows a microservices design, with each service handling specific domain responsibilities such as user management, car listings, messaging, notifications, and service discovery. Services communicate via REST APIs, and the system uses Eureka for service registration/discovery, JWT for security, and Flyway for database migrations.
This backend emphasizes modularity, security, and real-time features (e.g., via SSE in chat service). It integrates with external services for notifications (email/SMS) and is designed for containerization (e.g., Docker) and cloud deployment.
Features

Microservices Architecture: Independent services for chats, notifications, user management, car listings, etc., allowing independent scaling and deployment.
Real-Time Messaging: Buyer-seller chat with unread counts, inbox views, and SSE for live updates.
Notifications: Asynchronous email/SMS alerts for new messages or events, integrated via REST calls.
Security: JWT-based authentication and authorization across services.
Database Management: MySQL with Hibernate JPA for CRUD operations, custom queries for efficiency (e.g., unread counts), and Flyway for schema versioning.
Service Discovery: Eureka Server for registering and discovering microservices.
Error Handling & Monitoring: Global exception handlers, Actuator for health checks, and logging.
Integration: REST clients for inter-service communication, with fire-and-forget patterns for non-critical ops like notifications.

Tech Stack

Language: Java 17
Framework: Spring Boot 3.2.5 (Web, Data JPA, Validation, Actuator, Security)
ORM/Database: Hibernate (Spring Data JPA) + MySQL
Migrations: Flyway
Security: Spring Security + JWT (JJWT library)
Real-Time: Server-Sent Events (SSE)
Service Discovery: Netflix Eureka
Build Tool: Maven (multi-module project)
Other: Lombok for code reduction, RestTemplate for HTTP clients, Concurrent data structures for thread safety

Project Structure
The backend is a multi-module Maven project under carverse-backend:
textcarverse-backend/
├── pom.xml                  # Parent POM
├── chat-service/            # Chat microservice (detailed below)
├── notification-service/    # Notification handling (email/SMS)
├── discovery-service/       # Eureka Server for service discovery
├── user-service/            # User authentication, profiles (assumed based on context)
├── car-service/             # Car listings, inventory (assumed based on context)
├── ...                      # Other services as needed
└── docker/                  # Dockerfiles and compose files
Chat Service Details
(From provided code – expand similarly for other services)

Package: com.epitomehub.carverse.chatservice
Key Components:
Entities: ChatMessage (with fields like conversationId, senderId, receiverId, message, isRead, createdAt)
Repositories: ChatMessageRepository (JPA with custom queries for unread counts, inbox, marking read)
Services: ChatServiceImpl (handles sending messages, fetching, unread counts; integrates with SSE and notifications)
Controllers: ChatController (REST endpoints for send/receive messages, unread, inbox, SSE subscribe)
Security: JwtAuthenticationFilter, JwtService (token validation, userId extraction)
SSE: SseHub (manages emitters for real-time unread badges)
Integration: NotificationClient (calls notification-service for alerts)
Config: Application YAML/Docker YAML for ports, DB URLs, Eureka, JWT secrets
Migrations: Flyway scripts (e.g., V1__create_chat_messages.sql for table creation with indexes)

Endpoints (under /api/chats):
POST /messages: Send message
GET /{conversationId}/messages: Get messages
PATCH /{conversationId}/read: Mark as read
GET /unread-count: Total unread
GET /unread-count/by-conversation: Per-conversation unread
GET /inbox: Inbox items
GET /sse: SSE stream for updates


For other services (e.g., notification-service, user-service), provide similar structures when details are available.
Setup & Installation
Prerequisites

Java 17 JDK
Maven 3.8+
MySQL 8.0+ (databases: carverse_chatdb, etc.)
Docker (optional for containerization)

Local Setup

Clone Repository:textgit clone <repo-url>
cd carverse-backend
Build:textmvn clean install
Database Setup:
Create databases (e.g., carverse_chatdb).
Run Flyway migrations via Maven: mvn flyway:migrate -pl chat-service (configure in pom.xml).

Run Services:
Start Eureka: java -jar discovery-service/target/discovery-service.jar
Start Chat Service: java -jar chat-service/target/chat-service.jar
Similarly for others.

Configuration:
Update application.yaml for DB credentials, Eureka URL, JWT secret, notification base URL.


Docker Setup
Use docker-compose.yml (assumed or create one):
textversion: '3'
services:
mysql:
image: mysql:8.0
environment:
MYSQL_ROOT_PASSWORD: root
MYSQL_DATABASE: carverse
eureka:
image: <eureka-image>
ports:
- "8761:8761"
chat-service:
image: <chat-image>
ports:
- "7004:7004"
depends_on:
- mysql
- eureka
environment:
SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/carverse
Run: docker-compose up
Usage

Authentication: Obtain JWT from user-service (assumed). Pass in Authorization: Bearer <token> for protected endpoints.
Testing: Use Postman or curl for APIs. For SSE, connect via browser or tools like curl -H "Accept: text/event-stream".
Monitoring: Access /actuator/health for each service.

Contributing

Follow standard Git flow: feature branches, PRs.
Use Lombok annotations sparingly.
Add tests with Spring Boot Test.

License
Proprietary – © EpitomeHub 2025