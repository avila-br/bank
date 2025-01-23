DOCKER_COMPOSE_FILE := compose.yml

# Target: Starts all containers defined in the Docker Compose file.
up:
	@echo "Starting $(DOCKER_COMPOSE_FILE)..."
	@docker compose -f $(DOCKER_COMPOSE_FILE) up -d
	@echo "$(DOCKER_COMPOSE_FILE) started successfully!"

# Target: Stop the containers without removing them, their volumes or their images.
stop:
	@echo "Stopping $(DOCKER_COMPOSE_FILE)..."
	@docker compose -f $(DOCKER_COMPOSE_FILE) stop
	@echo "$(DOCKER_COMPOSE_FILE) stopped!"

# Target: Removes the containers without removing their volumes or images.
down:
	@echo "Removing $(DOCKER_COMPOSE_FILE)..."
	@docker compose -f $(DOCKER_COMPOSE_FILE) down
	@echo "$(DOCKER_COMPOSE_FILE) removed!"

# Target: Completely removes containers, volumes, and images.
clear:
	@echo "Removing containers, volumes, and images associated with $(DOCKER_COMPOSE_FILE)..."
	@docker compose -f $(DOCKER_COMPOSE_FILE) down -v --rmi all
	@echo "All containers, volumes, and images removed!"

# Target: Restarts containers without removing their volumes or images.
refresh:
	@echo "Refreshing $(DOCKER_COMPOSE_FILE)..."
	@docker compose -f $(DOCKER_COMPOSE_FILE) down
	@docker compose -f $(DOCKER_COMPOSE_FILE) up --build --force-recreate -d
	@echo "$(DOCKER_COMPOSE_FILE) refreshed!"

# Target: Stops and removes all containers, networks, and volumes defined in the Docker Compose file.
down-v:
	@echo "Shutting down all containers, networks, and volumes defined in $(DOCKER_COMPOSE_FILE)..."
	@docker compose -f $(DOCKER_COMPOSE_FILE) down -v
	@echo "All containers, networks, and volumes have been removed."

# Target: Stops all containers and removes images built by the Docker Compose file.
down-i:
	@echo "Shutting down all containers and removing all images built by $(DOCKER_COMPOSE_FILE)..."
	@docker compose -f $(DOCKER_COMPOSE_FILE) down --rmi all
	@echo "All containers have been stopped, and images removed."

# Target: Displays logs of services defined in the Docker Compose file in real-time.
logs:
	@echo "Displaying logs for services defined in $(DOCKER_COMPOSE_FILE) in real-time..."
	@docker compose -f $(DOCKER_COMPOSE_FILE) logs -f