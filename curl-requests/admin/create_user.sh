#!/bin/bash
curl -X POST 'localhost:8080/api/v1/admin/users' \
  --header 'Content-Type: application/json' \
  --data '{
  "username": "Tressie99",
  "password": "pass1221P.",
  "is_active": true
}' \
  --header 'Authorization: Bearer token'
