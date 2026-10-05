# SkillBridge – Local Job and Daily-Wage Worker Portal

A Java web application (Servlets + JSP + JDBC + MySQL) that connects customers with verified local
electricians, plumbers, carpenters, painters and helpers. Customers search, book a slot, pay (simulated) and
rate the job. Workers manage their profile, rate, hours and bookings. An admin verifies workers and views reports.

Built for the **Web Technology Lab** – it covers all 10 experiments in one project (see the table below).

## 1. Requirements
| Tool | Version |
|---|---|
| JDK | 17 or newer |
| Apache Tomcat | **10.1.x** (uses `jakarta.servlet.*`; Tomcat 9 will NOT work) |
| MySQL | 8.x (MariaDB 10.5+ also works) |
| Maven | 3.8+ |

## 2. Set up the database
```
mysql -u root -p < database/skillbridge.sql
```
This creates the `skillbridge` database, all tables and demo data (16 workers, 20 bookings, reviews).
Then open `src/main/resources/db.properties` and set `db.user` / `db.password` for your MySQL.
(You can also override any value without editing the file, e.g. `-Ddb.password=secret` in `CATALINA_OPTS`.)

## 3. Build and run
```
mvn clean package
```
Copy `target/skillbridge.war` to Tomcat's `webapps/` folder, start Tomcat, and open
**http://localhost:8080/skillbridge/**

In IntelliJ IDEA (Ultimate) or Eclipse (Java EE) you can instead add Tomcat 10.1 as a server and deploy the
`skillbridge:war exploded` artifact.

## 4. Demo accounts
| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `Admin@123` |
| Customer | `customer1` … `customer4` | `Customer@123` |
| Worker | `ramesh_k` (electrician), `mahesh_y` (plumber), others in the admin Users page | `Worker@123` |

Payment test data: card `4242 4242 4242 4242` (any future expiry, any 3-digit CVV) succeeds; a card ending
`0002` or a UPI id ending `@fail` is declined. No real money is ever charged.

## 5. Lab experiments → where to find them
| # | Experiment | In SkillBridge |
|---|---|---|
| 1 | Client-side scripting (JavaScript) | `assets/js/app.js`: form validation, password strength, live price estimate, star input |
| 2 | Simple web application using Servlets | `servlet/` package: login, register, search, booking, checkout, review… |
| 3 | Simple web application using JSP | `WEB-INF/views/*.jsp` with JSTL/EL and custom tags in `WEB-INF/tags` |
| 4 | Cookies and session management | `/session-info` demo page; login session; "remember me" cookie; "recently viewed" cookie; visit counter |
| 5 | Database connectivity using Servlet and JSP | `dao/` package (JDBC, prepared statements, HikariCP pool) feeding JSP pages |
| 6 | Form validation using AJAX | `/register`: live username/e-mail/phone availability (`/ajax/check`); booking form slot check (`/ajax/slot`); live search on `/workers` |
| 7 | XML document shown as an HTML table | `/workers.xml` (server) → `/xml-workers` (client parses `responseXML`, sortable table) |
| 8 | Application that uses a database | Users, workers, skills, bookings, payments, reviews; admin CRUD (`/admin/*`) |
| 9 | E-commerce style transactional application | Book → price breakdown → pay → booking history, cancellation with refund |
| 10 | Testing | `src/test/java` (JUnit 5, 41 tests) and `docs/TEST_CASES.md` (black-box + boundary value) |

## 6. Project structure
```
database/skillbridge.sql       schema + demo data
src/main/java/com/skillbridge
  model/    User, Worker, Skill, Booking, Review, BookingDraft
  dao/      JDBC data access (UserDAO, WorkerDAO, BookingDAO, …)
  service/  BookingService (slot + availability rules)
  servlet/  controllers (and servlet/admin/)
  filter/   EncodingFilter (UTF-8 + security headers), AuthFilter (roles, CSRF, remember-me)
  util/     Validator, PricingUtil, SlotRules, PasswordUtil (BCrypt), TokenUtil, MockGateway, Json
src/main/webapp
  WEB-INF/views, fragments, tags, web.xml
  assets/css/style.css, assets/js/app.js
src/test/java                  JUnit tests
docs/                          test cases
```

## 7. Security features (good viva points)
BCrypt password hashing · prepared statements (no SQL injection) · output escaping (no XSS) · CSRF token on
every POST · role-based access in a filter · session id regenerated on login · HttpOnly cookies · signed
"remember me" token · open-redirect protection · server-side validation that mirrors the browser rules.

## 8. Troubleshooting
* **HTTP 404 on every page / `ClassNotFoundException: javax.servlet`** – you are using Tomcat 9. Use Tomcat 10.1.
* **`Access denied for user`** – fix `db.user`/`db.password` in `db.properties`.
* **Emoji icons look garbled** – re-run the SQL file with `mysql --default-character-set=utf8mb4 -u root -p < database/skillbridge.sql`.
* **Port 8080 busy** – change the connector port in Tomcat's `conf/server.xml`.
* **Reset demo data** – run the SQL file again (it drops and recreates the tables).
