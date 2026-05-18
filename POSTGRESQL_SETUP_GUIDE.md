# PostgreSQL Database Setup Guide

This guide will help you set up PostgreSQL database for the Login Microservice Application.

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [PostgreSQL Installation](#postgresql-installation)
3. [Database Setup](#database-setup)
4. [Application Configuration](#application-configuration)
5. [Testing the Connection](#testing-the-connection)
6. [Troubleshooting](#troubleshooting)
7. [Switching Between H2 and PostgreSQL](#switching-between-h2-and-postgresql)

---

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12 or higher

---

## PostgreSQL Installation

### Windows

1. **Download PostgreSQL:**
   - Visit: https://www.postgresql.org/download/windows/
   - Download the installer for Windows

2. **Run the Installer:**
   - Double-click the downloaded installer
   - Follow the installation wizard
   - Set a password for the `postgres` superuser (remember this!)
   - Default port: `5432` (keep this unless you have a conflict)
   - Install pgAdmin 4 (recommended for GUI management)

3. **Verify Installation:**
   ```powershell
   psql --version
   ```

### Linux (Ubuntu/Debian)

```bash
# Update package list
sudo apt update

# Install PostgreSQL
sudo apt install postgresql postgresql-contrib

# Start PostgreSQL service
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Verify installation
psql --version
```

### macOS

```bash
# Using Homebrew
brew install postgresql

# Start PostgreSQL service
brew services start postgresql

# Verify installation
psql --version
```

---

## Database Setup

### Method 1: Using Command Line (psql)

1. **Connect to PostgreSQL:**

   **Windows:**
   ```powershell
   psql -U postgres
   ```

   **Linux/macOS:**
   ```bash
   sudo -u postgres psql
   ```

2. **Create Database:**
   ```sql
   CREATE DATABASE logindb;
   ```

3. **Create User (Optional - for better security):**
   ```sql
   CREATE USER loginapp WITH PASSWORD 'your_secure_password';
   GRANT ALL PRIVILEGES ON DATABASE logindb TO loginapp;
   ```

4. **Verify Database:**
   ```sql
   \l
   ```
   You should see `logindb` in the list.

5. **Exit psql:**
   ```sql
   \q
   ```

### Method 2: Using pgAdmin 4 (GUI)

1. **Open pgAdmin 4**
2. **Connect to PostgreSQL Server:**
   - Right-click on "Servers" → "Create" → "Server"
   - Name: `Local PostgreSQL`
   - Connection tab:
     - Host: `localhost`
     - Port: `5432`
     - Username: `postgres`
     - Password: (your postgres password)

3. **Create Database:**
   - Right-click on "Databases" → "Create" → "Database"
   - Database name: `logindb`
   - Owner: `postgres`
   - Click "Save"

---

## Application Configuration

The application is already configured to use PostgreSQL. Here's what was changed:

### 1. pom.xml
PostgreSQL dependency has been added:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 2. application.properties
PostgreSQL configuration is now active:
```properties
# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/logindb
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

### 3. Update Your Credentials

**IMPORTANT:** Update the database credentials in `application.properties`:

```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

If you created a custom user (e.g., `loginapp`), use those credentials instead of `postgres`.

---

## Testing the Connection

### 1. Build the Application

```bash
cd backend
mvn clean install
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

### 3. Check Logs

Look for these success messages:
```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
Hibernate: create table if not exists users (...)
Started LoginApplication in X.XXX seconds
```

### 4. Verify Tables Created

Connect to PostgreSQL and check:
```sql
\c logindb
\dt
```

You should see tables like:
- `users`
- `candidates`
- `panelists`
- `interviews`
- `chat_messages`
- `email_otp`
- `password_history`

---

## Troubleshooting

### Issue 1: Connection Refused

**Error:**
```
Connection to localhost:5432 refused
```

**Solution:**
- Ensure PostgreSQL service is running:
  ```bash
  # Windows
  services.msc (look for postgresql-x64-XX)
  
  # Linux
  sudo systemctl status postgresql
  sudo systemctl start postgresql
  
  # macOS
  brew services list
  brew services start postgresql
  ```

### Issue 2: Authentication Failed

**Error:**
```
FATAL: password authentication failed for user "postgres"
```

**Solution:**
- Verify your password in `application.properties`
- Reset PostgreSQL password if needed:
  ```bash
  # Linux
  sudo -u postgres psql
  ALTER USER postgres PASSWORD 'new_password';
  ```

### Issue 3: Database Does Not Exist

**Error:**
```
FATAL: database "logindb" does not exist
```

**Solution:**
- Create the database as shown in [Database Setup](#database-setup)

### Issue 4: Port Already in Use

**Error:**
```
Port 5432 is already in use
```

**Solution:**
- Check if another PostgreSQL instance is running
- Or change the port in `application.properties`:
  ```properties
  spring.datasource.url=jdbc:postgresql://localhost:5433/logindb
  ```

### Issue 5: Driver Not Found

**Error:**
```
Cannot load driver class: org.postgresql.Driver
```

**Solution:**
- Rebuild the project:
  ```bash
  mvn clean install -U
  ```

---

## Switching Between H2 and PostgreSQL

### Switch to H2 (In-Memory Database)

1. **Edit `application.properties`:**
   ```properties
   # Comment out PostgreSQL
   #spring.datasource.url=jdbc:postgresql://localhost:5432/logindb
   #spring.datasource.driverClassName=org.postgresql.Driver
   #spring.datasource.username=postgres
   #spring.datasource.password=postgres
   
   # Uncomment H2
   spring.datasource.url=jdbc:h2:mem:logindb
   spring.datasource.driverClassName=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=
   
   # Change dialect
   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
   
   # Enable H2 Console
   spring.h2.console.enabled=true
   spring.h2.console.path=/h2-console
   ```

2. **Restart the application**

### Switch to PostgreSQL

1. **Edit `application.properties`:**
   ```properties
   # Comment out H2
   #spring.datasource.url=jdbc:h2:mem:logindb
   #spring.datasource.driverClassName=org.h2.Driver
   #spring.datasource.username=sa
   #spring.datasource.password=
   
   # Uncomment PostgreSQL
   spring.datasource.url=jdbc:postgresql://localhost:5432/logindb
   spring.datasource.driverClassName=org.postgresql.Driver
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   
   # Change dialect
   spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
   
   # Disable H2 Console
   #spring.h2.console.enabled=true
   #spring.h2.console.path=/h2-console
   ```

2. **Restart the application**

---

## PostgreSQL Useful Commands

### Connect to Database
```bash
psql -U postgres -d logindb
```

### List All Databases
```sql
\l
```

### List All Tables
```sql
\dt
```

### Describe Table Structure
```sql
\d table_name
```

### View Table Data
```sql
SELECT * FROM users;
SELECT * FROM candidates;
SELECT * FROM chat_messages;
```

### Delete All Data (Keep Structure)
```sql
TRUNCATE TABLE users CASCADE;
TRUNCATE TABLE candidates CASCADE;
TRUNCATE TABLE chat_messages CASCADE;
```

### Drop Database (Careful!)
```sql
DROP DATABASE logindb;
```

### Backup Database
```bash
pg_dump -U postgres logindb > backup.sql
```

### Restore Database
```bash
psql -U postgres logindb < backup.sql
```

---

## Production Considerations

### 1. Security

- **Never use default passwords in production**
- Create a dedicated database user with limited privileges
- Use environment variables for credentials:
  ```properties
  spring.datasource.username=${DB_USERNAME:postgres}
  spring.datasource.password=${DB_PASSWORD:postgres}
  ```

### 2. Connection Pooling

The application uses HikariCP (default in Spring Boot). You can tune it:
```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

### 3. Performance

```properties
# Enable query caching
spring.jpa.properties.hibernate.cache.use_second_level_cache=true
spring.jpa.properties.hibernate.cache.region.factory_class=org.hibernate.cache.jcache.JCacheRegionFactory

# Batch processing
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

### 4. Monitoring

```properties
# Enable statistics
spring.jpa.properties.hibernate.generate_statistics=true

# Slow query logging
spring.jpa.properties.hibernate.session.events.log.LOG_QUERIES_SLOWER_THAN_MS=100
```

---

## Data Migration from H2 to PostgreSQL

If you have existing data in H2 and want to migrate:

1. **Export data from H2:**
   - Access H2 Console: http://localhost:8081/h2-console
   - Run: `SCRIPT TO 'backup.sql'`

2. **Convert H2 SQL to PostgreSQL:**
   - H2 and PostgreSQL have some syntax differences
   - Manually adjust the SQL script if needed

3. **Import to PostgreSQL:**
   ```bash
   psql -U postgres -d logindb -f backup.sql
   ```

---

## Support

For issues or questions:
1. Check the [Troubleshooting](#troubleshooting) section
2. Review PostgreSQL logs
3. Check application logs in the console

---

## Summary

✅ PostgreSQL dependency added to `pom.xml`
✅ Application configured to use PostgreSQL
✅ Database connection settings updated
✅ All entities will be automatically created by Hibernate
✅ Data persists across application restarts (unlike H2 in-memory)

**Next Steps:**
1. Install PostgreSQL
2. Create `logindb` database
3. Update credentials in `application.properties`
4. Run the application
5. Test the features

---

Made with Bob 🤖