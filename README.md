# Academic Workflow System

A Spring Boot web application to automate departmental academic activities:
- Create academic committees
- Assign course teachers
- Form exam committees
- Assign question setters & script evaluators
- Call question moderation meetings
- Send automated emails with attachments (applications, letters)
- Auto-generate bills for faculty after completing all tasks of an exam committee

---

##  Features
- Role-based access (Admin, Faculty, Chairman)
- Workflow automation for exam committees
- Email notification system with attachments
- Auto bill generation stored in database
- Dashboard for academic activities tracking

---

##  Tech Stack
- **Backend:** Spring Boot (Java 17+), Spring Data JPA
- **Database:** MySQL 
- **Frontend:** Thymeleaf, HTML, CSS
- **Email Service:** Spring Mail + SMTP
- **Build Tool:** Maven 

---

## Clone & Setup Instructions

To get a local copy up and running, follow these simple steps:

### Clone the Repository
```bash
git clone https://github.com/montasirfahim/academics-and-billing-automation.git
cd academics-and-billing-automation
```
### Checkout main branch
```bash
git checkout main
```

### Setup application.properties with your own database credential
```
spring.datasource.url=jdbc:mysql://localhost:3306/db_name
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

### Run the application
```
mvn spring-boot:run
```
### Visit URL
```
http://localhost:8080
```