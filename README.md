# Primo Game Spin

## Overview
The Primo Game Spin allows player to test his luck by calling an endpoint that spins randomly a number, if the number is prime, user wins.
It does this by generating cryptographically secure random numbers, and tracks player outcomes. Developed using Java 11 and SPARK Framework.
Uses in-memory cache and DB which make the flow of the game to handle multiple simultaneous users without issues.

## Technical Details
- Language: Java 11
- Framework: Spark Framework
- Database: Portable or in-memory storage
- Documentation: OpenAPI for API specification (swagger.yaml provided)

## Getting Started

### Running the Application
To run the application, use the following commands:

### Compile the application
```bash
mvn clean install
```

### Run the application
```bash
java -cp target/PrimoSpark-1.0-SNAPSHOT.jar org.primo.PrimoApp
```
# Testing
For testing use /test/request.http or the swagger.yaml file to have an overview for the endpoints