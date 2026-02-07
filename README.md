# AI Powered Vibe Coding SaaS

A production-grade SaaS backend platform that provides AI-powered coding assistance with real-time chat streaming, project-based collaboration, and subscription management.

## Overview

This platform enables teams to leverage AI for coding assistance within isolated project environments. It features role-based access control, real-time AI interactions, comprehensive usage tracking, and integrated payment processing.

### Running the Backend Application
```bash
# Clone the repository
git clone https://github.com/HarshilChampaneri/AI-Powered-Vibe-Coding-SaaS-Platform
cd AI-Powered-Vibe-Coding-SaaS-Platform

# Install dependencies
mvn clean install

# Run the application
mvn spring-boot:run
```

## Running the Frontend Application
```bash
# Clone the repository
git clone https://github.com/HarshilChampaneri/AI-Powered-Vibe-Coding-SaaS-Platform-Frontend
cd project-companion
# Install dependencies
npm install

# Run the application
npm run dev
```

## Key Features

### Project & Access Management
- **Project-based Isolation**: Each project operates in its own secure environment
- **Role-based Access Control**: Three-tier permission system
  - `OWNER`: Full project control and management
  - `EDITOR`: Can modify files and interact with AI
  - `VIEWER`: Read-only access to project resources
- **Member Management**: Invite, remove, and manage team members per project

### AI Integration
- **Real-time AI Chat Streaming**: Live AI responses using Server-Sent Events (SSE)
- **Chat History Tracking**: Complete conversation history per project
- **Token Usage Monitoring**: Track AI API consumption in real-time
- **File-level Context**: AI responses based on project file context
- **Event-driven Architecture**: Scalable AI request processing

### Payment & Subscriptions
- **Stripe Integration**: Complete payment processing pipeline
- **Subscription Management**: Multiple pricing tiers with different limits
- **Customer Portal**: Self-service billing management
- **Webhook Handling**: Real-time subscription event processing
- **Plan Enforcement**: Automatic enforcement of:
  - Maximum projects per subscription
  - Daily token usage limits

### API & Documentation
- **RESTful API Design**: Production-grade endpoints following REST principles
- **OpenAPI/Swagger Documentation**: Interactive API documentation
- **Comprehensive Endpoints**:
  - Project management
  - File system operations
  - Member administration
  - Usage tracking
  - Billing operations

## Technical Architecture

### Technology Stack
- **Framework**: Spring Boot
- **Web Layer**: Spring Web
- **API Specification**: OpenAPI 3.0
- **Authentication**: JWT (JSON Web Tokens)
- **Real-time Communication**: Server-Sent Events (SSE)
- **Payment Processing**: Stripe API
- **Architecture Pattern**: Clean Layered Architecture

### Architecture Layers
```
┌─────────────────────────────────────┐
│     Controller Layer (REST API)     │
├─────────────────────────────────────┤
│       Service Layer (Business)      │
├─────────────────────────────────────┤
│    Repository Layer (Data Access)   │
├─────────────────────────────────────┤
│         Database Layer              │
└─────────────────────────────────────┘
```

## Core Modules

### Authentication & Authorization
- JWT-based authentication system
- Token generation and validation
- Role-based authorization middleware
- Secure password management

### Project Management
- CRUD operations for projects
- Project isolation and data segregation
- File system management within projects
- Project-level settings and configuration

### AI Chat System
- Real-time streaming responses via SSE
- Context-aware AI interactions
- Chat session management
- Message history persistence

### Usage Tracking
- Token consumption monitoring
- Daily usage aggregation
- Per-project analytics
- Rate limiting enforcement

### Billing & Subscriptions
- Stripe checkout session creation
- Subscription lifecycle management
- Customer portal generation
- Webhook event processing
- Plan limit enforcement

## Security Features

- **JWT Authentication**: Stateless token-based authentication
- **Role-based Authorization**: Fine-grained access control
- **Project Isolation**: Data segregation between projects
- **API Rate Limiting**: Protection against abuse
- **Webhook Signature Verification**: Secure Stripe event handling
- **Input Validation**: Request payload validation
- **CORS Configuration**: Cross-origin request management

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL/MySQL database
- Stripe account for payment processing
- OpenAI API key (or compatible AI service)

### API Documentation
Once running, access the Swagger UI at:
```
http://localhost:9090/swagger-ui.html
```

## Event-Driven Architecture
The system uses an event-driven approach for:
- AI request processing
- Usage tracking updates
- Subscription status changes
- Real-time notifications

## Best Practices Implemented
- **Clean Code**: SOLID principles and design patterns
- **Layered Architecture**: Separation of concerns
- **Error Handling**: Comprehensive exception management
- **Logging**: Structured logging for monitoring
- **Testing**: Unit and integration tests
- **Documentation**: OpenAPI/Swagger specifications
- **Version Control**: Git with semantic versioning

---

Built with ❤️ using Spring Boot and modern development practices
