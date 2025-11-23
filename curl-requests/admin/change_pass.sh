#!/bin/bash
curl -X PATCH 'localhost:8080/api/v1/admin/users/2/field/password' \
  --header 'Content-Type: application/json' \
  --data '{
  "new_password": "anton"
}' \
  --header 'Authorization: token'
