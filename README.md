# Challenge Quanta - BOM Microservice

Microservicio reactivo en Java 21 con Spring Boot, Spring WebFlux funcional y arquitectura hexagonal para gestionar una BOM (Bill of Materials).

## Stack

- Java 21
- Spring Boot 4.0.4
- Spring WebFlux (functional endpoints)
- Spring Data R2DBC
- H2 en memoria (R2DBC)
- MapStruct
- Lombok
- Maven

## Arquitectura

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

## Ejecutar la aplicación

```bash
./mvnw spring-boot:run
```

La API queda disponible en:

```text
http://localhost:8080
```

## Ejecutar tests

```bash
./mvnw test
```

## Endpoints

### 1) Crear producto

```bash
curl -X POST "http://localhost:8080/products" \
  -H "Content-Type: application/json" \
  -d '{"name":"Zapato"}'
```

Respuesta:

```json
{
  "id": 1,
  "name": "Zapato"
}
```

### 2) Agregar material al producto

```bash
curl -X POST "http://localhost:8080/products/1/materials" \
  -H "Content-Type: application/json" \
  -d '{"material":"Cuero","quantity":2}'
```

Respuesta:

```json
{
  "id": 1,
  "productId": 1,
  "material": "Cuero",
  "quantity": 2
}
```

### 3) Calcular materiales para producción

```bash
curl "http://localhost:8080/production/calculate?productId=1&quantity=100"
```

Respuesta:

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

## Manejo de errores

Formato de error unificado:

```json
{
  "message": "Product with id 99 was not found",
  "status": 404,
  "timestamp": "2026-03-25T12:00:00"
}
```

## Validaciones implementadas

- Nombre del producto obligatorio
- Material obligatorio
- `quantity` del material > 0
- `productId` > 0
- `quantity` de producción > 0
- Producto inexistente -> `404`
- Entrada inválida -> `400`

## Tests incluidos

- Unit tests del servicio principal (`BomServiceTest`)
- Tests reactivos con `StepVerifier`
- Tests de endpoints con `WebTestClient` (`BomEndpointTest`)
