#
# Build stage
#
FROM maven:3.8.2-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

#
# Package stage
#
FROM openjdk:17-jdk-alpine
COPY --from=build /target/pcv-backend-1.0.0.jar pcv-backend.jar
# ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","pcv-backend.jar"]