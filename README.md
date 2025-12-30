E-Wallet Backend System – Project Documentation
1. Project Overview

E-Wallet is a backend-only, microservice-based digital wallet system that allows users to:

Get onboarded with an initial wallet balance

Transfer money to other users

Maintain wallet balance consistency

View transaction history

Receive email notifications for important events

The system is built using Spring Boot, Kafka, MySQL, and Spring Security, following event-driven and service-to-service secure communication principles.

2. High-Level Architecture

The system is split into four independent microservices:

User Service

Wallet Service

Transaction (Txn) Service

Notification Service

Each service has:

Its own database (logical separation)

Clear responsibility

Kafka-based asynchronous communication

3. Services & Responsibilities
3.1 User Service

Responsibilities

User onboarding

Authentication & authorization

Providing user details to other services securely

Key Features

Users are created with roles: USER, ADMIN, SERVICE

Passwords stored using BCrypt

Implements UserDetailsService

Exposes a protected API to fetch user details

Important APIs

POST /user/addUser → onboard user

GET /user/userDetails?contact={contact} → fetch user details
🔒 Accessible only to SERVICE / ADMIN roles

Security

Uses Spring Security

Role-based access control

Only trusted services (like Txn Service) can call internal APIs

3.2 Wallet Service

Responsibilities

Maintain wallet balance

Credit initial balance on user creation

Debit/credit balance during transactions

Key Features

Wallet is automatically created when a user is onboarded

Initial balance (₹20/₹50) is configurable

Balance updates are atomic and validated

Kafka Topics Consumed

USER_CREATED_TOPIC

TXN_INITIATED_TOPIC

Kafka Topics Produced

WALLET_CREATED_TOPIC

TXN_UPDATED_TOPIC

3.3 Transaction (Txn) Service

Responsibilities

Initiate transactions

Validate users before transaction

Persist transaction state

Publish transaction events

Key Features

Supports money transfer between users

Handles transaction lifecycle: INITIATED → SUCCESS / FAILED

Uses service-to-service authentication

API

POST /txn/initTxn

Transaction Flow

Authenticate caller (Basic Auth)

Fetch user details from User Service

Create transaction with INITIATED status

Publish Kafka event

3.4 Notification Service

Responsibilities

Send email notifications

React to system events asynchronously

Kafka Topics Consumed

USER_CREATED_TOPIC

TXN_UPDATED_TOPIC

Notifications

User onboarding email

Transaction success / failure email

Tech Used

Spring Kafka

JavaMailSender (Mailtrap / SMTP)

4. Event-Driven Architecture (Kafka)

Kafka is used to decouple services and enable asynchronous processing.

Main Kafka Topics
Topic	Producer	Consumer
USER_CREATED_TOPIC	User Service	Wallet Service, Notification Service
WALLET_CREATED_TOPIC	Wallet Service	Notification Service
TXN_INITIATED_TOPIC	Txn Service	Wallet Service
TXN_UPDATED_TOPIC	Wallet Service	Notification Service
Why Kafka?

Loose coupling

Scalability

Fault tolerance

Async processing

Better user experience

5. End-to-End Business Flows
5.1 User Onboarding Flow

Client calls User Service

User is saved in DB

User Service publishes USER_CREATED_TOPIC

Wallet Service consumes event and:

Creates wallet

Credits initial balance

Wallet Service publishes WALLET_CREATED_TOPIC

Notification Service sends welcome email

✔ User onboarded successfully with wallet balance

5.2 Money Transfer Flow

Client calls Txn Service

Txn Service authenticates user

Txn Service calls User Service internally to validate user

Transaction saved as INITIATED

Txn Service publishes TXN_INITIATED_TOPIC

Wallet Service validates:

Sender wallet

Receiver wallet

Sufficient balance

Wallet balances updated

Wallet Service publishes TXN_UPDATED_TOPIC

Notification Service sends email

✔ Transaction completed asynchronously


✔ Prevents unauthorized access
✔ Only trusted internal services can fetch user data

7. Tech Stack
Category	Technology
Language	Java 17
Framework	Spring Boot
Security	Spring Security
Messaging	Apache Kafka
Database	MySQL
ORM	Hibernate / JPA
Serialization	Jackson
Email	JavaMailSender
Containerization	Docker
