# ShoppingAPI

## Description
ShoppingAPI is a RESTful API for a shopping application built with Spring Boot. It provides core e-commerce functionalities such as product and cart management. The project uses Spring Data JPA with a PostgreSQL database and is fully containerized using Docker for easy setup and deployment.

## Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/utkualcan/ShoppingAPI.git
   cd ShoppingAPI
   ```

2. **Configure Database Settings**
   - Update your PostgreSQL database credentials in `src/main/resources/application.properties` or your `.env` file:
     ```
     spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
     spring.datasource.username=your_user
     spring.datasource.password=your_password
     ```
   - Create your database if it does not already exist.

3. **Using Docker (Recommended)**
   - Make sure you have Docker and Docker Compose installed. See [Docker documentation](https://docs.docker.com/get-docker/).
   - Start the application and database with:
     ```bash
     docker-compose up --build
     ```

4. **Manual Setup (Alternative)**
   - Ensure Java 17 or higher is installed.
   - Install dependencies and build the project:
     ```bash
     ./mvnw clean install
     ```
   - Run the application:
     ```bash
     ./mvnw spring-boot:run
     ```

## Usage

- By default, the API runs at `http://localhost:8080`.
- You can access API documentation via Swagger UI or OpenAPI:
  ```
  http://localhost:8080/swagger-ui.html
  ```
- Main features include:
  - Product listing, adding, updating, and deleting
  - Cart creation, and product add/remove to cart
  - User operations (registration, login, etc.)

### Example API Requests

```http
GET /api/products
```

```http
POST /api/cart
Content-Type: application/json

{
  "productId": 1,
  "quantity": 2
}
```

## Contributing

Contributions are welcome!

1. Fork the repository and create a new branch.
2. Make your changes.
3. Run tests.
4. Submit a pull request.

Please contact for more details or open an issue for questions or suggestions.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

