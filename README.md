# Portafolio Backend

API REST para portafolio personal. Gestiona proyectos, mensajes de contacto y autenticación con JWT.

> **Estado actual:** backend funcional y testeado, pero **no conectado** a la versión estática publicada en GitHub Pages. El frontend usa datos locales y Web3Forms para el contacto.

## Tecnologías

- **Java 21**, **Spring Boot 3.5.6**
- **Spring Web** — controladores REST
- **Spring Data JPA / Hibernate** — persistencia
- **PostgreSQL** — base de datos
- **Spring Security** + **JWT (jjwt)** — autenticación
- **JavaMailSender** — envío de correos
- **Maven** — build tool

## Requisitos previos

- JDK 21
- Maven 3.9+ (o usar `./mvnw`)
- PostgreSQL 15+
- Variables de entorno configuradas (si no usas defaults)

## Configuración del entorno

La aplicación se configura mediante variables de entorno. El archivo `src/main/resources/application.properties` está ignorado por git para no versionar secretos.

| Variable         | Descripción                                          |
|------------------|------------------------------------------------------|
| `DB_URL`         | URL JDBC de PostgreSQL (por defecto, localhost)      |
| `DB_USERNAME`    | Usuario de PostgreSQL (por defecto, `postgres`)      |
| `DB_PASSWORD`    | Contraseña de PostgreSQL                             |
| `MAIL_USERNAME`  | Email SMTP (Gmail recomendado)                       |
| `MAIL_PASSWORD`  | Contraseña de aplicación Gmail                       |
| `JWT_SECRET`     | Clave aleatoria de al menos 32 bytes                 |
| `CONTACT_NOTIFICATION_EMAIL` | Destino de los mensajes de contacto      |
| `RATE_LIMIT_MAX_REQUESTS` | Peticiones por ventana en login/contacto   |
| `RATE_LIMIT_WINDOW_SECONDS` | Duración de la ventana de rate limit     |
| `CORS_ALLOWED_ORIGINS` | Orígenes permitidos, separados por comas         |

> El perfil `dev` importa automáticamente el archivo `.env` mediante `spring.config.import`. En producción define las variables directamente en el entorno del despliegue.
>
> Genera un secreto JWT seguro con: `openssl rand -hex 48`.

## Instalación y ejecución

```bash
# Clonar el repositorio
git clone <url>
cd portafolio-backend

# Configurar variables de entorno
export DB_URL=jdbc:postgresql://localhost:5432/portafolio_elena
export DB_USERNAME=postgres
export DB_PASSWORD=tu-password
export MAIL_USERNAME=tu-email@gmail.com
export MAIL_PASSWORD=tu-contraseña-app
export JWT_SECRET=una-clave-aleatoria-de-al-menos-32-bytes

# Ejecutar en desarrollo
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

Perfiles disponibles:

- `dev`: SQL visible y logs de seguridad en `DEBUG`.
- `prod`: `ddl-auto=validate`, logs reducidos y sin trazas en las respuestas.

Si prefieres un archivo local, copia `application-example.properties` como `application-local.properties` (ignorado por git), ajusta los valores y ejecuta con `--spring.profiles.active=local`.

## Estructura del proyecto

```
src/main/java/com/elena/portafolio/
├── PortafolioApplication.java
├── controller/
│   ├── AuthController.java        # POST /api/auth/login
│   ├── ContactController.java     # POST /api/contact
│   └── ProjectController.java     # CRUD /api/projects
├── config/
│   ├── CorsConfig.java
│   └── PasswordConfig.java
├── dto/
│   ├── ContactRequest.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   └── ProjectRequest.java
├── entity/
│   ├── ContactMessage.java
│   ├── Project.java
│   └── User.java
├── repository/
│   ├── ContactMessageRepository.java
│   ├── ProjectRepository.java
│   └── UserRepository.java
├── security/
│   ├── SecurityConfig.java
│   └── filter/
│       └── JWTAuthentication.java
└── service/
    ├── AuthService.java
    ├── ContactService.java
    ├── CustomUserDetailsService.java
    ├── JwtService.java
    └── ProjectService.java
```

## API Endpoints

### Autenticación

| Método | Ruta                | Descripción                              | Auth   |
|--------|---------------------|------------------------------------------|--------|
| POST   | `/api/auth/login`   | Iniciar sesión                           | No     |
| POST   | `/api/auth/refresh` | Renovar access token y rotar refresh     | Cookie |
| POST   | `/api/auth/logout`  | Revocar refresh token y limpiar cookie   | Cookie |

El access token dura 15 minutos por defecto. El refresh token se guarda como hash en `refresh_tokens`, viaja en una cookie `HttpOnly` y rota en cada uso.

### Documentación OpenAPI

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- JSON OpenAPI: `http://localhost:8080/v3/api-docs`

**Body:**
```json
{
  "username": "admin",
  "password": "tu-contraseña"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "role": "ADMIN"
}
```

### Proyectos

| Método | Ruta                      | Descripción                    | Auth   |
|--------|---------------------------|--------------------------------|--------|
| GET    | `/api/projects?page=0&size=12` | Listar paginados y ordenados | No |
| GET    | `/api/projects/featured`  | Listar destacados              | No     |
| GET    | `/api/projects/{id}`      | Obtener proyecto               | No     |
| POST   | `/api/projects`           | Crear proyecto                 | ADMIN  |
| PUT    | `/api/projects/{id}`      | Actualizar proyecto            | ADMIN  |
| DELETE | `/api/projects/{id}`      | Eliminar proyecto              | ADMIN  |

La respuesta paginada incluye `content`, `page`, `size`, `totalElements`, `totalPages`, `first` y `last`. El orden por defecto es `displayOrder` ascendente y `createdAt` descendente.

**Header requerido para escritura:** `Authorization: Bearer <token>`

**Body POST/PUT:**
```json
{
  "title": "Mi proyecto",
  "slug": "mi-proyecto",
  "description": "Descripción del proyecto",
  "technologies": "Java, Spring, React",
  "imageUrl": "https://...",
  "githubUrl": "https://github.com/...",
  "demoUrl": "https://...",
  "featured": true,
  "displayOrder": 1
}
```

El `slug` es opcional: se genera automáticamente desde el título y se deduplica si ya existe.

### Contacto

| Método | Ruta            | Descripción                | Auth |
|--------|-----------------|----------------------------|------|
| POST   | `/api/contact`  | Enviar mensaje de contacto | No   |

**Body:**
```json
{
  "name": "Tu nombre",
  "email": "tu@email.com",
  "message": "Hola, me gustaría contactarte..."
}
```

Al enviar un mensaje:
- Se guarda en la tabla `contact_messages`
- Se envía un email a `CONTACT_NOTIFICATION_EMAIL` (por defecto, `elenaalmansacampos@gmail.com`)
- Se envía un email de confirmación al remitente (de forma asíncrona)
- El endpoint tiene rate limiting por IP

### Salud

| Método | Ruta               | Descripción        | Auth |
|--------|--------------------|--------------------|------|
| GET    | `/actuator/health` | Estado de la app   | No   |
| GET    | `/actuator/info`   | Información        | No   |

## Migraciones

El esquema se gestiona con Flyway (`src/main/resources/db/migration`). En producción se ejecuta con `spring.jpa.hibernate.ddl-auto=validate`.

## Docker

Desde la raíz del proyecto:

```bash
docker compose --env-file ./portafolio-backend/.env up --build
```

Servicios: PostgreSQL (`5432`), backend (`8080`) y frontend (`5173`). El backend ejecuta Flyway al arrancar.

## Scripts disponibles

| Comando                    | Descripción                    |
|----------------------------|--------------------------------|
| `./mvnw spring-boot:run`   | Ejecutar la aplicación         |
| `./mvnw test`              | Ejecutar tests                 |
| `./mvnw package`           | Empaquetar como JAR            |
| `./mvnw clean`             | Limpiar build                  |
| `./mvnw compile`           | Compilar código                |

`./mvnw test` incluye una prueba de integración con Testcontainers y PostgreSQL real. Si usas Docker Desktop en macOS y no detecta el socket:

```bash
DOCKER_HOST=unix://$HOME/.docker/run/docker.sock ./mvnw test
```

También puedes declarar `docker.host` en `~/.testcontainers.properties`.

## Operaciones

Consulta `../OPERACIONES.md` para backup/restore, health checks, rotación de secretos, logs y despliegue.

## Crear el primer administrador

No hay credenciales por defecto. Con una base de datos limpia:

```bash
SPRING_PROFILES_ACTIVE=dev \
APP_ADMIN_BOOTSTRAP=true \
APP_ADMIN_USERNAME=elena \
APP_ADMIN_PASSWORD='una-password-larga-y-segura' \
./mvnw spring-boot:run
```

La contraseña debe tener al menos 12 caracteres. El proceso crea el usuario y termina.

## Despliegue

El repositorio incluye `render.yaml` para desplegar en Render con Docker. La aplicación escucha en `PORT` (con fallback a `8080`) y el health check es `/actuator/health`. Configura las variables indicadas en `../OPERACIONES.md`.

## Licencia

Proyecto personal. Sin licencia específica.

## Autora
https://github.com/elenaalmansacampos
https://www.linkedin.com/in/elena-almansa-5315a017/

