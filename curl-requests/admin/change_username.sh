#!/bin/bash
curl -X PATCH 'localhost:8080/api/v1/admin/users/2/field/username' \
  --header 'Content-Type: application/json' \
  --data '{
  "new_username": "anton"
}' \
  --header 'Authorization: Bearer token'