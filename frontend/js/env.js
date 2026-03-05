// ============================================================
// ENV.js — Single config file for ALL API URLs
// ============================================================
// For LOCAL development:  keep as-is (localhost + direct ports)
// For AWS deployment:     change API_HOST to EC2 IP, set USE_DIRECT_PORTS to false
// ============================================================

const ENV = {
  // Base host for all APIs
  API_HOST: "http://localhost",

  // true  = call each microservice port directly (local dev)
  // false = route everything through Nginx on port 80 (production)
  USE_DIRECT_PORTS: true,

  // ---------- Service Ports (used when USE_DIRECT_PORTS = true) ----------
  PORTS: {
    MOOD_TRACKING:       8081,
    AVAILABILITY:        8082,
    APPOINTMENT:         8083,
    AUTH:                8084,
    COUNSELOR_DIRECTORY: 8085,
    SESSION_COMM:        8086,
    RECOVERY_PLAN:       8087,
  },

  // ---------- Computed base URLs ----------
  get MOOD_TRACKING_BASE()       { return this.USE_DIRECT_PORTS ? `${this.API_HOST}:${this.PORTS.MOOD_TRACKING}`       : this.API_HOST; },
  get AVAILABILITY_BASE()        { return this.USE_DIRECT_PORTS ? `${this.API_HOST}:${this.PORTS.AVAILABILITY}`        : this.API_HOST; },
  get APPOINTMENT_BASE()         { return this.USE_DIRECT_PORTS ? `${this.API_HOST}:${this.PORTS.APPOINTMENT}`         : this.API_HOST; },
  get AUTH_BASE()                { return this.USE_DIRECT_PORTS ? `${this.API_HOST}:${this.PORTS.AUTH}`                : this.API_HOST; },
  get COUNSELOR_DIRECTORY_BASE() { return this.USE_DIRECT_PORTS ? `${this.API_HOST}:${this.PORTS.COUNSELOR_DIRECTORY}` : this.API_HOST; },
  get SESSION_COMM_BASE()        { return this.USE_DIRECT_PORTS ? `${this.API_HOST}:${this.PORTS.SESSION_COMM}`        : this.API_HOST; },
  get RECOVERY_PLAN_BASE()       { return this.USE_DIRECT_PORTS ? `${this.API_HOST}:${this.PORTS.RECOVERY_PLAN}`       : this.API_HOST; },
};

