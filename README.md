# Swift Application Setup Guide

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

During this step, all unit and integration tests will be executed automatically. If any tests fail, the build process will stop.
The application contains a total of 23 tests.

### 4. Start the application with Docker
```bash
docker compose up --build
```
This Docker Compose command:
- `--build`: Forces rebuilding of Docker images
- `up`: Creates and starts all services defined in docker-compose.yml

The application should now be running in Docker containers.

## Accessing the Application

- All API endpoints are accessible at `http://localhost:8080`
- Swagger API documentation is available at `http://localhost:8080/swagger-ui/index.html`
- Additional application information and metrics `http://localhost:8080/actuator`

To stop the application, press `Ctrl+C` in the terminal or run `docker compose down` in another terminal.