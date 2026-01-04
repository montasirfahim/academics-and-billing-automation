![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005C00.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white)
![CSS3](https://img.shields.io/badge/css3-%231572B6.svg?style=for-the-badge&logo=css3&logoColor=white)
![Bootstrap 5](https://img.shields.io/badge/Bootstrap_5-%238511FA.svg?style=for-the-badge&logo=bootstrap&logoColor=white)
![JavaScript](https://img.shields.io/badge/javascript-%23F7DF1E.svg?style=for-the-badge&logo=javascript&logoColor=black)

# Academics Automation Software
A Real-world, modern, full-stack Java application designed to streamline departmental academic workflows. The system automates administrative tasks and tracks the responsibilities of both internal and external faculty members throughout the entire academic lifecycle, from the starting of a semester to the publication of results. By replacing manual processes, this application increases operational efficiency, saves time, and ensures the highly accurate generation of faculty gratuity bills.

## Summary of Academic Workflow System

- Create academic committees
- Assign course teachers
- Form exam committees
- Assign question setters & script evaluators
- Call question moderation meetings
- Send automated emails with attachments (applications, letters)
- Assign third examiner if marks between internal & external examiner differ by more than 20%
- Auto-generate bills for faculty members after completing all tasks of an exam committee
- Create Tour Allowance Bill for external faculty members
---

##  Features
- Role-based access (Admin, Faculty, Chairman)
- Two-factor authentication via Email
- Workflow automation for a complete semester
- Email notification system with auto generated attachments (Formal Application or Letter)
- Auto bill generation stored in database
- Dashboard for academic activities tracking

---

##  Tech Stack
- **Backend:** Spring Boot (Java 17+), MVC & REST API with Spring Data JPA
- **Database:** MySQL 
- **Frontend:** Thymeleaf, HTML, CSS, Bootstrap 5, Javascript
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

[![Montasir's GitHub stats](https://github-readme-stats.vercel.app/api?username=montasirfahim&show_icons=true&theme=radical)](https://github.com/montasirfahim)