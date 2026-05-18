# MySQL Database Setup Guide

This guide will help you set up MySQL Workbench for the Login Microservice Application.

## Prerequisites

- MySQL Server 8.0 or higher
- MySQL Workbench (latest version)
- Java 17 or higher
- Maven 3.6 or higher

## Step 1: Install MySQL Server and Workbench

### Windows
1. Download MySQL Installer from [MySQL Downloads](https://dev.mysql.com/downloads/installer/)
2. Run the installer and select "Developer Default" or "Custom"
3. During installation, set the root password to `root` (as configured in application.properties)
4. Complete the installation and ensure MySQL Server is running

### Verify Installation
Open Command Prompt and run:
```bash
mysql --version
```

## Step 2: Configure MySQL Server

1. Open MySQL Workbench
2. Click on "Local instance MySQL80" (or your MySQL connection)
3. Enter password: `root`
4. You should now be connected to MySQL Server

## Step 3: Create Database (Optional)

The application is configured to automatically create the database if it doesn't exist (`createDatabaseIfNotExist=true`).

However, if you want to create it manually:

```sql
CREATE DATABASE IF NOT EXISTS logindb;
USE logindb;
```

## Step 4: Verify Database Configuration

The application is configured with the following settings in `application.properties`:

```properties
# Database Configuration - MySQL (Production)
spring.datasource.url=jdbc:mysql://localhost:3306/logindb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=root

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

## Step 5: Build and Run the Application

1. Navigate to the backend directory:
```bash
cd backend
```

2. Clean and build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

Or run the JAR file:
```bash
java -jar target/login-service-0.0.1-SNAPSHOT.jar
```

## Step 6: Verify Database Tables

After starting the application, Hibernate will automatically create the necessary tables.

To verify in MySQL Workbench:

```sql
USE logindb;
SHOW TABLES;
```

You should see tables like:
- `users`
- `candidates`
- `panelists`
- `interviews`
- `chat_messages`
- `email_otp`
- `password_history`

## Step 7: View Table Data

To view data in any table:

```sql
SELECT * FROM users;
SELECT * FROM candidates;
SELECT * FROM panelists;
```

## Troubleshooting

### Connection Issues

If you encounter connection errors:

1. **Check MySQL Service is Running**
   - Windows: Open Services (services.msc) and ensure "MySQL80" is running
   - Or run: `net start MySQL80`

2. **Verify Port 3306 is Available**
   ```bash
   netstat -ano | findstr :3306
   ```

3. **Check Credentials**
   - Ensure username is `root` and password is `root`
   - If you used a different password during installation, update `application.properties`

4. **Firewall Issues**
   - Ensure Windows Firewall allows MySQL connections on port 3306

### Authentication Issues

If you get "Public Key Retrieval is not allowed" error:
- The connection URL already includes `allowPublicKeyRetrieval=true`
- If still facing issues, try connecting via MySQL Workbench first

### Time Zone Issues

If you encounter timezone errors:
- The connection URL includes `serverTimezone=UTC`
- Alternatively, configure MySQL server timezone:
  ```sql
  SET GLOBAL time_zone = '+00:00';
  ```

## Useful MySQL Workbench Features

### 1. Query Editor
- Write and execute SQL queries
- View results in tabular format

### 2. Schema Inspector
- Right-click on `logindb` schema
- Select "Schema Inspector" to view all tables and their structures

### 3. Data Export/Import
- Right-click on `logindb` schema
- Select "Table Data Export Wizard" or "Table Data Import Wizard"

### 4. Visual Explain
- Analyze query performance
- Click on "Execution Plan" icon in query editor

## Database Backup

To backup your database:

```sql
-- Using MySQL Workbench
-- Server > Data Export > Select logindb > Export to Self-Contained File
```

Or via command line:
```bash
mysqldump -u root -p logindb > logindb_backup.sql
```

To restore:
```bash
mysql -u root -p logindb < logindb_backup.sql
```

## Changing Database Credentials

If you want to use different credentials:

1. Create a new MySQL user:
```sql
CREATE USER 'newuser'@'localhost' IDENTIFIED BY 'newpassword';
GRANT ALL PRIVILEGES ON logindb.* TO 'newuser'@'localhost';
FLUSH PRIVILEGES;
```

2. Update `application.properties`:
```properties
spring.datasource.username=newuser
spring.datasource.password=newpassword
```

## Performance Tuning

For better performance, you can adjust MySQL settings in `my.ini` (Windows) or `my.cnf` (Linux):

```ini
[mysqld]
max_connections=200
innodb_buffer_pool_size=1G
query_cache_size=64M
```

## Next Steps

1. Start the backend application
2. Start the frontend application (see QUICK_START.md)
3. Register a new user and test the application
4. Monitor database tables in MySQL Workbench

## Additional Resources

- [MySQL Documentation](https://dev.mysql.com/doc/)
- [MySQL Workbench Manual](https://dev.mysql.com/doc/workbench/en/)
- [Spring Boot with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)

---

**Made with Bob**