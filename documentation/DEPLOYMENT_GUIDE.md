# AWS Deployment Guide — Mental Health Services Platform

## Architecture Overview

```
┌──────────────────┐         ┌──────────────────────────────────────┐
│  S3 Static Site  │         │           EC2 Instance                │
│  (Frontend)      │────────▶│  ┌──────────┐  ┌────────────────┐   │
│  HTML/CSS/JS     │         │  │  Nginx   │  │  MongoDB Local  │   │
│                  │         │  │  :80     │  │  :27017         │   │
└──────────────────┘         │  └────┬─────┘  └────────────────┘   │
                             │       │                              │
                             │  ┌────▼──────────────────────────┐  │
                             │  │  Spring Boot Microservices     │  │
                             │  │  :8081 :8082 :8083 :8084       │  │
                             │  │  :8085 :8086 :8087             │  │
                             │  └───────────────────────────────┘  │
                             │                                      │
                             │  auth-service & recovery-plan-service│
                             │  ───▶ MongoDB Atlas (remote)         │
                             └──────────────────────────────────────┘
```

---

## ✅ Pre-deployment Changes (Already Done)

The following changes have been applied to your codebase. **No manual port/URL edits needed.**

### Port Assignments (No Conflicts)
| Service | Port | Database |
|---|---|---|
| resources-mood-tracking-service | 8081 | Local MongoDB |
| availability-management-service | 8082 | Local MongoDB |
| appointment-booking-service | 8083 | Local MongoDB |
| auth-service | 8084 | MongoDB Atlas |
| counselor-directory-service | 8085 | Local MongoDB |
| session-communication-service | 8086 | H2 in-memory |
| recovery-plan-service | 8087 | MongoDB Atlas |

### Inter-service URL Fixes
- `availability-management-service` → counselor-directory reference set to `localhost:8085` ✅
- `appointment-booking-service` → availability reference set to `localhost:8082` ✅
- `recovery-plan-service` → auth-service reference set to `localhost:8084` ✅

### Frontend URL Centralization
- Created `frontend/js/env.js` — single config file for all API URLs ✅
- All JS files now use `ENV.*_BASE` instead of hardcoded `localhost:XXXX` ✅
- All HTML pages include `env.js` as the first script ✅

### Database Setup
- **auth-service** → MongoDB Atlas (kept as-is) ✅
- **recovery-plan-service** → MongoDB Atlas (kept as-is) ✅
- **session-communication-service** → H2 in-memory (no MongoDB needed) ✅
- Other 4 services → local MongoDB ✅

---

## BEFORE DEPLOYING: The One Thing You Must Change

When you have your EC2 Public IP, edit **one file**: `frontend/js/env.js`

```javascript
// Change these two lines:
API_HOST: "http://localhost",     →  API_HOST: "http://<EC2_PUBLIC_IP>",
    USE_DIRECT_PORTS: true,           →  USE_DIRECT_PORTS: false,
```

That's it. All frontend API calls will automatically route through Nginx on port 80.

---

## PART 1: Set Up EC2 Instance (Backend + MongoDB)

### Step 1: Launch EC2 Instance

1. Go to **AWS Console → EC2 → Launch Instance**
2. Settings:
    - **Name**: `mental-health-backend`
    - **AMI**: Amazon Linux 2023
    - **Instance type**: `t3.medium` or `c7i-flex.large` (7 Java services + MongoDB need >= 4GB RAM - `t3.small` 2GB is usually not enough)
    - **Key pair**: Create new -> download `.pem` file
    - **Network / Security Group**:
        - SSH (port 22) - from **My IP** only
        - HTTP (port 80) - from **Anywhere** (0.0.0.0/0)
        - HTTPS (port 443) - from **Anywhere** (0.0.0.0/0)
    - **Storage**: 20 GB gp3
3. Click **Launch Instance**
4. Note the **Public IPv4 address**

### Step 2: SSH into EC2

```bash
# Run from Windows PowerShell on your local machine
ssh -i "your-key.pem" ec2-user@<EC2_PUBLIC_IP>
```

> **Windows .pem fix:** Right-click `.pem` -> Properties -> Security -> Advanced -> Disable inheritance -> Remove all except your user account.

### Step 3: Install Java 17 (+ Maven for auth-service build)

```bash
sudo dnf install java-17-amazon-corretto-devel maven -y

# Verify
java -version
mvn -version
```

### Step 4: Install MongoDB 7.0

```bash
sudo tee /etc/yum.repos.d/mongodb-org-7.0.repo <<EOF
[mongodb-org-7.0]
name=MongoDB Repository
baseurl=https://repo.mongodb.org/yum/amazon/2023/mongodb-org/7.0/x86_64/
gpgcheck=1
enabled=1
gpgkey=https://pgp.mongodb.com/server-7.0.asc
EOF

sudo dnf install mongodb-org -y
sudo systemctl start mongod
sudo systemctl enable mongod
mongosh --eval "db.runCommand({ ping: 1 })"
```

### Step 5: Install Nginx

```bash
sudo dnf install nginx -y
sudo systemctl start nginx && sudo systemctl enable nginx
```

### Step 6: Upload & Build Backend Services

**Option A: Git clone (recommended)**
```bash
sudo dnf install git -y
cd ~ && git clone <YOUR_REPO_URL> mental-health-platform
cd ~/mental-health-platform
git checkout dev/hosting
```

**Option B: SCP from your Windows machine**
```bash
# Run on YOUR LOCAL machine:
scp -i "your-key.pem" -r "D:\NSBM\mental-health-services-platform\backend" ec2-user@<EC2_PUBLIC_IP>:~/mental-health-platform/backend
```

**Build all services:**
```bash
cd ~/mental-health-platform/backend/services

for service in resources-mood-tracking-service \
               counselor-directory-service \
               availability-management-service \
               session-communication-service \
               appointment-booking-service \
               recovery-plan-service \
               auth-service; do
  echo "=== Building $service ==="
  cd "$service"

  if [ -f "mvnw" ]; then
    chmod +x mvnw
    ./mvnw clean package -DskipTests
  else
    mvn clean package -DskipTests
  fi

  cd ..
done
```

### Step 7: Create Systemd Services

Run this script to create all 7 service files:

```bash
# ---- mood-tracking (port 8081, local MongoDB) ----
sudo tee /etc/systemd/system/mood-tracking.service <<'EOF'
[Unit]
Description=Resources & Mood Tracking Service
After=network.target mongod.service

[Service]
User=ec2-user
WorkingDirectory=/home/ec2-user/mental-health-platform/backend/services/resources-mood-tracking-service
ExecStart=/usr/bin/java -jar -Xmx256m target/resources-mood-tracking-service-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# ---- counselor-directory (port 8085, local MongoDB) ----
sudo tee /etc/systemd/system/counselor-directory.service <<'EOF'
[Unit]
Description=Counselor Directory Service
After=network.target mongod.service

[Service]
User=ec2-user
WorkingDirectory=/home/ec2-user/mental-health-platform/backend/services/counselor-directory-service
ExecStart=/usr/bin/java -jar -Xmx256m target/counselor-directory-service-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# ---- availability (port 8082, local MongoDB) ----
sudo tee /etc/systemd/system/availability.service <<'EOF'
[Unit]
Description=Availability Management Service
After=network.target mongod.service

[Service]
User=ec2-user
WorkingDirectory=/home/ec2-user/mental-health-platform/backend/services/availability-management-service
ExecStart=/usr/bin/java -jar -Xmx256m target/availability-management-service-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# ---- session-communication (port 8086, H2 in-memory) ----
sudo tee /etc/systemd/system/session-comm.service <<'EOF'
[Unit]
Description=Session Communication Service
After=network.target

[Service]
User=ec2-user
WorkingDirectory=/home/ec2-user/mental-health-platform/backend/services/session-communication-service
ExecStart=/usr/bin/java -jar -Xmx256m target/session-communication-service-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# ---- appointment-booking (port 8083, local MongoDB) ----
sudo tee /etc/systemd/system/appointment.service <<'EOF'
[Unit]
Description=Appointment Booking Service
After=network.target mongod.service

[Service]
User=ec2-user
WorkingDirectory=/home/ec2-user/mental-health-platform/backend/services/appointment-booking-service
ExecStart=/usr/bin/java -jar -Xmx256m target/appointment-booking-service-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# ---- recovery-plan (port 8087, MongoDB Atlas) ----
sudo tee /etc/systemd/system/recovery-plan.service <<'EOF'
[Unit]
Description=Recovery Plan Service
After=network.target

[Service]
User=ec2-user
WorkingDirectory=/home/ec2-user/mental-health-platform/backend/services/recovery-plan-service
ExecStart=/usr/bin/java -jar -Xmx256m target/recovery-plan-service-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# ---- auth (port 8084, MongoDB Atlas) ----
sudo tee /etc/systemd/system/auth.service <<'EOF'
[Unit]
Description=Auth Service
After=network.target

[Service]
User=ec2-user
WorkingDirectory=/home/ec2-user/mental-health-platform/backend/services/auth-service
ExecStart=/usr/bin/java -jar -Xmx256m target/auth-service-1.0-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF
```

> **Note:** JAR filenames are based on pom.xml artifactId + version. Run `ls target/*.jar` in each service to confirm the exact name.

**Enable and start all:**
```bash
sudo systemctl daemon-reload

for svc in mood-tracking counselor-directory availability session-comm appointment recovery-plan auth; do
  sudo systemctl enable $svc.service
  sudo systemctl start $svc.service
  echo "Started $svc: $(sudo systemctl is-active $svc.service)"
done
```

### Step 8: Configure Nginx Reverse Proxy

```bash
sudo tee /etc/nginx/conf.d/mental-health-api.conf <<'NGINX'
server {
    listen 80;
    server_name _;

    # ---- CORS (needed for S3 → EC2 cross-origin calls) ----
    add_header 'Access-Control-Allow-Origin' '*' always;
    add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, PATCH, DELETE, OPTIONS' always;
    add_header 'Access-Control-Allow-Headers' 'Content-Type, Authorization' always;

    if ($request_method = 'OPTIONS') {
        add_header 'Access-Control-Allow-Origin' '*';
        add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, PATCH, DELETE, OPTIONS';
        add_header 'Access-Control-Allow-Headers' 'Content-Type, Authorization';
        add_header 'Access-Control-Max-Age' 86400;
        add_header 'Content-Length' 0;
        return 204;
    }

    # Auth Service → port 8084
    location /api/auth/ {
        proxy_pass http://127.0.0.1:8084/api/auth/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # Resources & Mood Tracking Service → port 8081
    location /api/v1/mood-entries {
        proxy_pass http://127.0.0.1:8081/api/v1/mood-entries;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location /api/v1/mood-analytics {
        proxy_pass http://127.0.0.1:8081/api/v1/mood-analytics;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location /api/v1/resources {
        proxy_pass http://127.0.0.1:8081/api/v1/resources;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location /api/v1/resource-categories {
        proxy_pass http://127.0.0.1:8081/api/v1/resource-categories;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location /api/v1/counselor-availability {
        proxy_pass http://127.0.0.1:8081/api/v1/counselor-availability;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # Counselor Directory Service → port 8085
    location /api/counselors {
        proxy_pass http://127.0.0.1:8085/api/counselors;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # Availability Management Service → port 8082
    location /api/v1/availability {
        proxy_pass http://127.0.0.1:8082/api/v1/availability;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # Appointment Booking Service → port 8083
    location /api/v1/appointments {
        proxy_pass http://127.0.0.1:8083/api/v1/appointments;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # Recovery Plan Service → port 8087
    location /api/recovery-plans {
        proxy_pass http://127.0.0.1:8087/api/recovery-plans;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # Session Communication Service → port 8086 (WebSocket)
    location /api/sessions {
        proxy_pass http://127.0.0.1:8086/api/sessions;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    location /ws/ {
        proxy_pass http://127.0.0.1:8086/ws/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
    }
}
NGINX

sudo rm -f /etc/nginx/conf.d/default.conf
sudo nginx -t && sudo systemctl restart nginx
```

### Step 9: Update Flow (After You Change Code)

Use this quick flow every time new code is pushed to `dev/hosting`.

**Backend service code changed (Java):**
```bash
cd ~/mental-health-platform
git fetch origin
git checkout dev/hosting
git pull origin dev/hosting

cd backend/services/<service-name>
chmod +x mvnw
./mvnw clean package -DskipTests

sudo systemctl restart <service-systemd-name>.service
sudo systemctl status <service-systemd-name>.service
```

**Frontend only changed (HTML/CSS/JS):**
```bash
cd ~/mental-health-platform
git fetch origin
git checkout dev/hosting
git pull origin dev/hosting

cd frontend
aws s3 sync . s3://<your-bucket-name>/ --delete
```

After uploading frontend files, hard refresh the browser (`Ctrl+F5`) to avoid cached JS.

---

## PART 2: Set Up S3 (Frontend)

### Step 1: Update `env.js` with EC2 IP

Edit `frontend/js/env.js` on your local machine:

```javascript
API_HOST: "http://<YOUR_EC2_PUBLIC_IP>",
USE_DIRECT_PORTS: false,
```

### Step 2: Create S3 Bucket

1. **AWS Console → S3 → Create Bucket**
2. **Bucket name**: `mental-health-frontend-<yourname>` (globally unique)
3. **Region**: Same as your EC2
4. **Uncheck** "Block all public access" → acknowledge warning
5. **Create Bucket**

### Step 3: Enable Static Website Hosting

1. Bucket → **Properties** → **Static website hosting** → Edit → **Enable**
2. **Index document**: `index.html`
3. **Error document**: `404.html`
4. Save → copy **Bucket website endpoint**

> **Note:** S3 index documents cannot contain folder paths. We created a root `index.html` that auto-redirects to `pages/authentication/signin.html`.

### Step 4: Set Bucket Policy

Bucket → **Permissions** → **Bucket Policy**:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::mental-health-frontend-<yourname>/*"
    }
  ]
}
```

### Step 5: Upload Frontend Files

```bash
aws configure   # enter Access Key, Secret, region, json

cd D:\NSBM\mental-health-services-platform\frontend
aws s3 sync . s3://mental-health-frontend-<yourname>/ --delete
```

### Step 6: Open Your Site

```
http://mental-health-frontend-<yourname>.s3-website-<region>.amazonaws.com/pages/authentication/signin.html
```

---

## PART 3: Verify & Troubleshoot

```bash
# On EC2:
mongosh --eval "db.runCommand({ping:1})"

# Check all services
for svc in mood-tracking counselor-directory availability session-comm appointment recovery-plan auth; do
  echo "$svc => $(sudo systemctl is-active $svc.service)"
done

# Direct service checks
curl -s http://localhost:8085/api/counselors
curl -s "http://localhost:8082/api/v1/availability/available?date=2026-03-05"
curl -s http://localhost:8084/api/auth/health

# Through Nginx (port 80)
curl -s http://localhost/api/counselors
curl -s "http://localhost/api/v1/availability/available?date=2026-03-05"
curl -s http://localhost/api/auth/health
```

| Problem | Fix |
|---|---|
| Service won't start | `sudo journalctl -u <service>.service -n 50 --no-pager` |
| MongoDB refused | `sudo systemctl status mongod` |
| `auth-service` build fails with `./mvnw: No such file` | Build with `mvn clean package -DskipTests` |
| CORS errors | Check Nginx headers; ensure frontend uses port-less URLs (`USE_DIRECT_PORTS=false`) |
| 502 Bad Gateway | Backend service crashed or wrong port in Nginx route |
| Wrong JAR name | `ls target/*.jar` in each service -> update `.service` file |
| `PATCH /api/v1/appointments/{id}/reschedule` returns `404 Appointment not found` | Endpoint is working, but the ID does not exist in appointment DB. Use the appointment `_id` (not `counselorId`) from `GET /api/v1/appointments?userName=...` or `mongosh mhs_appointment --eval "db.appointments.find().pretty()"`. |
| Frontend change not visible after S3 upload | Re-upload with `aws s3 sync ... --delete` and hard refresh (`Ctrl+F5`). |

---

## Final Service Map

| Service | Port | Database | Nginx Route |
|---|---|---|---|
| resources-mood-tracking | 8081 | Local MongoDB | /api/v1/mood-entries, /api/v1/resources, etc. |
| counselor-directory | 8085 | Local MongoDB | /api/counselors |
| availability-management | 8082 | Local MongoDB | /api/v1/availability |
| session-communication | 8086 | H2 in-memory | /api/sessions, /ws/ |
| appointment-booking | 8083 | Local MongoDB | /api/v1/appointments |
| recovery-plan | 8087 | **MongoDB Atlas** | /api/recovery-plans |
| auth | 8084 | **MongoDB Atlas** | /api/auth/ |

---

## Recommended: Elastic IP

1. **EC2 → Elastic IPs → Allocate → Associate** with your instance
2. Update `env.js` → re-upload to S3:
   ```bash
   aws s3 cp frontend/js/env.js s3://mental-health-frontend-<yourname>/js/env.js
   ```
