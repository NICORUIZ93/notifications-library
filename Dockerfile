FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Instalar Maven
RUN apk add --no-cache maven

# Copiar archivos del proyecto
COPY pom.xml .
COPY src ./src

# Compilar la librería
RUN mvn clean package -DskipTests

# Imagen final
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar el JAR compilado
COPY --from=build /app/target/notifications-library-1.0.0-SNAPSHOT.jar ./notifications-library.jar

# Ejecutar ejemplos
CMD ["java", "-jar", "notifications-library.jar"]
