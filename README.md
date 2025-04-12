# Swift application setup guide

## Prerequisites
- Git
- Maven
- Docker and Docker Compose

## Steps

### 1. Clone the repository
```bash
git clone https://github.com/podgorskip/swift.git
```
This command downloads the source code from the GitHub repository to your local machine.

### 2. Navigate to the project directory
```bash
cd swift
```
This changes your current directory to the newly cloned project folder.

### 3. Build the application
```bash
mvn clean install
```
This Maven command:
- `clean`: Removes any previously built artifacts
- `install`: Compiles the code, runs tests, packages the application, and installs it to your local Maven repository

During this step, all **unit** and **integration** tests will be executed automatically. If any tests fail, the build process will stop.
The application contains a total of 23 tests.

### 4. Start the application with Docker
```bash
docker compose up --build
```
This Docker Compose command:
- `--build`: Forces rebuilding of Docker images
- `up`: Creates and starts all services defined in docker-compose.yml

The application should now be running in Docker containers.

## Accessing the application

- All API endpoints are accessible at `http://localhost:8080`
- Swagger API documentation is available at `http://localhost:8080/swagger-ui/index.html`
  <img width="1453" alt="Screenshot 2025-04-12 at 17 39 29" src="https://github.com/user-attachments/assets/6862b888-56c7-4f8f-ae77-b553cdf7f52f" />
  <img width="1452" alt="Screenshot 2025-04-12 at 17 39 48" src="https://github.com/user-attachments/assets/5f5723de-c831-4389-8d0a-bdfdb8baa4d0" />

- Additional application information and metrics `http://localhost:8080/actuator`

## Available endpoints

| Method | Endpoint                            | Description                                   | Response codes            |
|--------|-------------------------------------|-----------------------------------------------|---------------------------|
| GET    | /v1/swift-codes/{code}              | Get swift code details by code                | 200 (Success), 404 (Not Found) |
| GET    | /v1/swift-codes/country/{country}   | Get swift codes by country (2-letter ISO code)| 200 (Success)              |
| POST   | /v1/swift-codes                     | Create a new swift code                       | 201 (Created), 400 (Bad Request), 409 (Conflict) |
| DELETE | /v1/swift-codes/{code}              | Delete a swift code                           | 200 (Success), 404 (Not Found) |

<img width="1031" alt="Screenshot 2025-04-12 at 17 58 11" src="https://github.com/user-attachments/assets/58e1a886-c113-46f0-b6d8-65625d021106" />

<img width="1034" alt="Screenshot 2025-04-12 at 17 58 37" src="https://github.com/user-attachments/assets/8b33c752-0644-4e5a-86aa-f278086b74a1" />

<img width="1039" alt="Screenshot 2025-04-12 at 17 58 50" src="https://github.com/user-attachments/assets/a5634cc6-19ce-49b1-9c8e-7a570068243e" />

<img width="1032" alt="Screenshot 2025-04-12 at 17 59 13" src="https://github.com/user-attachments/assets/9b53b097-07db-4416-b7e4-c89a67ff444a" />

## Stopping application

To stop the application, press `Ctrl+C` in the terminal or run `docker compose down` in another terminal.
