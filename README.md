# ShopStream - Event-driven e-commerce (dev)

Run dev infra:
cd docker
docker compose up -d

Run service (example):
cd product-service
mvn spring-boot:run

Health: http://localhost:8080/health (port depends on service)
