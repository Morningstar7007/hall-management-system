# HallSync

A comprehensive JavaFX application designed to manage university residence halls. This system provides distinct portals for Hall Administrators and Students, facilitating room assignments, meal tracking, payment history, and secure profile management.

---

## Key Features
- **Role-Based Access Control:** Secure login system differentiating between `Admin` and `Student` accounts.
- **Dynamic Room Management:** Admins can create and delete rooms. The system tracks dynamic real-time occupancy.
- **Meal Management System:** Students can turn their meals on/off. Admins can view aggregate meal counts and enforce "Global Meal Overrides" for hall closures.
- **Financial Tracking:** Admins can log student payments, while students can view their payment history.
- **Secure Network Clock:** Dashboard utilizes live internet time fetching to prevent local client clock-tampering for meal deadlines.

---

## Academic Grading Rubric Highlights
This project was specifically architected to demonstrate mastery of advanced Java concepts:

### 1. Advanced OOP Concepts
- **Interfaces & Abstract Classes:** Created the `DashboardNavigation` interface and the `BaseDashboardController` abstract class. All dashboard controllers inherit from this base class, enforcing a strict contract for UI injection while preventing duplicate code.
- **Encapsulation:** Strong use of MVC-like architecture, separating database models (e.g., `Room.java`, `StudentProfile.java`) from UI controllers.

### 2. JavaFX UI Design
- Extensive use of a wide array of JavaFX controls and layouts including `BorderPane`, `StackPane`, `VBox`, `HBox`, `TableView`, `ComboBox`, `DatePicker`, and `PasswordField`.

### 3. Layout Responsiveness
- Implemented **Java Property Constraints** (e.g., `sidebarVBox.prefWidthProperty().bind(...)`) to mathematically scale sidebars and font sizes dynamically relative to the window's height and width.

### 4. Concurrency (Thread Pools)
- Engineered a centralized `ConcurrencyManager` utility.
- Utilized an `ExecutorService` (Fixed Thread Pool) to manage and queue all background database queries and network calls asynchronously, avoiding GUI freezes and dangerous unmanaged thread spawning.

### 5. Database Integration
- Fully integrated with a local **SQLite** database (`hall_management.db`).
- Designed a normalized schema utilizing Primary Keys and Foreign Keys (e.g., bridging `MealRecords` and `Payments` back to the `Users` table). 
- Automated schema initialization via `DatabaseInitializer`.

### 6. Data Manipulation (CRUD)
- Complete Create, Read, Update, and Delete capabilities demonstrated powerfully in the **Manage Students** and **Manage Rooms** administrator modules.

### 7. Networking & Data Parsing (JSON)
- Integrated the `TimeAPI.io` public API to fetch the live, true time in Dhaka.
- Utilized Java `HttpClient` to execute HTTP GET requests.
- Leveraged the `Jackson` library (`ObjectMapper` / `JsonNode`) to securely parse nested JSON payloads and inject the verified network time into the JavaFX dashboard.

---

## How to Run

1. **Prerequisites:** Ensure you have JDK 17 (or higher) installed.
2. **Build Tool:** The project uses Maven.
3. **Execution:** Run the application via the entry point class: `org.example.hallmanagementsystem.Launcher`.
   
   *(Alternatively, use the Maven wrapper from the terminal: `.\mvnw clean javafx:run`)*

### Default Test Credentials:
* **Admin Login:** Username: `admin` | Password: `adminpassword`
* **Student Login:** Username: `2300001` | Password: `password123`
