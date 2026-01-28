## Standard Error Response Format

All errors must follow this JSON structure:

```json
{
  "timestamp": "2026-01-24T10:30:00Z",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Counselor not found",
  "path": "/api/counselors/123"
}
