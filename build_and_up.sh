docker compose down
gradle clean build
docker compose build resource_server
docker compose up -d postgres minio cache_redis
docker compose up -d resource_server
