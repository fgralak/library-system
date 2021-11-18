Library Management System API using Java with Spring Boot.

Includes:

- Spring Web
- Spring Data JPA with PostgreSQL database
- Spring Security
- Registration system which enables:
  - create local app user with email confirmation
  - single sign on using oAuth2 with Google and Facebook API
- Initial data and tables created using Flyway
- Caching implemented for the most commonly used methods
- SSL certificate
- Thymeleaf
- Custom exceptions

Functionalities:

- local users with roles: ADMIN, EMPLOYEE and USER
- oAuth2 users - Facebook and Google
- one unique account with different authentication providers
- each user has its own list of rented books - max 5
- easy way to find if desirable book is located in the library

Admin:
  - user manager - CRUD
  - employee manager - CRUD

Employee:
  - user manager - CRUD
  - book manager - CRUD, and rent/return book, get methods
  - record manager - CRUD for records of books added and removed from library

User:
  - check user's rented books
  - methods to check if books can be found in the library