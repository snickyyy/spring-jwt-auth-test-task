#!/bin/bash
curl -X POST 'localhost:8080/api/v1/auth/login' \
  --header 'Content-Type: application/json' \
  --data '{
  "username": "admin",
  "password": "Admin12."
}'