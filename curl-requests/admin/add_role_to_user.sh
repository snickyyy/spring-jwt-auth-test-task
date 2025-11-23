#!/bin/bash
curl -X PATCH 'localhost:8080/api/v1/admin/users/2/field/role' \
  --header 'Content-Type: application/json' \
  --data '{
  "role": "user"
}' \
  --header 'Authorization: Bearer token'