# 🚀 Employee Portal CI/CD Pipeline (Java + Jenkins + SonarQube + Docker + AWS)

This project demonstrates a **complete end-to-end CI/CD pipeline** for a **Spring Boot Java application** using Jenkins, SonarQube, Docker, and AWS EC2.

The pipeline automatically builds, tests, analyzes code quality, creates Docker images, pushes them to DockerHub, and deploys the application on AWS EC2.

---

## 🏗️ CI/CD Pipeline Architecture

![CI/CD Pipeline Architecture](docs/screenshots/cicd-architecture.png)

> **Single Instance Deployment on AWS EC2 (No Kubernetes)**
>
> The diagram above illustrates the full pipeline flow:
> **Code Push → Jenkins Build & Test → SonarQube Analysis → Docker Build & Push → Deploy to EC2 → Application Live**

---

## 🔄 Pipeline Overview

The pipeline is implemented using **Jenkins Declarative Pipeline** and includes the following stages:

1. Git Checkout
2. Compile
3. Test
4. SonarQube Analysis
5. Build
6. Docker Build
7. Docker Push to DockerHub
8. Run Docker Container

---

## ⚙️ Technologies Used

| Tool | Purpose |
|------|---------|
| Java | Backend Development |
| Spring Boot | Web Framework |
| Maven | Build Tool |
| Jenkins | CI/CD Automation |
| SonarQube | Code Quality Analysis |
| Docker | Containerization |
| DockerHub | Image Repository |
| AWS EC2 | Cloud Deployment |
| GitHub | Source Code Repository |

---

## 🔧 Stage 0: Jenkins Plugin Installation

Before creating the pipeline, the required plugins are installed in Jenkins.

![Jenkins Plugin Installation](docs/screenshots/jenkins-plugins.png)

**Plugins installed:**
- SonarQube Scanner 2.18.2 — integrates SonarQube for code quality inspection
- Pipeline Stage View 2.41 — provides a visual pipeline stage dashboard
- Eclipse Temurin Installer — installs the JDK build based on OpenJDK

---

## 🛠️ Stage 0b: SonarQube Scanner Tool Configuration

The SonarQube Scanner version is configured under **Manage Jenkins → Tools**.

![SonarQube Scanner Version Selection](docs/screenshots/sonarqube-scanner-config.png)

**SonarQube Scanner 8.1.0.6389** is selected as the scanner version for this pipeline.

---

## 🔄 Jenkins Pipeline Execution

The CI/CD pipeline is executed automatically through Jenkins.

![Jenkins Pipeline Stage View](docs/screenshots/jenkins-pipeline-stages.png)

The **Stage View** shows each pipeline stage with average run times:

| Stage | Avg. Time |
|-------|-----------|
| Declarative: Tool Install | 163ms |
| Git Checkout | 1s |
| Compile | 3s |
| Test | 11s |
| SonarQube Analysis | 20s |
| Build Package | 11s |
| Docker Build | 2s |
| Docker Push | 10s |
| Run Docker Container | 773ms |

> ✅ **Build #17** — Passed with all green stages  
> ❌ **Build #16** — Failed at the "Run Docker Container" stage (774ms timeout)

The **SonarQube Quality Gate** result is shown inline as `Passed` with server-side processing `Success`.

---

## 📥 Stage 1: Git Checkout

The pipeline pulls the latest source code from GitHub.

```groovy
git branch: 'main', url: 'https://github.com/mumtaz2029/project-1.git'
```

![Jenkins Pipeline Syntax - Git Checkout](docs/screenshots/jenkins-git-checkout.png)

The Jenkins **Pipeline Syntax** generator is used to produce the correct Groovy `git` step, targeting the `main` branch of the repository.

**Source Code Structure:**
```
app/
Dockerfile
Jenkinsfile
pom.xml
requirements.txt
README.md
etc.
```

---

## 🔨 Stage 2: Compile

Maven compiles the Spring Boot application source code:

```groovy
stage('Compile') {
    steps {
        sh 'mvn compile'
    }
}
```

---

## 🧪 Stage 3: Test

Unit tests are executed (if any) using Maven:

```groovy
stage('Test') {
    steps {
        sh 'mvn test'
    }
}
```

---

## 🔍 Stage 4: SonarQube Analysis

SonarQube scans the codebase for code smells, bugs, security vulnerabilities, and duplications.

### Quality Gate — Initial Scan (Failed)

![SonarQube Quality Gate Failed](docs/screenshots/sonarqube-quality-gate-failed.png)

The initial scan showed **1 condition failed**: 26.7% duplicated lines (threshold ≤ 3.0%). Coverage also showed 0.0% (required ≥ 80.0%).

![SonarQube Project Overview — Failed](docs/screenshots/sonarqube-project-overview-failed.png)

The **employeeapp** project overview showed `Failed` status with 20.1% duplications.

### SonarQube Issues Identified

**Issue 1 — CSS Contrast (Maintainability / Medium)**

![SonarQube CSS Contrast Issue](docs/screenshots/sonarqube-issue-css-contrast.png)

SonarQube flagged a CSS accessibility issue: `Text does not meet the minimal contrast requirement with its background` in `src/main/resources/static/css/style.css` at Line 92 (`color: #fff`).

**Issue 2 — Empty Test Method (Maintainability / High)**

![SonarQube Empty Method Issue](docs/screenshots/sonarqube-issue-empty-method.png)

SonarQube flagged an empty `contextLoads()` method in `JavaCicdPipelineAppApplicationTests.java`: *"Add a nested comment explaining why this method is empty, throw an UnsupportedOperationException or complete the implementation."*

### Fixed Issues

![SonarQube Fixed Issues](docs/screenshots/sonarqube-fixed-issues.png)

After resolving the flagged issues, all **3 issues** were marked as `Fixed` in SonarQube.

### Quality Gate — Passed ✅

![SonarQube Quality Gate Passed](docs/screenshots/sonarqube-quality-gate-passed.png)

After fixes, the SonarQube Quality Gate shows **Passed** with:
- New issues: **0**
- Accepted issues: **0**
- Duplications: **0.0%**

```groovy
stage('SonarQube Analysis') {
    steps {
        withSonarQubeEnv('sonar-server') {
            sh '''$SCANNER_HOME/bin/sonar-scanner \
                -Dsonar.projectName=employeeapp \
                -Dsonar.projectKey=employeeapp \
                -Dsonar.java.binaries=. '''
        }
    }
}

stage('Quality Gate') {
    steps {
        script {
            waitForQualityGate abortPipeline: false,
                credentialsId: 'Sonar-token'
        }
    }
}
```

---

## 📦 Stage 5: Build Package

Maven packages the application into a JAR:

```groovy
stage('Build') {
    steps {
        sh 'mvn package'
    }
}
```

---

## 🐳 Stage 6 & 7: Docker Build & Push

The application is containerized and pushed to DockerHub.

![DockerHub Repository — Pushed Images](docs/screenshots/dockerhub-repository.png)

The **mumtaz2029/employee-portal** DockerHub repository shows **4 active image tags** successfully pushed (latest push: 2 minutes ago, repository size: 177.8 MB).

```groovy
stage('Docker Build') {
    steps {
        script {
            withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                sh 'docker build -t employee-portal .'
                sh 'docker tag employee-portal mumtaz2029/employee-portal:latest'
            }
        }
    }
}

stage('Docker Push to DockerHub') {
    steps {
        script {
            withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                sh 'docker push mumtaz2029/employee-portal:latest'
            }
        }
    }
}
```

---

## ☁️ Stage 8: Deploy to AWS EC2

### EC2 Instance

![AWS EC2 Instance Running](docs/screenshots/aws-ec2-instance.png)

The **Employee Portal** EC2 instance is deployed and running in **ap-southeast-1b (Singapore)**:

| Property | Value |
|---------|-------|
| Instance ID | i-0b69d0b30eecdd1ee |
| Instance Type | m7i-flex.large |
| Instance State | ✅ Running |
| Public IPv4 | 18.143.76.138 |
| Status Checks | 3/3 passed |

### Security Group — Inbound Rules

![AWS EC2 Security Group Rules](docs/screenshots/aws-ec2-security-group.png)

The inbound security group rules expose the following ports:

| Port | Protocol | Purpose |
|------|----------|---------|
| 9000 | TCP | SonarQube |
| 8080 | TCP | Jenkins |
| 5555 | TCP | Application |
| 22 | SSH | Remote Access |

### Deployment Script

```groovy
stage('Run Docker Container') {
    steps {
        script {
            withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                sh 'docker stop employee-portal || true'
                sh 'docker rm employee-portal || true'
                sh 'docker pull mumtaz2029/employee-portal:latest'
                sh 'docker run -d --name employee-portal -p 5555:5555 mumtaz2029/employee-portal:latest'
            }
        }
    }
}
```

---

## 🌐 Stage 9: Application Live

The Employee Portal is live and accessible via the EC2 Public IP.

![Employee Portal — Application Live](docs/screenshots/employee-portal-live.png)

> **URL:** `http://18.143.76.138:5555`

**Features visible in the live app:**
- Welcome Dashboard with employee name (Mumtaz Jahan)
- Attendance tracker — 96.4%
- Leave Balance — 08 Days
- Open Requests — 03
- Check-in Time — 09:07 AM (on time for 18 consecutive days)
- Upcoming Holiday — 22 April
- Manager Feedback — Positive
- Company Announcements & Today's Tasks

---

## 🏗️ Infrastructure & Tools Summary

| Tool | Role |
|------|------|
| **AWS EC2** | Jenkins Server & Application Host (Single Instance) |
| **Docker Hub** | Container Registry — `mumtaz2029/employee-portal` |
| **SonarQube** | Code Quality Analysis — Community Build v26.4.0 |
| **GitHub** | Source Control — `mumtaz2029/project-1` |

---

## 📋 Full Jenkinsfile

```groovy
pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    environment {
        SCANNER_HOME = tool 'sonar-scanner'
    }

    stages {
        stage('Git Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/mumtaz2029/project-1.git'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar-server') {
                    sh '''$SCANNER_HOME/bin/sonar-scanner \
                        -Dsonar.projectName=employeeapp \
                        -Dsonar.projectKey=employeeapp \
                        -Dsonar.java.binaries=. '''
                }
            }
        }

        stage('Quality Gate') {
            steps {
                script {
                    waitForQualityGate abortPipeline: false, credentialsId: 'Sonar-token'
                }
            }
        }

        stage('Build') {
            steps {
                sh 'mvn package'
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                        sh 'docker build -t employee-portal .'
                        sh 'docker tag employee-portal mumtaz2029/employee-portal:latest'
                    }
                }
            }
        }

        stage('Docker Push to DockerHub') {
            steps {
                script {
                    withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                        sh 'docker push mumtaz2029/employee-portal:latest'
                    }
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                script {
                    withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                        sh 'docker stop employee-portal || true'
                        sh 'docker rm employee-portal || true'
                        sh 'docker pull mumtaz2029/employee-portal:latest'
                        sh 'docker run -d --name employee-portal -p 5555:5555 mumtaz2029/employee-portal:latest'
                    }
                }
            }
        }
    }
}
```

---

## 📁 Project Structure

```
project-1/
├── src/
│   ├── main/
│   │   ├── java/com/mumtaz/devops/
│   │   └── resources/static/css/style.css
│   └── test/java/com/mumtaz/devops/
│       └── JavaCicdPipelineAppApplicationTests.java
├── Dockerfile
├── Jenkinsfile
├── pom.xml
└── README.md
```

---

## 🚀 How to Run Locally

```bash
# Clone the repository
git clone https://github.com/mumtaz2029/project-1.git
cd project-1

# Build with Maven
mvn clean package

# Build Docker image
docker build -t employee-portal .

# Run Docker container
docker run -d --name employee-portal -p 5555:5555 employee-portal

# Access the app
open http://localhost:5555
```

---

## 📌 Pipeline Flow Summary

```
GitHub        Jenkins          SonarQube       Docker          AWS EC2         Live
Code Push  →  Build & Test  →  Analysis     →  Build & Push →  Deploy      →  App Live
              (Maven)          (Quality Gate)  (DockerHub)     (EC2 + SSH)    :5555
```
