# Drones Application

This Spring Boot application manages drones and their operations.

## Setup

1. **Clone the repository:** https://github.com/Kagweitis/Ajua-Drones.git

2. **Build and Run:**
   `./mvnw spring-boot`

3. **Access H2 Console:**
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:drones`
- Username: `sa`
- Password: `password`

4. **API Endpoints:**

- **Create a Drone:**
  ```
  POST http://localhost:8080/api/v1/drones/new
  {
    "serialNumber": "DRN12345",
    "model": "HEAVYWEIGHT",
    "weightLimit": 500.0,
    "batteryCapacity": 75,
    "state": "IDLE"
  }
  ```

- **Get All Available Drones:**
  ```
  GET http://localhost:8080/api/v1/drones/available
  ```

- **Get Drone Battery Capacity:**
  ```
  GET http://localhost:8080/api/v1/drones/battery?serialNumber={serialNumber}
  ```

- **Load Drone:**
  ```
  PUT http://localhost:8080/api/v1/drones/load
  {
    {
    "medications": [
        {
            "name": "MedicationA",
            "code": "MED001",
            "weight": 100.0,
            "image": "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAMCAgMC" // Base64-encoded image data
        },
        {
            "name": "MedicationB",
            "code": "MED002",
            "weight": 150.0,
            "image": "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAMCAgMC" // Base64-encoded image data
        }
    ],
    "droneSerialNumber": "DRN12345"
  }


- **Get Drone Loads:**
  ```
  GET http://localhost:8080/api/v1/drones/get-loads?serialNumber={serialNumber}
  ```

## Testing

### Running Unit Tests

You can run the unit tests using Maven:

```bash
./mvnw test
```


## Technologies Used

- Java
- Spring Boot
- H2 Database
- Lombok
- Jakarta Persistence (JPA)
- Maven

## Contributors

- [George Kimani](https://github.com/Kagweitis)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
