
.PHONY: postgres kafka kafka-ui user-service \
        start-infra stop-infra docker-build docker-up docker-down

# PostgreSQL
postgres:
	docker run -d \
		--name postgres \
		-e POSTGRES_DB=commercehub \
		-e POSTGRES_USER=commercehub \
		-e POSTGRES_PASSWORD=commercehub_dev \
		-p 5433:5432 \
		-v commercehub_postgres_data:/var/lib/postgresql \
		postgres:19
# docker network create commercehub-network
# Kafka
kafka:
	docker run -d \
		--name kafka \
		--network commercehub-network \
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
		apache/kafka:latest

# Kafka UI
kafka-ui:
	docker run -d \
		--name kafka-ui \
		--network commercehub-network \
		-p 8081:8080 \
		-e KAFKA_CLUSTERS_0_NAME=local \
		-e KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS=kafka:29092 \
		provectuslabs/kafka-ui:latest

# Start existing infrastructure containers
start-infra:
	docker start postgres kafka kafka-ui

# Stop infrastructure containers
stop-infra:
	docker stop user-service kafka-ui kafka postgres

# Build user-service Docker image
user-service-build:
	docker image build -f Dockerfile -t user-service:1.0 ..

# Run user-service container
user-service:
	docker run -d \
		--name user-service \
		--network commercehub-network \
		-p 8080:8080 \
		-e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5433/commercehub \
		-e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:29092 \
		user-service:1.0

# Build image and run Spring Boot container
# user-service:
# 	docker image build -f Dockerfile -t user-service:1.0 .. && \
# 	docker run -d \
# 		--name user-service \
# 		-p 8080:8080 \
# 		user-service:1.0

# Docker Compose commands (for future use)
docker-up:
	docker compose up -d

docker-down:
	docker compose down

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
docker-rmi:
	docker rmi example:1.0


# ==========================
# Docker Container Commands
# ==========================

# Create and run container from image
docker-run:
	docker run -d --name example-container example:1.0

# Stop container
docker-stop:
	docker stop example-container

# Start existing container
docker-start:
	docker start example-container

# Restart container
docker-restart:
	docker restart example-container

# List running containers
docker-ps:
	docker ps

# List all containers
docker-ps-all:
	docker ps -a

# Remove container
docker-rm:
	docker rm example-container

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

# Have to authenticate to ghcr.io first
# https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry#authenticating-to-the-container-registry
.PHONY: push-github-packages
push-github-packages:
	docker tag user-service ghcr.io/kaderyeniyildiz/user-service # defaults to latest
	docker push ghcr.io/kaderyeniyildiz/user-service

	docker tag user-service ghcr.io/kaderyeniyildiz/user-service:1.0 # defaults to latest
	docker push ghcr.io/kaderyeniyildiz/user-service:1.0