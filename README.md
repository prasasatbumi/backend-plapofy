# Loan Management System

## Documentation

### 1. JPA Annotations Purpose

*   **@Entity**: Specifies that the class is an entity and is mapped to a database table.
*   **@Table(name = "...")**: Specifies the name of the database table to be used for mapping.
*   **@Id**: Specifies the primary key of an entity.
*   **@GeneratedValue(strategy = GenerationType.IDENTITY)**: Provides for the specification of generation strategies for the values of primary keys. `IDENTITY` indicates that the persistence provider must assign primary keys for the entity using a database identity column.
*   **@Column(nullable = false, unique = true)**: Specifies the mapped column for a persistent property or field. `nullable = false` adds a NOT NULL constraint. `unique = true` adds a UNIQUE constraint.
*   **@ManyToMany**: Specifies a many-to-many association with another entity class.
*   **@JoinTable**: Used in the mapping of associations. It specifies the mapped association table (join table) and the join columns.
    *   `name`: The name of the join table (`user_roles`).
    *   `joinColumns`: The foreign key columns of the join table which reference the primary table of the entity owning the association (`user_id`).
    *   `inverseJoinColumns`: The foreign key columns of the join table which reference the primary table of the entity that does not own the association (`role_id`).
*   **@JsonIgnore**: Instructs Jackson JSON serializer to ignore this field during serialization, preventing infinite recursion in bidirectional relationships.

### 2. Database Schema (Why 3 Tables?)

Hibernate creates 3 tables because of the **Many-to-Many** relationship between `User` and `Role`.

1.  **users**: Stores user information (id, username, password, etc.).
2.  **roles**: Stores role information (id, name).
3.  **user_roles**: This is the **Join Table**. In a relational database, a Many-to-Many relationship cannot be represented by adding a column in either table (because one user has multiple roles, and one role belongs to multiple users). A third table is required to link `user_id` and `role_id` pairs.

### 3. ORM Flow

1.  **HTTP Request**: The client sends a REST API request (e.g., `GET /users`) to the application.
2.  **Controller**: The `UserController` receives the request. It acts as the entry point and delegates the business logic to the `UserService`.
3.  **Service**: The `UserService` contains the business logic. It calls the `UserRepository` to fetch data.
4.  **Repository**: The `UserRepository` extends `JpaRepository`. It provides methods to interact with the data layer. When `findAll()` is called, Spring Data JPA translates this into a Hibernate query.
5.  **Hibernate**: Hibernate (the ORM provider) translates the JPA/HQL query into a native SQL query specific to Microsoft SQL Server (`SELECT ... FROM users ...`).
6.  **SQL Server**: The database executes the SQL query and returns the result set.
7.  **Return Path**: Hibernate maps the ResultSet back to Java Objects (`User` entities). The Service returns them to the Controller. The Controller serializes them to JSON and sends the HTTP Response back to the client.

### 4. Sample JSON Request & Response

#### Create Role
**POST** `/roles`
Request Body:
```json
{
    "name": "MANAGER"
}
```
Response:
```json
{
    "id": 3,
    "name": "MANAGER"
}
```

#### Create User
**POST** `/users`
Request Body:
```json
{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "secretpassword",
    "isActive": true,
    "roles": [
        {
            "id": 2,
            "name": "USER"
        }
    ]
}
```
Response:
```json
{
    "id": 2,
    "username": "john_doe",
    "email": "john@example.com",
    "password": "secretpassword",
    "isActive": true,
    "roles": [
        {
            "id": 2,
            "name": "USER"
        }
    ]
}
```

#### Get All Users
**GET** `/users`
Response:
```json
[
    {
        "id": 1,
        "username": "admin",
        "email": "admin@finprov.com",
        "password": "admin123",
        "isActive": true,
        "roles": [
            {
                "id": 1,
                "name": "ADMIN"
            },
            {
                "id": 2,
                "name": "USER"
            }
        ]
    },
    {
        "id": 2,
        "username": "john_doe",
        "email": "john@example.com",
        "password": "secretpassword",
        "isActive": true,
        "roles": [
            {
                "id": 2,
                "name": "USER"
            }
        ]
    }
]
```
