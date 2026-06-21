# Database Setup

This project uses Oracle and keeps Hibernate schema generation disabled:

```properties
spring.jpa.hibernate.ddl-auto=none
```

Before running the backend against a fresh schema, run the manual SQL files in this order as the application schema owner:

1. `src/main/resources/db/manual/phase1_stabilization_security.sql`
2. `src/main/resources/db/manual/phase2_production_readiness.sql`
3. `src/main/resources/db/manual/phase3_dynamic_configuration.sql`
4. `src/main/resources/db/manual/phase4_notifications_reporting_accounting.sql`

Required environment values are shown in `.env.example`. At minimum, set:

```properties
DB_URL=jdbc:oracle:thin:@localhost:1521/XEPDB1
DB_USERNAME=your_oracle_username
DB_PASSWORD=your_oracle_password
JWT_SECRET=change-this-to-a-long-secure-secret-at-least-32-chars
```

Notifications and general ledger features require phase 4. If the notification bell loads with API errors, confirm that the `notification` table exists and that the logged-in user has the `ADMIN` or `MANAGER` role.
