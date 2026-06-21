# Manufacturing ERP System

A comprehensive Enterprise Resource Planning (ERP) system designed for manufacturing operations. This full-stack application provides robust user management, granular authority-based permissions, and a modern, data-dense interface tailored for high-efficiency enterprise environments.

## Features

- **Granular Authority & Permission System:** Module-specific role-based access control (RBAC) supporting fine-grained permissions (e.g., specific Edit/Delete access per module).
- **Modern User Interface:** A professional, enterprise-grade React frontend built with Vite, emphasizing high information density, command-bar driven workflows, and responsive data visualizations.
- **Secure Backend Services:** Powered by Spring Boot 3 with JWT-based authentication and secure endpoints.
- **Oracle Database Integration:** Robust data persistence using Oracle Database, complete with Oracle Wallet integration for secure cloud connectivity.
- **API Documentation:** Integrated Swagger UI/OpenAPI for easy exploration and testing of backend RESTful APIs.

## Tech Stack

**Frontend:**
- React 19
- Vite
- Lucide React (Icons)
- Recharts (Data Visualization)

**Backend:**
- Java 17
- Spring Boot (Web, Data JPA, Security, Validation)
- Oracle Database (ojdbc11)
- Oracle Wallet (oraclepki, osdt_core, osdt_cert)
- JSON Web Tokens (jjwt)
- Lombok
- ModelMapper
- SpringDoc OpenAPI (Swagger)

## Getting Started

### Prerequisites
- [Java 17](https://jdk.java.net/17/)
- [Node.js](https://nodejs.org/) (v18 or higher recommended)
- [Maven](https://maven.apache.org/)
- Oracle Database instance (with Oracle Wallet for secure connection)

### Backend Setup

1. **Configure Environment Variables:**
   Review `.env.example` and create a `.env` file with your specific database credentials, JWT secrets, and other configuration parameters.

2. **Database Setup:**
   Please refer to `DATABASE_SETUP.md` for detailed instructions on configuring the Oracle Database and setting up the Oracle Wallet (`/wallet` directory).

3. **Run the Spring Boot Application:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
   The backend API will be available at `http://localhost:8080`.
   Swagger UI documentation can be accessed at `http://localhost:8080/swagger-ui/index.html`.

### Frontend Setup

1. **Navigate to the frontend directory:**
   ```bash
   cd frontend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Run the development server:**
   ```bash
   npm run dev
   ```
   The frontend will be available at `http://localhost:5173`.

## Architecture Overview

The system architecture follows a standard client-server model:
- **Client:** React SPA providing an interactive and responsive user experience.
- **Server:** Spring Boot application exposing RESTful APIs, handling business logic, data validation, and security (JWT).
- **Database:** Relational data storage utilizing Oracle DB with strict referential integrity and optimized mapping using Lombok-based POJOs and ModelMapper.
