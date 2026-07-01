#!/bin/bash

echo "Inicializando Docker Swarm..."
docker swarm init

echo ""
echo "Construyendo imagen techstore-api..."
docker build -t techstore-api:latest .

echo ""
echo "Desplegando stack en Swarm..."
docker stack deploy -c docker-compose.yml techstore

echo ""
sleep 10

echo "Estado de los servicios:"
docker service ls

echo ""
echo "Listo. La aplicacion esta corriendo en http://localhost:8080"