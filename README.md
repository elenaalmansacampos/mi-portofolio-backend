# Portafolio Backend

API REST para portafolio personal. Gestiona proyectos, mensajes de contacto y autenticación con JWT.

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

```properties
# src/main/resources/application.properties (valores por defecto)
server.port=8080

spring.datasource.url=jdbc:postgresql://localhost:5432/portafolio_elena
spring.datasource.username=postgres
spring.datasource.password=REDACTED
spring.datasource.driver-class-name=org.postgresql.Driver
```

Variables de entorno requeridas para email y JWT:

| Variable         | Descripción                      |
|------------------|----------------------------------|
| `MAIL_USERNAME`  | Email SMTP (Gmail recomendado)   |
| `MAIL_PASSWORD`  | Contraseña de aplicación Gmail   |
| `JWT_SECRET`     | Clave secreta para firmar tokens |

## Instalación y ejecución

```bash
# Clonar el repositorio
git clone <url>
cd portafolio-backend

# Configurar variables de entorno
export MAIL_USERNAME=tu-email@gmail.com
export MAIL_PASSWORD=tu-contraseña-app
export JWT_SECRET=clave-secreta

# Ejecutar
./mvnw spring-boot:run
```

## Estructura del proyecto

```
src/main/java/com/elena/portafolio/
├── PortafolioApplication.java
├── controller/
│   ├── AuthController.java        # POST /api/auth/login
│   ├── ContactController.java     # POST /api/contact
│   └── ProjectController.java     # CRUD /api/projects
├── dto/
│   ├── ContactRequest.java
│   ├── LoginRequest.java
│   └── LoginResponse.java
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
│   ├── filter/
│   │   └── JWTAuthentication.java
│   ├── config/
│   │   └── PasswordConfig.java
│   └── service/
│       ├── CustomUserDetailsService.java
│       └── JwtService.java
└── service/
    ├── AuthService.java
    ├── ContactService.java
    └── ProjectService.java
```

## API Endpoints

### Autenticación

| Método | Ruta              | Descripción         | Auth |
|--------|-------------------|---------------------|------|
| POST   | `/api/auth/login` | Iniciar sesión      | No   |

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

### Proyectos (requiere rol ADMIN)

| Método | Ruta                  | Descripción           | Auth     |
|--------|-----------------------|-----------------------|----------|
| GET    | `/api/projects`       | Listar proyectos      | JWT      |
| POST   | `/api/projects`       | Crear proyecto        | JWT      |
| GET    | `/api/projects/{id}`  | Obtener proyecto      | JWT      |
| PUT    | `/api/projects/{id}`  | Actualizar proyecto   | JWT      |
| DELETE | `/api/projects/{id}`  | Eliminar proyecto     | JWT      |

**Header requerido:** `Authorization: Bearer <token>`

**Body POST/PUT:**
```json
{
  "title": "Mi proyecto",
  "description": "Descripción del proyecto",
  "technologies": "Java, Spring, React",
  "imageUrl": "https://...",
  "githubUrl": "https://github.com/...",
  "demoUrl": "https://..."
}
```

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
- Se envía un email a `domingosamisa@gmail.com`
- Se envía un email de confirmación al remitente

## Scripts disponibles

| Comando                    | Descripción                    |
|----------------------------|--------------------------------|
| `./mvnw spring-boot:run`   | Ejecutar la aplicación         |
| `./mvnw test`              | Ejecutar tests                 |
| `./mvnw package`           | Empaquetar como JAR            |
| `./mvnw clean`             | Limpiar build                  |
| `./mvnw compile`           | Compilar código                |

## Licencia

Proyecto personal. Sin licencia específica.

## Autora
https://github.com/elenaalmansacampos
https://www.linkedin.com/in/elena-almansa-5315a017/

