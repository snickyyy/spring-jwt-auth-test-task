#!/bin/bash
curl -X GET 'localhost:8080/api/v1/admin/users/list' \
  --header 'Authorization: Bearer token'
