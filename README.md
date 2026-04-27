# CI/CD Pipeline for Java Application

This project is a Jenkins-driven Java application built for an Ubuntu 24.04 VM setup. The core workflow is: launch the VM, install Jenkins with Java 21, run SonarQube in Docker, build the Spring Boot application with Maven, and execute automated code quality analysis from Jenkins. The application itself is a mini employee portal, while the DevOps story focuses on Jenkins automation, Maven builds, and SonarQube validation.

## Project Overview

This repository demonstrates how to:

- host Jenkins on an Ubuntu EC2 VM
- run SonarQube in Docker on the same VM
- build a Java Spring Boot project with Maven
- perform automated SonarQube analysis from Jenkins
- prepare a clean CI flow for interview and portfolio use

## Tech Stack

- Ubuntu 24.04 VM
- Jenkins
- SonarQube Community LTS
- Docker
- Java 21
- Maven 3
- Spring Boot
- Thymeleaf

## Application Features

- employee portal dashboard UI at `/`
- project and pipeline details API at `/api/pipeline`
- welcome API at `/api/message`
- application health endpoint at `/actuator/health`
- Maven and JaCoCo-enabled build configuration
- Jenkinsfile aligned to Linux shell execution

## Infrastructure Used

- Instance type: `t2.large`
- AMI: `Ubuntu 24.04`
- Storage: `25 GB`
- Number of instances: `1`

## Ubuntu VM Setup

### 1. Launch the VM

Provision an Ubuntu VM with:

- instance type `t2.large`
- Ubuntu `24.04`
- `25 GB` storage
- one instance

### 2. Connect to the VM

Connect using SSH or MobaXterm.

### 3. Update packages

```bash
sudo apt update
```

## Jenkins Setup

Install Java 21 first:

```bash
sudo apt install openjdk-21-jre-headless -y
```

Then install Jenkins using the official Debian/Ubuntu repository:

```bash
sudo wget -O /usr/share/keyrings/jenkins-keyring.asc \
  https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key
echo "deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc]" \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null
sudo apt-get update
sudo apt-get install jenkins -y
```

If you want to keep the installation commands in a script file:

```bash
vi j.sh
```

Paste the Jenkins commands, save the file, and then run:

```bash
sudo chmod +x j.sh
./j.sh
```

Jenkins runs on port `8080` by default, so open port `8080` in the VM security group and complete the usual Jenkins first-time setup in the browser:

```text
http://<public-ip>:8080
```

## SonarQube Setup

SonarQube is run through Docker on the same VM.

Install Docker:

```bash
sudo apt install docker.io -y
```

Allow the current user to communicate with the Docker socket:

```bash
sudo chmod 666 /var/run/docker.sock
```

Run SonarQube:

```bash
docker run -d --name sonarqube -p 9000:9000 sonarqube:lts-community
docker ps
```

Open port `9000` in the security group, then access SonarQube:

```text
http://<public-ip>:9000
```

Default login:

- username: `admin`
- password: `admin`

After first login, set a new password.

## Jenkins Plugin Setup

From Jenkins:

`Manage Jenkins` -> `Plugins` -> `Available Plugins`

Install these plugins:

- SonarQube Scanner
- Eclipse Temurin installer
- Pipeline Stage View

## Jenkins Tool Configuration

Go to:

`Manage Jenkins` -> `Tools`

Configure the following tools:

### JDK

- Name: `jdk21`
- Install automatically: enabled
- Source: `Install from adoptium.net`
- Version: `jdk-21`

### SonarQube Scanner

- Name: `sonar-scanner`
- Install automatically: enabled
- Source: `Install from Maven Central`
- Version: `SonarQube Scanner 5.0.1.3006`

### Maven

- Name: `maven3`
- Install automatically: enabled
- Source: `Install from Apache`
- Version: `3.9.7`

## SonarQube Token and Credentials

Generate a token in SonarQube:

`Administration` -> `Security` -> `Users` -> `Tokens`

Create a token, copy it, and store it in Jenkins:

`Manage Jenkins` -> `Credentials` -> `global` -> `Add Credentials`

Use:

- Kind: `Secret text`
- Scope: `Global`
- Secret: `<your-sonarqube-token>`
- ID: `sonar-token`
- Description: `sonar-token`

If you plan to push images later, also add Docker Hub credentials:

- Kind: `Username with password`
- ID: `dockerhub-credentials`

## SonarQube Server Configuration in Jenkins

Go to:

`Manage Jenkins` -> `System`

Under `SonarQube servers`, add:

- Name: `sonar-server`
- Server URL: `http://<public-ip>:9000`
- Server authentication token: `sonar-token`

## Jenkins Job Setup for SonarQube Analysis

This project can be used in Jenkins as a pipeline job for automated analysis.

### Create the job

- Create a new Pipeline job
- Point it to this repository
- Select the branch you want to build

The repository already contains a Linux-friendly [`Jenkinsfile`](Jenkinsfile) for Ubuntu/Jenkins agents.

### Pipeline stages included

The pipeline performs:

1. Source checkout
2. Maven compile
3. Unit test execution
4. Maven verify and package lifecycle
5. SonarQube analysis

## Required Project Changes Included

This repository was updated to match your Ubuntu + Jenkins + SonarQube setup:

- [`Jenkinsfile`](Jenkinsfile) now uses Linux `sh` commands instead of Windows `bat`
- [`pom.xml`](pom.xml) now includes Sonar properties
- [`pom.xml`](pom.xml) now includes JaCoCo reporting support for better analysis coverage

## Run the Application Locally

## SonarQube Code Analysis and Issue Resolution

After integrating SonarQube with Jenkins, automated code quality analysis was executed as part of the CI pipeline.

During the first scan, SonarQube reported a maintainability issue related to an empty test method (`contextLoads`) in the Spring Boot test class.

The issue appeared under rule:

`java:S1186 – Methods should not be empty`

### Issue Detected

SonarQube flagged the test method because it had an empty body.

![SonarQube Issue](images/sonarqube-issue.png)

### Resolution

The issue was resolved by documenting the purpose of the test method.  
The method verifies that the Spring Boot application context loads successfully.

Updated test method:

``
@Test
void contextLoads() {
    // Verify that the Spring Boot application context loads successfully
} ``

## API Endpoints

- `GET /` - employee portal dashboard
- `GET /api/message` - welcome message
- `GET /api/pipeline` - project and pipeline summary
- `GET /actuator/health` - application health

## Interview Value

This project is useful for explaining:

- Jenkins installation on Ubuntu
- SonarQube setup with Docker
- plugin and tool configuration in Jenkins
- pipeline-based Maven build flow
- automated code quality analysis for a Java application

## Note

Do not commit real secrets or SonarQube tokens to the repository. Keep tokens and passwords in Jenkins credentials instead of hardcoding them into code or pipeline files.
