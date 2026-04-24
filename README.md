# CI/CD Pipeline for Java Application

This project is a Maven-based Spring Boot web application prepared for a CI/CD workflow using GitHub, Jenkins, Docker, Java, and Linux-oriented deployment steps. It now presents a modern mini employee portal UI so the application feels like a real internal business product.

## Project Overview

The application demonstrates a simple Java web service that can be:

- built with Maven
- tested automatically
- packaged as a JAR
- containerized with Docker
- deployed through a Jenkins pipeline

## Tech Stack

- GitHub
- Jenkins
- Docker
- Maven
- Java 21
- Spring Boot
- Linux-friendly container deployment

## Features

- Modern employee portal dashboard UI
- REST API endpoints for application status and project information
- Health endpoint through Spring Boot Actuator
- Maven build and test configuration
- Dockerfile for container image creation
- Jenkinsfile for CI/CD pipeline stages

## API Endpoints

- `GET /` - employee portal dashboard
- `GET /api/message` - returns a welcome message
- `GET /api/pipeline` - returns pipeline/project details
- `GET /actuator/health` - returns health status

## Run Locally

### 1. Build the application

```bash
mvn clean package
```

### 2. Run the JAR

```bash
java -jar target/java-cicd-pipeline-app-0.0.1-SNAPSHOT.jar
```

### 3. Access the app

```text
http://localhost:8080/
```

## Docker Commands

### Build image

```bash
docker build -t java-cicd-pipeline-app .
```

### Run container

```bash
docker run -d -p 8080:8080 --name java-cicd-pipeline-app java-cicd-pipeline-app
```

## Jenkins Pipeline Flow

The `Jenkinsfile` performs:

1. Source checkout
2. Maven build
3. Test execution
4. Docker image build
5. Container deployment

## Suggested Jenkins Requirements

- JDK 21 configured as `jdk21`
- Maven configured as `maven3`
- Docker available on the Jenkins agent

## Repository Purpose

This repository is intended to demonstrate a CI/CD-ready Java application project for portfolio and learning purposes.
