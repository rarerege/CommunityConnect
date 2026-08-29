# CommunityConnect

A desktop resource-matching app for community organizations. Suppliers list
donated resources (food, clothing, medical supplies, etc.), customers request
what they need, and admins moderate the whole flow — built with Java Swing
and MySQL.

## Features

- Role-based login (Admin / Supplier / Customer) with dedicated dashboards
- Suppliers manage their own resource listings (add, edit, delete, track quantity)
- Customers search/filter available resources and submit requests
- Admins manage users, all resources, and request approvals with live stats
- Request lifecycle: `pending` → `approved` / `rejected` → `fulfilled` / `cancelled`

## Tech Stack

- Java (Swing for the UI, JDBC for data access)
- MySQL 8
- MySQL Connector/J (JDBC driver)

## Project Structure

```
Main.java               Entry point
LoginGUI.java            Login screen
AdminDashboard.java       Admin: users, resources, requests, stats
SupplierDashboard.java     Supplier: manage own resources
CustomerDashboard.java      Customer: browse resources, make requests
DatabaseConnection.java   JDBC connection management
User.java / UserDAO.java  User model + data access
Resource.java / ResourceDAO.java  Resource model + data access
Request.java / RequestDAO.java   Request model + data access
ValidationUtil.java       Form validation helpers
SecurityUtil.java         Password hashing helper
resources/schema.sql      Database schema + demo seed data
resources/database.properties.example  DB connection template
```

## Getting Started

### Prerequisites

- JDK 17+
- A running MySQL 8 server
- [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/) jar

### 1. Set up the database

```sh
mysql -u root -p < resources/schema.sql
```

This creates the `communityconnect` database, its tables, and seeds a few
demo accounts, categories, and resources.

### 2. Configure the connection

```sh
cp resources/database.properties.example resources/database.properties
```

Edit `resources/database.properties` with your MySQL host, username, and
password. This file is gitignored — never commit real credentials.

### 3. Compile and run

```sh
javac -d out -cp path/to/mysql-connector-j.jar *.java
java -cp "out;resources;path/to/mysql-connector-j.jar" communityconnect.Main
```

(On macOS/Linux, use `:` instead of `;` as the classpath separator.)

### Demo accounts

| Role     | Username   | Password      |
|----------|-----------|---------------|
| Admin    | admin     | password123   |
| Supplier | redcross  | password123   |
| Supplier | foodbank  | password123   |
| Customer | customer1 | password123   |

## License

MIT
