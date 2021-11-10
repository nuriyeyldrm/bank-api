# Banking Api Back-end

The Banking API implements a complex domain model flow to banking scenario. The flow simulates a real world application in terms of having a variety of banking options and business rules.


#### Technologies
- Spring Boot (JPA, Web, Devtools, Validation, Security)
- Spring Framework
- PostgreSQL
- Maven
- Javax
- Jsonwebtoken
- Lombok
- Swagger

##### Application starts on localhost port 8080
- [http://localhost:8080/bank/api/user/*]
- [http://localhost:8080/bank/api/admin/*]

#### Available Services
| Http Method | Path | Usage |
| ------ | ------ | ------ |
| GET | /bank/api/user/auth | get user by ssn |
| GET | /bank/api/admin/{id}/auth | get user by id (preauthorize admin) |
| GET | /bank/api/admin/auth/all | get all users (preauthorize admin) |
| POST | /bank/api/user/register | register |
| POST | /bank/api/user/login | login |
| PUT | /bank/api/user/auth | update to user |
| PUT | /bank/api/admin/{id}/auth | update to user (preauthorize admin) |
| PATCH | /bank/api/user/auth | update to password |
| DELETE | /bank/api/admin/{id}/auth | delete to user (preauthorize admin) |
#### ... continues

#### All Postman collections [https://www.getpostman.com/collections/64e4496ef43f7a3df9c0]

#### NOTE: All method except signup/login methods, needs Authorization Bearer token in header 
