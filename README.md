# backend

## Requirements

- Node 16.
- Java 17 (tested with Eclipse Temurin).
- Maven 3.8+.
- Docker or Podman(currently only for testing)

## BEFORE ALL
### Configurar desencriptado de application.yml
Si estas en Windows:
- Click en editar las variables de Entorno del sistema y añadir nueva variable cuyo nombre sea MAVEN_OPTS con un valor:
```
  -Djasypt.plugin.path="file:src/main/resources/application.yml" -Djasypt.encryptor.password=secretKey -Djasypt.encryptor.algorithm=PBEWithHMACSHA512AndAES_256
  ```
- En principio debería bastar con jasypt.ecryptor.password pero para asegurarse se pueden poner las 3. secretKey es un ejemplo, por seguridad no aparecera la contraseña real por ningún sitio

## Configuración de la BD
### Producción
- Actualmente es un contenedor docker que tiene una imagen de postgresql y está desplegada en render.com y se puede ver en las propiedades del pom y no hay que tocar nada para su funcionamiento.
- Expira a los 90 dias la versión gratuita (se puede crear otra)
### Tests
- Se necesita configurar un contenedor local en el puerto localhost:5433.

Con podman en windows. Se recomienda instalar Podman Desktop
```
podman run -d --name pcvtest -e POSTGRES_PASSWORD=pcv -p 5433:5432 docker.io/library/postgres:latest

-If you have a local image
podman run -d --name pcvtest -e POSTGRES_PASSWORD=pcv -p 5433:5432 localhost/my-postgres:image

podman exec -it pcvtest psql -U postgres -c "CREATE USER pcv WITH PASSWORD 'pcv';"
podman exec -it pcvtest psql -U postgres 
CREATE DATABASE pcvtest WITH OWNER pcv;    
GRANT ALL PRIVILEGES ON DATABASE pcvtest TO pcv;
quit
```

## Run

```
cd backend
mvn sql:execute (only first time to create tables)
mvn spring-boot:run

cd frontend
npm install (only first time to download libraries)
npm start

```

## OPENAPI specification
### Acessible mientras la política cors sea permisiva
```
http://localhost:8080/swagger-ui/index.html?url=v3/api-docs
http://localhost:8080/v3/api-docs (OpenApi JSON format)
permitAll instead of denyAll in corsPolicy backend/common/SecurityConfig
```
## Production

```

cd backend
mvn clean package

cd frontend
npm run build
```