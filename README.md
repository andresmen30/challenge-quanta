# 🚀 Challenge Quanta | BOM Microservice

<p align="left">
  <img src="https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white" alt="Java 21">
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.4-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/WebFlux-Reactive-6DB33F" alt="WebFlux">
  <img src="https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white" alt="Maven">
  <img src="https://img.shields.io/badge/Database-H2%20(R2DBC)-0A6C74" alt="H2 R2DBC">
  <img src="https://img.shields.io/badge/Architecture-Hexagonal-111827" alt="Hexagonal Architecture">
</p>

Microservicio reactivo para gestionar una **BOM (Bill of Materials)**: creación de productos, asociación de materiales por producto y cálculo de materiales requeridos para producción.

## ✨ Características

- ⚡ API reactiva con **Spring WebFlux** y endpoints funcionales.
- 📘 Documentación OpenAPI 3 y Swagger UI integrada.
- 🧱 Diseño con **arquitectura hexagonal** (puertos y adaptadores).
- 🧮 Cálculo de requerimientos de materiales para lotes de producción.
- ✅ Validaciones de entrada y manejo de errores unificado.
- 🧪 Cobertura con pruebas unitarias y de integración reactiva.

## 🛠️ Stack técnico

- `Java 21`
- `Spring Boot 4.0.4`
- `Spring WebFlux`
- `Spring Data R2DBC`
- `H2 en memoria (R2DBC)`
- `Springdoc OpenAPI + Swagger UI`
- `MapStruct`
- `Lombok`
- `Maven Wrapper`

## 🧭 Arquitectura

```text
domain
  model
  port.in
  port.out
application
  service
infrastructure
  adapter.in
    dto
    handler
    router
  adapter.out
    persistence
      entity
      repository
      mapper
      adapter
config
shared.exception
```

## ⚙️ Ejecución local

### Requisitos

- Java 21

### Levantar el servicio

```bash
./mvnw clean spring-boot:run
```

### Ejecutar pruebas

```bash
./mvnw test
```

## 🌐 Accesos

- API base: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- OpenAPI YAML: `http://localhost:8080/v3/api-docs.yaml`

> Nota: este proyecto usa WebFlux reactivo, por lo que `/h2-console` no está disponible como endpoint web embebido.

## 📡 Endpoints

| Método | Ruta | Descripción | Código esperado |
|---|---|---|---|
| `POST` | `/products` | Crear producto | `201 Created` |
| `DELETE` | `/products/{productId}` | Eliminar producto y sus materiales | `204 No Content` |
| `POST` | `/products/{productId}/materials` | Agregar material a producto | `201 Created` |
| `GET` | `/production/calculate?productId={id}&quantity={n}` | Calcular materiales requeridos | `200 OK` |

## 📘 Documentación de API

La documentación se genera automáticamente a partir de las rutas funcionales y modelos del servicio.

- Interfaz Swagger UI: `http://localhost:8080/swagger-ui.html`
- Especificación OpenAPI (JSON): `http://localhost:8080/v3/api-docs`
- Especificación OpenAPI (YAML): `http://localhost:8080/v3/api-docs.yaml`

## 🧪 Flujo de ejemplo (end-to-end)

### 1) Crear producto

```bash
curl -X POST "http://localhost:8080/products" \
  -H "Content-Type: application/json" \
  -d '{"name":"Zapato"}'
```

```json
{
  "id": 1,
  "name": "Zapato"
}
```

### 2) Agregar materiales al producto

```bash
curl -X POST "http://localhost:8080/products/1/materials" \
  -H "Content-Type: application/json" \
  -d '{"material":"Cuero","quantity":2}'

curl -X POST "http://localhost:8080/products/1/materials" \
  -H "Content-Type: application/json" \
  -d '{"material":"Suela","quantity":1}'

curl -X POST "http://localhost:8080/products/1/materials" \
  -H "Content-Type: application/json" \
  -d '{"material":"Cordones","quantity":1}'
```

### 3) Calcular producción para 100 unidades

```bash
curl "http://localhost:8080/production/calculate?productId=1&quantity=100"
```

```json
{
  "product": "Zapato",
  "quantity": 100,
  "materials": [
    {
      "material": "Cuero",
      "required": 200
    },
    {
      "material": "Suela",
      "required": 100
    },
    {
      "material": "Cordones",
      "required": 100
    }
  ]
}
```

## 🚨 Manejo de errores

Formato de error unificado:

```json
{
  "message": "Product with id 99 was not found",
  "status": 404,
  "timestamp": "2026-03-25T12:00:00"
}
```

## ✅ Validaciones implementadas

- Nombre del producto obligatorio.
- Material obligatorio.
- `quantity` de material debe ser mayor a `0`.
- `productId` debe ser mayor a `0`.
- `quantity` de producción debe ser mayor a `0`.
- Nombre de producto único (sin duplicados) -> `409`.
- Material único por producto (sin duplicados) -> `409`.
- Producto inexistente responde `404`.
- Entrada inválida responde `400`.

## 🧪 Testing incluido

- Unit tests del servicio principal: `BomServiceTest`.
- Pruebas reactivas con `StepVerifier`.
- Pruebas de endpoints con `WebTestClient`: `BomEndpointTest`.
