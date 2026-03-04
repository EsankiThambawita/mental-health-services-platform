package com.nsbm.health.appointment.security;

/**
* Placeholder for future JWT security configuration.
* JWT-based authentication will be added here in a later phase.
  */
  public final class SecurityPackageInfo {
  private SecurityPackageInfo() {
  }
  }
```

---

That's all 27 files. Here's what the **one API call** to their service does:
```
Your service                         Availability service (port 8082)
│                                          │
│  PUT /api/v1/availability/{id}/book      │
│ ────────────────────────────────────►   │
│                                          │  marks slot BOOKED in their DB
│  ◄────────────────────────────────────  │
│  { availabilityId, counselorId,          │
│    date, startTime, endTime,             │
│    status: "BOOKED" }                    │