
.PHONY: network postgres kafka kafka-ui \
        user-service-build user-service \
        start-infra stop-infra docker-rm docker-rmi\
        logs ps clean

network:
	docker network inspect commercehub-network >/dev/null 2>&1 || \
	docker network create commercehub-network

# PostgreSQL
postgres:
	docker run -d \
		--name postgres \
		--network commercehub-network \
		--restart unless-stopped \
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
		--network commercehub-network \
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
		--network commercehub-network \
		-p 8080:8080 \
		-e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/commercehub \
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
# 	-docker network rm commercehub-network

# Remove CommerceHub Docker images
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
   # docker volume inspect commercehub_postgres_data

# docker network --help
   # docker network ls
   # docker network connect commercehub-network user-service
   # docker network inspect 4fb84a5552d8
   # docker network disconnect commercehub-network user-service