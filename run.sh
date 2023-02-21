#!/bin/bash
docker-compose -f docker-compose.yml -f docker-compose-develop.yml down
docker-compose -f docker-compose.yml -f docker-compose-develop.yml build
docker-compose -f docker-compose.yml -f docker-compose-develop.yml up -d
