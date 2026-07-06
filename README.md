# ByteForge PC Store - E-Commerce Platform

Welcome to **ByteForge PC Store**, a premium, high-end e-commerce system built for PC enthusiasts, builders, and gamers. The application consists of a responsive Vue 3 frontend and a robust Spring Boot REST API backed by MySQL.

---

## 🛠️ Technology Stack & Architecture

This application leverages a modern client-server architecture:

### 🔙 Backend (Spring Boot REST API)
* **Java Development Kit (JDK):** Java 8 / 11 / 17 compatible.
* **Spring Boot (2.7.7):** Core framework for dependency injection, bean management, and startup configuration.
* **Spring MVC (Web):** Implements RESTful HTTP request mapping controllers (`GET`, `POST`, `PUT`, `DELETE`).
* **Spring Data JPA & Hibernate:** Object-Relational Mapping (ORM) to interact with the database. Automatically creates/updates the database schema mapping DDL.
* **Spring Boot DevTools:** Enabled for automatic application context restarts during active development.
* **Lombok:** Eliminates boilerplate code (automatically generating getters, setters, constructors, toString, equals, and hashCode methods via annotations).
* **JWT (JSON Web Token):** Handles secure authentication token signing and validation.

### 🔜 Frontend (Vue 3 Single Page Application)
* **HTML5 & CSS3:** Semantic content structure and custom styles.
* **Bootstrap 5:** Fluid grid systems, layout rows/columns, table styling, and dynamic input aesthetics.
* **Vue.js 3:** JavaScript framework utilizing components, reactive data states, watchers, and lifecycle hooks.
* **Vue Router:** Client-side routing management, including navigation guards to prevent unauthorized access (e.g. blocking admins from cart routes).
* **Axios:** Promise-based HTTP client used to interact with the Spring Boot backend, sending headers and payloads.
* **npm / Node.js:** Dependency management and bundling compiler.

### 🗄️ Database & Storage
* **MySQL:** Relational Database Management System (RDBMS) for persistent data storage.
* **Static File System:** Multi-part uploaded files are saved locally on the server filesystem within an `/uploads/` directory, which is served statically to the client browser.

---

## 🌟 Key Features

### 🖥️ 1. Rebranded E-Commerce Experience
* Custom PC hardware shop aesthetics (banners, components, curated space-dark layouts).
* Dynamic homepage carousel, search, and category navigation.

### 🛡️ 2. Admin Shopping Restrictions
* Administrators (`ADMIN` role) are strictly restricted from performing shopping actions.
* Shopping cart indicators and checkout forms are hidden, quantity selection is disabled, and route guards intercept manual `/cart` path queries.

### 📊 3. Unified Admin Control Center (`/admin/dashboard`)
* **Deliveries / Orders:** View all user orders, items, and status indicators. Update statuses (Pending, Shipped, Delivered, Cancelled) in real-time.
* **Registered Users Directory:** Lists all usernames, role badges, email accounts, and contact numbers.
* **Category & Product Catalog Management:**
  - Add, edit, or delete categories and products.
  - Upload local images via automated multipart uploads.

### ✉️ 4. Support Messages & Tickets (eBay Style)
* **Customer Inbox:** Customers can log into their profile to click **"Report a Problem"** to submit a support request. They can view a scrollable inbox list containing ticket states and admin replies.
* **Admin Reply Desk:** Admins can view customer support tickets from the Admin Dashboard, review sender name/email/phone, and submit replies.

### 👤 5. Editable Profile Dashboards
* **Local Avatar Upload:** Hovering over the circular profile photo reveals a camera icon overlay. Users can upload a custom avatar file, which automatically updates the database and links to their profile.
* **Inline Info Editor:** Users can click to toggle edit modes to update their Full Name, Email Address, Phone Number, and Shipping Address in real-time.

---

## 📂 Project Structure

```
├── backend/                  # Spring Boot Maven Project
│   ├── src/main/java/        # Java Source Files
│   │   ├── config/           # Security, Web and Data Seeder configurations
│   │   ├── controller/       # Rest API Controllers (UserController, FileController, MessageController, etc.)
│   │   ├── model/            # JPA Entities (User, Category, Product, Message, etc.)
│   │   └── service/          # Business Logic Services
│   ├── src/main/resources/   # Application properties
│   └── uploads/              # Local image storage folder
│
└── frontend/                 # Vue 3 Frontend Project
    ├── src/
    │   ├── components/       # Shared UI components (ProductBox, Navbar, etc.)
    │   ├── router/           # Routing and Admin Route Guards
    │   ├── utils/            # Authentication & API wrappers
    │   └── views/            # Screen views (ProfileView, AdminDashboard, EditCategory, etc.)
    └── package.json
```

---

## 🚀 How to Run the Application

### 📋 Prerequisites
Ensure you have the following installed on your machine:
* **Java JDK 8 or 17+**
* **Node.js (version 16 or newer)** and **npm**
* **MySQL Server** running locally on port 3306

---

### 1. Database Setup
1. Open your MySQL command-line shell or database GUI client (e.g. MySQL Workbench, phpMyAdmin, DBeaver).
2. Create a new database schema named `ecommerce`:
   ```sql
   CREATE DATABASE ecommerce;
   ```
3. Update the database credentials in the backend configurations file: `backend/src/main/resources/application.properties`
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce
   spring.datasource.username=YOUR_MYSQL_USERNAME
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```

---

### 2. Start the Backend (Spring Boot)

#### Option A: Running via IDE (Recommended)
1. Open your IDE (IntelliJ IDEA, Eclipse, or VS Code).
2. Import the `backend` folder as a **Maven project**.
3. Let the IDE download the dependencies mapped in `pom.xml`.
4. Locate the main application class `BackendApplication.java` inside `src/main/java/com/example/backend/`.
5. Right-click the file and select **Run BackendApplication**.
6. The server will launch on port **`http://localhost:8000`**.

#### Option B: Running via Terminal
1. Open your command terminal and navigate into the `backend` directory.
2. Build and run the project using the Maven Wrapper:
   ```bash
   # On Windows (cmd/PowerShell):
   mvnw spring-boot:run

   # On Linux/macOS:
   ./mvnw spring-boot:run
   ```

> [!NOTE]
> * **Schema Auto-Update:** Hibernate automatically executes DDL scripts on startup to add or update required database tables and columns (e.g., adding `PROFILE_IMAGE_URL`, `PHONE_NUMBER`, etc.).
> * **Automatic Seeder:** The first time the backend starts, the seeder automatically populates the database with **1 Admin**, **10 Customers**, **15 Categories**, and **100 Products** (downloading/caching catalog images locally).

---

### 3. Start the Frontend (Vue 3)
1. Open your command terminal and navigate into the `frontend` directory.
2. Install the node package dependencies:
   ```bash
   npm install
   ```
3. Start the local webpack/vite development compile server:
   ```bash
   npm run serve
   ```
4. The frontend application will compile and start on **`http://localhost:8081`**.
5. Open your browser and navigate to `http://localhost:8081` to view the store.

---

## 🧪 Testing Account Details

Use these pre-seeded accounts to explore the platform:

* **Administrator Dashboard Access:**
  - **Username:** `admin`
  - **Password:** `admin`
* **Customer Shopping Access:**
  - **Username:** `cus01` (can use `cus01` through `cus10`)
  - **Password:** `password`
