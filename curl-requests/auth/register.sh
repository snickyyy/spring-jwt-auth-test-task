#!/bin/bash
curl -X POST 'localhost:8080/api/v1/auth/register'
--header 'Content-Type: application/json'
--data '{
"username": "snicky",
"password": "Admin12."
}'
