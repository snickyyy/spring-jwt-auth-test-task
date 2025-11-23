#!/bin/bash
curl -X DELETE 'localhost:8080/api/v1/admin/users/2/field/role' \
  --header 'Content-Type: application/json' \
  --data '{
  "role": "use1r"
}' \
  --header 'Authorization: Bearer token'
