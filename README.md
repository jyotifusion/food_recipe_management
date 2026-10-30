# CulinaryCraft — Recipe Management System

A modern, full-stack, enterprise-quality **Recipe Management System** built with **Java 17, Jakarta Servlets 6.0, JSP (JavaServer Pages), XML (W3C DOM), HTML5, CSS3, and JavaScript**.

---

## 1. Project Overview & Features

**CulinaryCraft** simplifies recipe organization with a responsive, warm food-inspired aesthetic and zero external database dependencies. All recipe data is persisted in a structured, external XML file that survives server restarts and application redeployments.

### Core Features
- **Interactive Home Dashboard:**
  - Displays total recipes count, cuisine categories count, quick prep options (<30m), and XML storage status.
  - Category navigation bar with recipe counters per category.
  - Recently added recipes grid with visual difficulty badges and metadata.
  - Quick action CTA buttons to add new recipes and browse the catalog.
- **Recipe Catalog & Advanced Search/Filter:**
  - Search recipes by title, ingredients, description, or category keyword.
  - Filter by category (`Breakfast`, `Lunch`, `Dinner`, `Desserts`, `Snacks`, `Beverages`).
  - Filter by difficulty (`Easy`, `Medium`, `Hard`).
  - Sort by newest, oldest, name (A–Z / Z–A), and cooking time (low to high / high to low).
  - Empty state with clear suggestions and one-click filter reset.
- **Detailed Recipe View:**
  - High-resolution food photography with fallback category illustrations.
  - Preparation time, cooking time, total time, and serving size statistics.
  - **Dynamic Serving Scaler:** Click `+` or `-` to dynamically recalculate ingredient quantities in real time.
  - **Interactive Ingredients Checklist:** Click to strike through and check off ingredients as you cook.
  - Numbered, step-by-step preparation instructions.
  - Print-friendly layout (`Print` button triggers clean printer stylesheet).
- **Add & Edit Recipe Forms:**
  - Multi-field server-side and client-side validation.
  - Live image URL preview box.
  - Multi-line textarea parsing for ingredients and preparation instructions.
  - Post/Redirect/Get (PRG) pattern preventing accidental duplicate form submissions.
- **Secure Deletion:**
  - Double-confirmation modal dialog protecting against accidental deletion.
  - Supported via `POST` requests conforming to HTTP semantics.
- **XML Data Engine & Security:**
  - Native Java DOM API (`DocumentBuilderFactory`, `Transformer`).
  - Hardened against **XXE (XML External Entity)** injection attacks (`disallow-doctype-decl`, disabled DTDs).
  - Thread-safe concurrency with `ReentrantReadWriteLock`.

---

## 2. Technology Stack & Prerequisites

| Layer | Technology | Version |
| :--- | :--- | :--- |
| **Language** | Java (JDK) | 17+ (Temurin / OpenJDK) |
| **Servlet API** | Jakarta Servlet | 6.0.0 |
| **View Engine** | JSP &amp; JSTL | 3.1.1 / 3.0.1 |
| **Data Persistence** | XML DOM Parser &amp; Transformer | W3C DOM (Java Standard Library) |
| **Build Tool** | Apache Maven | 3.9+ (Wrapper Included) |
| **Web Server** | Apache Tomcat | 10.1+ (Jakarta EE 10 compatible) |

---

## 3. Project Directory Structure

```text
RecipeManagementSystem/
├── pom.xml                                   # Maven build configuration
├── mvnw / mvnw.cmd                           # Cross-platform Maven wrapper
├── src/
│   ├── main/
│   │   ├── java/com/recipe/
│   │   │   ├── model/
│   │   │   │   └── Recipe.java               # JavaBean model & validation logic
│   │   │   ├── dao/
│   │   │   │   └── RecipeXMLRepository.java  # Thread-safe XML repository DAO
│   │   │   ├── util/
│   │   │   │   └── XMLUtil.java              # DOM parser with XXE protection
│   │   │   ├── servlet/
│   │   │   │   ├── DashboardServlet.java     # Dashboard controller (/, /dashboard)
│   │   │   │   ├── RecipeListServlet.java    # List, search, filter (/recipes)
│   │   │   │   ├── ViewRecipeServlet.java    # Recipe details (/recipe/view)
│   │   │   │   ├── AddRecipeServlet.java     # Create recipe (/recipe/add)
│   │   │   │   ├── EditRecipeServlet.java    # Edit recipe (/recipe/edit)
│   │   │   │   ├── DeleteRecipeServlet.java  # Delete recipe (/recipe/delete)
│   │   │   │   └── AboutServlet.java         # Architecture & XML overview (/about)
│   │   │   └── launcher/
│   │   │       └── Main.java                 # Embedded Tomcat 10.1 dev launcher
│   │   ├── resources/
│   │   │   └── recipes.xml                   # Seed XML dataset with 6 sample recipes
│   │   └── webapp/
│   │       ├── index.jsp                     # Root entry point redirector
│   │       ├── assets/
│   │       │   ├── css/style.css             # Modern warm-cream & sage-green design
│   │       │   └── js/script.js              # Dynamic scaler, checklist, modal JS
│   │       └── WEB-INF/
│   │           ├── web.xml                   # Deployment descriptor (Servlet 6.0)
│   │           └── views/
│   │               ├── common/
│   │               │   ├── header.jsp        # Meta tags, fonts, stylesheets
│   │               │   ├── navbar.jsp        # Navigation header & mobile menu
│   │               │   ├── alerts.jsp        # Flash toast & error messages
│   │               │   ├── delete-modal.jsp  # Reusable confirmation modal
│   │               │   └── footer.jsp        # Site footer & script includes
│   │               ├── dashboard.jsp         # Dashboard home view
│   │               ├── recipes.jsp           # Catalog & search results view
│   │               ├── recipe-details.jsp    # Single recipe presentation view
│   │               ├── add-recipe.jsp        # Creation form view
│   │               ├── edit-recipe.jsp       # Modification form view
│   │               ├── about.jsp             # Technical architecture details
│   │               └── error.jsp             # 404 / 500 error display
│   └── test/
│       └── java/com/recipe/dao/
│           └── RecipeXMLRepositoryTest.java  # Unit tests for CRUD, XXE, and filtering
└── README.md
```

---

## 4. How XML Storage Works

### External Storage Priority
Application data is **not** stored inside the packaged WAR file (which would reset on redeployment). The repository resolves the file path in this order:

1. **Servlet Context Parameter** (`web.xml`):
   ```xml
   <context-param>
       <param-name>recipeXmlPath</param-name>
       <param-value>/var/data/recipes.xml</param-value>
   </context-param>
   ```
2. **JVM System Property:** `-Drecipe.xml.path=/custom/path/recipes.xml`
3. **Environment Variable:** `RECIPE_XML_PATH=/custom/path/recipes.xml`
4. **Default Location:** `${user.home}/.recipe_management_system/recipes.xml`

### Automatic Seed Initialization
On first startup, if the external XML file does not exist, the repository creates parent directories and copies the bundled sample recipes from `src/main/resources/recipes.xml`.

### XML Schema
```xml
<?xml version="1.0" encoding="UTF-8"?>
<recipes>
    <recipe id="1">
        <name>Vegetable Pasta</name>
        <category>Dinner</category>
        <description>A delicious homemade vegetable pasta.</description>
        <ingredients>
            <ingredient>250g Penne or Fusilli Pasta</ingredient>
            <ingredient>4 Ripe Vine Tomatoes</ingredient>
        </ingredients>
        <instructions>
            <step>Bring a large pot of salted water to a boil.</step>
            <step>Cook pasta until al dente and toss with tomato sauce.</step>
        </instructions>
        <preparationTime>15</preparationTime>
        <cookingTime>20</cookingTime>
        <servings>3</servings>
        <difficulty>Easy</difficulty>
        <imageUrl>https://images.unsplash.com/photo-1621996346565-e3d5d6281781</imageUrl>
    </recipe>
</recipes>
```

---

## 5. How to Build & Run

### A. Prerequisites
- JDK 17+ installed and set on your PATH (`java -version`).
- Maven 3.9+ or use the included `./mvnw`.

### B. Compile & Package
In the project root directory, run:
```bash
mvn clean package
```
This produces:
`target/RecipeManagementSystem.war` (ready for any Tomcat 10.1+ server).

### C. Run the Embedded Tomcat Server (Quick Dev Mode)
You can launch the application immediately without an external Tomcat installation:
```bash
mvn compile exec:java
```
Or with custom port:
```bash
mvn compile exec:java -Dserver.port=9090
```
Open your browser at:
`http://localhost:8080/` (or `http://localhost:8080/dashboard`)

---

## 6. How to Deploy to Standalone Apache Tomcat 10.1

1. **Download Apache Tomcat 10.1+** from [tomcat.apache.org](https://tomcat.apache.org/download-10.cgi).
   *(Note: Tomcat 10.1+ supports Jakarta Servlet API 6.0).*
2. Build the project WAR:
   ```bash
   mvn package -DskipTests
   ```
3. Copy `target/RecipeManagementSystem.war` into the Tomcat `webapps/` folder:
   - **Windows:** `C:\apache-tomcat-10.1.x\webapps\RecipeManagementSystem.war`
   - **Linux/macOS:** `/opt/tomcat/webapps/RecipeManagementSystem.war`
4. Start Tomcat:
   - **Windows:** Run `bin\startup.bat`
   - **Linux/macOS:** Run `bin/startup.sh`
5. Access the application in your browser:
   `http://localhost:8080/RecipeManagementSystem/`

---

## 7. Testing Operations

### 1. Automated Unit Tests
Run the complete test suite:
```bash
mvn test
```
Verifies:
- `testInitialSeedLoading`: Validates seed data extraction from classpath.
- `testFindById`: Validates individual recipe retrieval.
- `testAddRecipe`: Verifies ID auto-generation and persistence to disk.
- `testUpdateRecipe`: Checks field modification and DOM rewrite.
- `testDeleteRecipe`: Tests record removal and XML synchronization.
- `testSearchAndFilter`: Tests query matching across ingredients, names, and categories.
- `testCategoryCounts`: Verifies category aggregation for dashboard stats.

### 2. Manual Testing Guide
| Action | Steps to Test | Expected Result |
| :--- | :--- | :--- |
| **Browse** | Click **"All Recipes"** in top navbar. | All 6 seed recipes render with tags, cooking times, and photos. |
| **Search** | Type `pasta` in search box and click **Filter**. | Only pasta recipes display. Click **Reset** to restore. |
| **Category Filter** | Select `Desserts` from dropdown and click **Filter**. | Only chocolate cake displays. |
| **Add Recipe** | Click **"Add Recipe"**, fill in required fields, click **Save Recipe**. | Redirects to the detailed view with a green success alert. |
| **Serving Scaler** | On recipe detail page, click `+` next to Servings. | Ingredient amounts automatically scale up dynamically! |
| **Checklist** | Click any ingredient line. | Strikethrough style applies and checkbox marks done. |
| **Edit Recipe** | Click **"Edit Recipe"**, change name or cooking time, click **Update**. | Changes reflect immediately on recipe details. |
| **Delete Recipe** | Click **"Delete"**, confirm modal prompt. | Recipe is removed from catalog with confirmation message. |
| **Persistence** | Stop server, edit a recipe, restart server. | Changes remain saved in `${user.home}/.recipe_management_system/recipes.xml`. |

---

## 8. Common Troubleshooting

| Issue | Root Cause | Solution |
| :--- | :--- | :--- |
| **`404 Not Found` on Tomcat 9 or earlier** | Tomcat 9 uses `javax.servlet` instead of `jakarta.servlet`. | Deploy to Apache Tomcat 10.1+ which supports Jakarta EE 10. |
| **`Access Denied` on XML file** | Default user home directory lacks write permissions. | Specify a writable path using `-Drecipe.xml.path=C:/mydata/recipes.xml`. |
| **Duplicate submissions on refresh** | Browser re-posting form data. | The system implements the Post/Redirect/Get pattern to prevent this. |
| **Port 8080 already in use** | Another service (e.g. XAMPP, Oracle) is bound to 8080. | Run with `-Dserver.port=8085` or change port in Tomcat's `conf/server.xml`. |
