# Load environment variables
ifneq (,$(wildcard .env))
include .env
export DB_USERNAME DB_PASSWORD
endif

.PHONY: network postgres kafka kafka-ui \
        user-service-build user-service \
        start-infra stop-infra docker-rm docker-rmi\
        logs ps clean

network:
	docker network inspect devlab-network >/dev/null 2>&1 || \
	docker network create devlab-network

# PostgreSQL
postgres:
	docker run -d \
		--name postgres \
		--network devlab-network \
		--restart unless-stopped \
		-e POSTGRES_DB=devlab \
		-e POSTGRES_USER \
		-e POSTGRES_PASSWORD \
		-p 5433:5432 \
		-v devlab_postgres_data:/var/lib/postgresql \
		postgres:19
# docker network create devlab-network
# Kafka
kafka:
	docker run -d \
		--name kafka \
		--network devlab-network \
		--restart unless-stopped \
		-p 9092:9092 \
		-e KAFKA_NODE_ID=1 \
		-e KAFKA_PROCESS_ROLES=broker,controller \
		-e KAFKA_LISTENERS=EXTERNAL://:9092,INTERNAL://:29092,CONTROLLER://:9093 \
		-e KAFKA_ADVERTISED_LISTENERS=EXTERNAL://localhost:9092,INTERNAL://kafka:29092 \
		-e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER \
		-e KAFKA_CONTROLLER_QUORUM_VOTERS=1@localhost:9093 \
		-e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,EXTERNAL:PLAINTEXT,INTERNAL:PLAINTEXT \
		-e KAFKA_INTER_BROKER_LISTENER_NAME=INTERNAL \
		-e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
		-e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
		-e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
		apache/kafka:4.1.1

# Kafka UI
kafka-ui:
	docker run -d \
		--name kafka-ui \
		--network devlab-network \
		--restart unless-stopped \
		-p 8081:8080 \
		-e KAFKA_CLUSTERS_0_NAME=local \
		-e KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS=kafka:29092 \
		provectuslabs/kafka-ui:latest

# Build user-service Docker image
user-service-build:
	docker image build -f Dockerfile -t user-service:1.0.0 ..

# Run user-service container
user-service:
	docker run -d \
		--name user-service \
		--network devlab-network \
		-p 8080:8080 \
		-e SPRING_PROFILES_ACTIVE=dev \
		-e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/devlab \
		-e DB_USERNAME \
		-e DB_PASSWORD \
		-e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:29092 \
		user-service:1.0.0

# Start existing infrastructure containers
start-infra:
	docker start postgres kafka kafka-ui user-service

# Stop infrastructure containers
stop-infra:
	docker stop user-service kafka-ui kafka postgres

# Remove containers
docker-rm:
	-docker container rm -f user-service
	-docker container rm -f kafka-ui
	-docker container rm -f kafka
	-docker container rm -f postgres
# 	-docker network rm devlab-network

# Remove DevLab Docker images
docker-rmi:
	-docker image rm -f user-service:1.0.0
	-docker image rm -f postgres:19
	-docker image rm -f apache/kafka:4.1.1
	-docker image rm -f provectuslabs/kafka-ui:latest

# ==========================
# DOCKER COMPOSE COMMANDS
# ==========================

.PHONY: compose-build compose-up compose-up-build \
        compose-down compose-ps compose-restart \
        compose-logs compose-logs-user

# Build all Docker images
compose-build:
	docker compose build

# Start all containers
compose-up:
	docker compose up -d

# Build images and start all containers
compose-up-build:
	docker compose up -d --build

# Stop and remove containers (preserve volumes)
compose-down:
	docker compose down

# Show container status
compose-ps:
	docker compose ps

# Restart all containers
compose-restart:
	docker compose restart

# Follow logs from all services
compose-logs:
	docker compose logs -f

# Follow User Service logs
compose-logs-user:
	docker compose logs -f user-service

# ==========================
# Docker Image Commands
# ==========================

# Build image from Dockerfile
docker-build:
	docker build -t example:1.0 .

# List Docker images
docker-images:
	docker images

# Remove image
# docker-rmi:
# 	docker rmi example:1.0

# ==========================
# Docker Container Commands
# ==========================

# Create and run container from image
# docker run -d --name example-container example:1.0

# Stop container
# docker stop example-container

# Start existing container
# docker start example-container

# Restart container
# docker restart example-container

# List running containers
# docker ps

# List all containers
# docker ps -a

# docker exec user-service whoami

# docker network list

#docker compose config

# ==========================
# Docker Container Registries
# ==========================

# Create and run container from image
# Build user-service Docker image
# If you have already built the image, you don't need to do it again as shown above.
.PHONY: build
build-dockerhub:
	docker image build -f Dockerfile -t user-service ..

# Have to authenticate to dockerhub and create repo first:
# https://docs.docker.com/engine/reference/commandline/login/
.PHONY: push-dockerhub
push-dockerhub:
	docker tag user-service kaderyeniyildiz/user-service # defaults to latest
	docker push kaderyeniyildiz/user-service

	docker tag user-service kaderyeniyildiz/user-service:1.0
	docker push kaderyeniyildiz/user-service:1.0

	docker tag user-service:1.0.0 kaderyeniyildiz/user-service:1.0.0
	docker push kaderyeniyildiz/user-service:1.0.0

# Have to authenticate to ghcr.io first
# https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry#authenticating-to-the-container-registry
.PHONY: push-github-packages
push-github-packages:
	docker tag user-service ghcr.io/kaderyeniyildiz/user-service # defaults to latest
	docker push ghcr.io/kaderyeniyildiz/user-service

	docker tag user-service ghcr.io/kaderyeniyildiz/user-service:1.0 # defaults to latest
	docker push ghcr.io/kaderyeniyildiz/user-service:1.0

	docker tag user-service ghcr.io/kaderyeniyildiz/user-service:1.0.0 # defaults to latest
	docker push ghcr.io/kaderyeniyildiz/user-service:1.0.0


# ==========================
# Docker Container Security
# ==========================
.PHONY: docker-security-scan
docker-security-scan:
	trivy image --severity HIGH,CRITICAL user-service:1.0.0

#Docker image security check and analize
# docker scout quickview user-service:1.0.0 #https://docs.docker.com/scout/?utm_source=chatgpt.com
# trivy image user-service:1.0.0 #https://trivy.dev/?utm_source=chatgpt.com
# Snyk #https://snyk.io/?utm_source=chatgpt.com
# https://www.chainguard.dev/ #https://www.chainguard.dev/docs/open-source/wolfi?utm_source=chatgpt.com (sample small image)


# ==========================
# Docker COMMAND Help
# ==========================
# docker --help
# docker image --help
   # docker image history user-service:1.0.0
   # docker image inspect user-service:1.0.0
   # docker image prune
   # docker image ls
   # docker image rm user-service:1.0.0
   # docker image ls | grep postgres

# docker container --help
   # docker container logs user-service -f
   # docker container inspect user-service
   # docker container prune
   # docker container ls

# docker volume --help
   # docker volume create my-volume
   # docker volume ls
   # docker volume inspect devlab_postgres_data

# docker network --help
   # docker network ls
   # docker network connect devlab-network user-service
   # docker network inspect 4fb84a5552d8
   # docker network disconnect devlab-network user-service

# ==========================
# Docker COMMAND Help
# ==========================
.PHONY: dev debug test

# Start development environment
dev:
	docker compose \
		-f docker-compose.yml \
		-f docker-compose-dev.yml \
		up -d --build

# Start development environment with remote debugging
debug:
	docker compose \
		-f docker-compose.yml \
		-f docker-compose-dev.yml \
		-f docker-compose-debug.yml \
		up -d --build user-service

# Run tests inside Docker
test:
	docker compose \
		-f docker-compose.yml \
		-f docker-compose-dev.yml \
		-f docker-compose-test.yml \
		run --rm --no-deps user-service

########################

# ==========================
# Docker Swarm Configuration
# ==========================

SHELL := /bin/bash

STACK_NAME := devlab
COMPOSE_FILE := docker-compose-swarm.yml
USER_SERVICE := $(STACK_NAME)_user-service

# ==========================
# PHONY
# ==========================

.PHONY: help swarm-init deploy stack-status stack-ps stack-remove \
        service-ps secrets-create secrets-list secrets-remove \
        logs logs-tail logs-follow container-id container-logs \
        container-inspect container-env healthcheck healthcheck-status

# ==========================
# Docker COMMAND Help
# ==========================

help:
	@echo "Docker Swarm Commands"
	@echo ""
	@echo "Swarm:"
	@echo "  make swarm-init"
	@echo "  make deploy"
	@echo "  make stack-status"
	@echo "  make stack-ps"
	@echo "  make service-ps"
	@echo "  make stack-remove"
	@echo ""
	@echo "Secrets:"
	@echo "  make secrets-create"
	@echo "  make secrets-list"
	@echo "  make secrets-remove"
	@echo ""
	@echo "Logs:"
	@echo "  make logs"
	@echo "  make logs-tail"
	@echo "  make logs-follow"
	@echo ""
	@echo "Container:"
	@echo "  make container-id"
	@echo "  make container-logs"
	@echo "  make container-inspect"
	@echo "  make container-env"
	@echo ""
	@echo "Healthcheck:"
	@echo "  make healthcheck"
	@echo "  make healthcheck-status"

# ==========================
# Docker Swarm
# ==========================

swarm-init:
	docker swarm init

deploy:
	docker stack deploy -c $(COMPOSE_FILE) $(STACK_NAME)

stack-status:
	docker stack services $(STACK_NAME)

stack-ps:
	docker stack ps $(STACK_NAME)

stack-remove:
	docker stack rm $(STACK_NAME)

service-ps:
	docker service ps $(USER_SERVICE)

# ==========================
# Docker Secrets
# ==========================

secrets-create:
	set -a; source .env; set +a; \
	printf '%s' "$$DB_USERNAME" | docker secret create db_username -; \
	printf '%s' "$$DB_PASSWORD" | docker secret create db_password -

secrets-list:
	docker secret ls

secrets-remove:
	docker secret rm db_username db_password

# ==========================
# Service Logs
# ==========================

logs:
	docker service logs $(USER_SERVICE)

logs-tail:
	docker service logs --tail 100 $(USER_SERVICE)

logs-follow:
	docker service logs -f $(USER_SERVICE)

# ==========================
# Container Operations
# ==========================

container-id:
	@docker ps --filter name=$(USER_SERVICE) --format '{{.ID}}' | head -n 1

container-logs:
	@docker logs $$(docker ps --filter name=$(USER_SERVICE) --format '{{.ID}}' | head -n 1)

container-inspect:
	@docker inspect $$(docker ps --filter name=$(USER_SERVICE) --format '{{.ID}}' | head -n 1)

container-env:
	@docker exec $$(docker ps --filter name=$(USER_SERVICE) --format '{{.ID}}' | head -n 1) printenv

# ==========================
# Healthcheck
# ==========================

healthcheck:
	@docker inspect $$(docker ps --filter name=$(USER_SERVICE) --format '{{.ID}}' | head -n 1) --format '{{json .State.Health.Log}}'

healthcheck-status:
	@docker inspect $$(docker ps --filter name=$(USER_SERVICE) --format '{{.ID}}' | head -n 1) --format '{{.State.Health.Status}}'
#docker inspect <container_id> --format '{{json .State.Health.Log}}'
#docker inspect <container_id> --format '{{json .State.Health.Status}}'