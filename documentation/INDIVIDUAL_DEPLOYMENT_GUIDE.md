# Individual Service Deployment Guide

> **Purpose:** Each team member deploys their own microservice on their own EC2 instance to demonstrate individual hosting capability.
>
> **Important:** The fully working platform still needs all services on one machine (see `DEPLOYMENT_GUIDE.md`). This guide is for individual demonstration only.

---

## Team Service Assignments

| Member | Service | Port | Database | Depends On |
|---|---|---|---|---|
| Member 1 | resources-mood-tracking-service | 8081 | Local MongoDB | None |
| Member 2 | counselor-directory-service | 8085 | Local MongoDB | None |
| Member 3 | availability-management-service | 8082 | Local MongoDB | counselor-directory (Member 2) |
| Member 4 | appointment-booking-service | 8083 | Local MongoDB | availability (Member 3) |
| Member 5 | auth-service | 8084 | MongoDB Atlas | None |
| Member 5 | recovery-plan-service | 8087 | MongoDB Atlas | auth-service (self) |
| Any | session-communication-service | 8086 | H2 (embedded) | None |

> **Note:** Some services call other services. See the "Cross-Service Communication" section below.

---

## Step-by-Step: Deploy YOUR Service on Your Own EC2

### 1. Launch Your EC2 Instance

1. **AWS Console → EC2 → Launch Instance**
2. Settings:
    - **Name**: `mental-health-<your-service-name>` (e.g., `mental-health-mood-tracking`)
    - **AMI**: Amazon Linux 2023
    - **Instance type**: `t3.small` (2GB RAM is enough for 1 service)
    - **Key pair**: Create/use your own `.pem`
    - **Security Group** — open these ports:
        - **22** (SSH) — My IP only
        - **Your service port** (e.g., 8081) — Anywhere (0.0.0.0/0)
        - **80** (HTTP) — Anywhere (optional, if you add Nginx)
    - **Storage**: 15 GB gp3
3. Note your **Public IPv4 address**

### 2. SSH Into Your Instance

```bash
# Windows PowerShell / Git Bash
ssh -i "your-key.pem" ec2-user@<YOUR_EC2_IP>
```

### 3. Install Java 17

```bash
sudo dnf install java-17-amazon-corretto-devel -y
java -version
```

### 4. Install MongoDB (skip if your service uses H2 or Atlas)

**You need local MongoDB if your service is:**
- resources-mood-tracking-service
- counselor-directory-service
- availability-management-service
- appointment-booking-service

**You do NOT need MongoDB if your service is:**
- auth-service (uses MongoDB Atlas — cloud-hosted)
- recovery-plan-service (uses MongoDB Atlas — cloud-hosted)
- session-communication-service (uses H2 in-memory)

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

### 5. Upload Your Service

**Option A: Git clone (recommended)**
```bash
sudo dnf install git -y
cd ~
git clone <YOUR_REPO_URL> mental-health-platform
cd ~/mental-health-platform
git checkout dev/hosting
```

**Option B: SCP just your service from your Windows machine**
```bash
# Run on YOUR LOCAL machine — replace <service-name> with yours:
scp -i "your-key.pem" -r "D:\NSBM\mental-health-services-platform\backend\services\<service-name>" ec2-user@<YOUR_EC2_IP>:~/
```

### 6. Build Your Service

```bash
# If you used git clone:
cd ~/mental-health-platform/backend/services/<your-service-name>

# If you used SCP:
cd ~/<your-service-name>

# Build
chmod +x mvnw
./mvnw clean package -DskipTests
```

### 7. Run Your Service

**Quick test (foreground — see logs directly):**
```bash
java -jar target/<your-jar-name>.jar
```

**JAR names per service:**
| Service | JAR Name |
|---|---|
| resources-mood-tracking-service | `resources-mood-tracking-service-0.0.1-SNAPSHOT.jar` |
| counselor-directory-service | `counselor-directory-service-0.0.1-SNAPSHOT.jar` |
| availability-management-service | `availability-management-service-0.0.1-SNAPSHOT.jar` |
| session-communication-service | `session-communication-service-0.0.1-SNAPSHOT.jar` |
| appointment-booking-service | `appointment-booking-service-0.0.1-SNAPSHOT.jar` |
| recovery-plan-service | `recovery-plan-service-0.0.1-SNAPSHOT.jar` |
| auth-service | `auth-service-1.0-SNAPSHOT.jar` |

**Production (background with systemd):**
```bash
sudo tee /etc/systemd/system/my-service.service <<EOF
[Unit]
Description=My Mental Health Service
After=network.target

[Service]
User=ec2-user
WorkingDirectory=/home/ec2-user/mental-health-platform/backend/services/<your-service-name>
ExecStart=/usr/bin/java -jar -Xmx512m target/<your-jar-name>.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable my-service
sudo systemctl start my-service
sudo systemctl status my-service
```

### 8. Verify It's Running

```bash
# Check service status
sudo systemctl status my-service

# Check logs
sudo journalctl -u my-service -n 50 --no-pager

# Test the API (replace port with yours)
curl http://localhost:<YOUR_PORT>/swagger-ui.html
```

**From your browser (replace with your EC2 IP and port):**
```
http://<YOUR_EC2_IP>:<YOUR_PORT>/swagger-ui.html
```

### 9. Update Workflow (When You Push New Code)

When your latest code is on `dev/hosting`, update your EC2 using this flow:

```bash
cd ~/mental-health-platform
git fetch origin
git checkout dev/hosting
git pull origin dev/hosting

cd backend/services/<your-service-name>
chmod +x mvnw
./mvnw clean package -DskipTests

sudo systemctl restart my-service
sudo systemctl status my-service
```

If your change is frontend-only (HTML/CSS/JS), no Java rebuild is needed:

```bash
cd ~/mental-health-platform/frontend
aws s3 sync . s3://<your-bucket-name>/ --delete
```

Then hard refresh browser (`Ctrl+F5`) to clear cached JS/CSS.

---

## Cross-Service Communication (Important!)

Some services call other services over HTTP. When deployed individually, these calls will fail because the other service isn't on the same machine. Here's how to handle it:

### Services with NO dependencies (can run fully standalone):
- ✅ **resources-mood-tracking-service** — fully standalone
- ✅ **counselor-directory-service** — fully standalone
- ✅ **auth-service** — fully standalone (uses MongoDB Atlas)
- ✅ **session-communication-service** — fully standalone (uses H2)

### Services WITH dependencies:

#### availability-management-service → needs counselor-directory-service
The availability service validates counselor IDs by calling the counselor-directory-service.

**Option A: Point to Member 2's EC2** — Edit `application.yaml` before building:
```yaml
services:
  counselor-directory:
    base-url: http://<MEMBER_2_EC2_IP>:8085
```

**Option B: It already handles this gracefully** — If the counselor-directory-service is unreachable, the `CounselorDirectoryClient` logs a warning and skips validation. Slots can still be created.

#### appointment-booking-service → needs availability-management-service
Edit `application.yaml` before building:
```yaml
availability:
  base-url: http://<MEMBER_3_EC2_IP>:8082
```

If reschedule fails with `Appointment not found`, verify you are passing appointment `_id` (not `counselorId`).

#### recovery-plan-service → needs auth-service
Edit `application.properties` before building:
```properties
auth.service.url=http://<MEMBER_5_EC2_IP>:8084
```
> If the same person owns both auth + recovery-plan, deploy both on the same EC2.

---

## What to Show Your Lecturer

For each member, demonstrate:

1. **Your EC2 is running** — SSH in, show `sudo systemctl status my-service`
2. **Your API responds** — Open `http://<YOUR_EC2_IP>:<PORT>/swagger-ui.html` in browser
3. **Test an endpoint** — Use Swagger UI or curl to hit your API:

```bash
# Examples per service:

# Mood Tracking (8081)
curl http://<IP>:8081/api/v1/mood-entries

# Counselor Directory (8085)
curl http://<IP>:8085/api/counselors

# Availability (8082)
curl http://<IP>:8082/api/v1/availability/available?date=2026-03-05

# Appointment Booking (8083)
curl http://<IP>:8083/api/v1/appointments?userName=test

# Auth Service (8084)
curl -X POST http://<IP>:8084/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@test.com","password":"pass123","role":"PATIENT"}'

# Recovery Plan (8087)
curl http://<IP>:8087/api/recovery-plans/patient/plans \
  -H "Authorization: Bearer <token>"

# Session Communication (8086)
curl http://<IP>:8086/h2-console
```

4. **Show logs** — `sudo journalctl -u my-service -n 20 --no-pager`
5. **Show the code is yours** — Point to the Git commit history for your service

---

## Quick Reference: EC2 Instance Per Person

| Member | Service | Instance Type | Needs MongoDB? | Port to Open |
|---|---|---|---|---|
| Member 1 | mood-tracking | t3.small | ✅ Local | 8081 |
| Member 2 | counselor-directory | t3.small | ✅ Local | 8085 |
| Member 3 | availability-management | t3.small | ✅ Local | 8082 |
| Member 4 | appointment-booking | t3.small | ✅ Local | 8083 |
| Member 5 | auth-service + recovery-plan | t3.small | ❌ Atlas | 8084, 8087 |
| Any | session-communication | t3.small | ❌ H2 | 8086 |

> **Cost tip:** `t3.small` is enough for a single service (~$0.02/hr). Stop your instance when not demoing to save costs.

---

## Troubleshooting

| Problem | Fix |
|---|---|
| `connection refused` on port | Check security group has port open + service is running |
| Service crashes on start | `sudo journalctl -u my-service -n 50 --no-pager` |
| MongoDB connection failed | `sudo systemctl status mongod` |
| Build fails (mvnw) | `chmod +x mvnw` then retry |
| Out of memory | Use `-Xmx512m` flag or upgrade to `t3.medium` |
| Can't reach other member's service | Check their EC2 IP + security group allows your port |
| Appointment reschedule gives 404 in appointment-booking-service | Use appointment `_id` from `GET /api/v1/appointments?userName=...` or `mongosh mhs_appointment --eval "db.appointments.find().pretty()"`; do not use `counselorId`. |
| Frontend update uploaded but old UI still appears | Run `aws s3 sync ... --delete` and hard refresh (`Ctrl+F5`). |

