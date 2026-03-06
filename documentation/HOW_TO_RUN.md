# How to Run the Mental Health Services Platform

## Hosted Website

- **URL:** [http://mental-health-service-nsbm-bucket.s3.us-east-1.amazonaws.com/pages/appointments/index.html](http://mental-health-service-nsbm-bucket.s3.us-east-1.amazonaws.com/pages/appointments/index.html)
- This is the main entry point for the platform. You can access all features from this site.

---

## Running Locally (Development)

### 1. **Frontend (Static Site)**
- Open `frontend/index.html` or any page in your browser.
- For API calls to work locally, ensure backend services are running and `frontend/js/env.js` is set to `localhost` and `USE_DIRECT_PORTS: true`.

### 2. **Backend (Microservices)**
- Each service is a Spring Boot Java application.
- Navigate to `backend/services/<service-name>`.
- Build with Maven:
  ```powershell
  .\mvnw clean package -DskipTests
  ```
- Run the JAR:
  ```powershell
  java -jar .\target\<service-jar-name>.jar
  ```
- Repeat for all 7 services:
  - resources-mood-tracking-service
  - availability-management-service
  - appointment-booking-service
  - auth-service
  - counselor-directory-service
  - session-communication-service
  - recovery-plan-service

### 3. **Database**
- **MongoDB:**
  - Install MongoDB locally or use Docker Compose (`compose.yaml` in counselor-directory-service).
  - For production, some services use MongoDB Atlas (cloud).
- **H2:**
  - Session Communication Service uses H2 in-memory (no setup needed).

### 4. **API Configuration**
- Edit `frontend/js/env.js` to match your environment:
  - Local: `API_HOST: "http://localhost"`, `USE_DIRECT_PORTS: true`
  - Production: `API_HOST: "http://<EC2_PUBLIC_IP>"`, `USE_DIRECT_PORTS: false`

---

## Running in Production (AWS)

### 1. **Backend Setup (EC2)**
- Launch an EC2 instance (Amazon Linux 2023, >=4GB RAM).
- SSH into EC2.
- Install Java 17, Maven, MongoDB, Nginx.
- Clone your repo or upload backend code.
- Build all services and create systemd service files (see DEPLOYMENT_GUIDE.md).
- Start all services.
- Configure Nginx as a reverse proxy (routes all API calls to correct microservice).

### 2. **Frontend Setup (S3)**
- Create an S3 bucket and enable static website hosting.
- Upload all files from `frontend/`.
- Set bucket policy for public read access.
- Update `env.js` with EC2 public IP and set `USE_DIRECT_PORTS: false`.

### 3. **Access the Platform**
- Open the hosted URL:
  - [http://mental-health-service-nsbm-bucket.s3.us-east-1.amazonaws.com/pages/appointments/index.html](http://mental-health-service-nsbm-bucket.s3.us-east-1.amazonaws.com/pages/appointments/index.html)
- All frontend API calls are routed through Nginx on EC2.

---

## Troubleshooting
- If a backend service fails, check logs with:
  ```bash
  sudo journalctl -u <service>.service -n 50 --no-pager
  ```
- For MongoDB issues:
  ```bash
  sudo systemctl status mongod
  ```
- For frontend changes not visible:
  - Re-upload files to S3 and hard refresh browser (`Ctrl+F5`).

---

## Summary
- Access the platform via the hosted URL.
- For local development, run all backend services and open frontend files in your browser.
- For production, follow AWS deployment steps and update configuration as needed.

---

For detailed deployment steps, see `documentation/DEPLOYMENT_GUIDE.md`.

