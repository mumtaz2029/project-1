# 🚀 Employee Portal CI/CD Pipeline (Java + Jenkins + SonarQube + Docker + AWS)

This project demonstrates a **complete end-to-end CI/CD pipeline** for a **Spring Boot Java application** using Jenkins, SonarQube, Docker, and AWS EC2.

The pipeline automatically builds, tests, analyzes code quality, creates Docker images, pushes them to DockerHub, and deploys the application on AWS EC2.

---

## 🏗️ CI/CD Pipeline Architecture

<img width="1344" height="896" alt="image" src="https://github.com/user-attachments/assets/bbd0a3b7-6be3-4e0f-9bfe-f5264a622c0d" />


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

<img width="1914" height="1031" alt="Screenshot 2026-04-27 052024" src="https://github.com/user-attachments/assets/33a1ac3b-a946-4ec7-94ae-a9123aec559d" />


**Plugins installed:**
- SonarQube Scanner 2.18.2 — integrates SonarQube for code quality inspection
- Pipeline Stage View 2.41 — provides a visual pipeline stage dashboard
- Eclipse Temurin Installer — installs the JDK build based on OpenJDK

---

## 🛠️ Stage 0b: SonarQube Scanner Tool Configuration

The SonarQube Scanner version is configured under **Manage Jenkins → Tools**.

<img width="1488" height="977" alt="Screenshot 2026-04-27 053113" src="https://github.com/user-attachments/assets/bb428e85-4494-4904-9d3c-7f159caac7df" />


**SonarQube Scanner 8.1.0.6389** is selected as the scanner version for this pipeline.

---

## 🔄 Jenkins Pipeline Execution

The CI/CD pipeline is executed automatically through Jenkins.

<img width="1914" height="1025" alt="Screenshot 2026-04-27 094841" src="https://github.com/user-attachments/assets/c4e5b5db-ff87-4e73-8026-f40efcf6bd04" />

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

<img width="1919" height="1028" alt="Screenshot 2026-04-27 062236" src="https://github.com/user-attachments/assets/c4100329-b9e6-4fe6-8239-cd17762af581" />


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

<img width="1306" height="628" alt="Screenshot 2026-04-27 084940" src="https://github.com/user-attachments/assets/53fb5864-3f59-4d6b-bb85-5d0d1902e021" />


The initial scan showed **1 condition failed**: 26.7% duplicated lines (threshold ≤ 3.0%). Coverage also showed 0.0% (required ≥ 80.0%).

<img width="1284" height="275" alt="Screenshot 2026-04-27 085914" src="https://github.com/user-attachments/assets/eb5f20d7-299c-4123-992f-ba7919cf437b" />

The **employeeapp** project overview showed `Failed` status with 20.1% duplications.

### SonarQube Issues Identified

**Issue 1 — CSS Contrast (Maintainability / Medium)**

<img width="1906" height="1025" alt="Screenshot 2026-04-27 074729" src="https://github.com/user-attachments/assets/9ce49a5b-5099-4c8b-b77e-96864ee26c83" />


SonarQube flagged a CSS accessibility issue: `Text does not meet the minimal contrast requirement with its background` in `src/main/resources/static/css/style.css` at Line 92 (`color: #fff`).

**Issue 2 — Empty Test Method (Maintainability / High)**

<img width="1908" height="1026" alt="Screenshot 2026-04-27 080615" src="https://github.com/user-attachments/assets/7cdcf9ae-7282-4887-91bc-d648419f1536" />


SonarQube flagged an empty `contextLoads()` method in `JavaCicdPipelineAppApplicationTests.java`: *"Add a nested comment explaining why this method is empty, throw an UnsupportedOperationException or complete the implementation."*

### Fixed Issues

<img width="1909" height="1012" alt="Screenshot 2026-04-27 090015" src="https://github.com/user-attachments/assets/ea02abba-ec1b-4798-8923-086f128a19ec" />

After resolving the flagged issues, all **3 issues** were marked as `Fixed` in SonarQube.

### Quality Gate — Passed ✅

<img width="1919" height="1035" alt="Screenshot 2026-04-27 094855" src="https://github.com/user-attachments/assets/0f952d8e-0241-43dd-a29c-ddecc097aefd" />


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

<img width="1916" height="1025" alt="Screenshot 2026-04-27 095344" src="https://github.com/user-attachments/assets/4cc35d61-479f-47a4-a6a8-1979cde8c826" />

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

<img width="1919" height="987" alt="Screenshot 2026-04-27 100103" src="https://github.com/user-attachments/assets/586f7234-1f95-498b-9544-e7d44766f7d2" />


The **Employee Portal** EC2 instance is deployed and running in **ap-southeast-1b (Singapore)**:

| Property | Value |
|---------|-------|
| Instance ID | i-0b69d0b30eecdd1ee |
| Instance Type | m7i-flex.large |
| Instance State | ✅ Running |
| Public IPv4 | 18.143.76.138 |
| Status Checks | 3/3 passed |

### Security Group — Inbound Rules

<img width="1913" height="990" alt="Screenshot 2026-04-27 100122" src="https://github.com/user-attachments/assets/1b8858be-c4cd-4c24-a779-dd5def42c594" />

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

<img width="1918" height="1024" alt="Screenshot 2026-04-27 095019" src="https://github.com/user-attachments/assets/5a054afe-e506-48c8-b822-cc048dc081ce" />


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
