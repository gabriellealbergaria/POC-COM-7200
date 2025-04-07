# Etapa 1: Build da aplicação com Gradle
FROM gradle:8.5.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# Etapa 2: Imagem final com Temurin e APM agent
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copia o JAR gerado
COPY --from=build /app/build/libs/*.jar app.jar

# Baixa o Elastic APM Agent
ADD https://repo1.maven.org/maven2/co/elastic/apm/elastic-apm-agent/1.45.0/elastic-apm-agent-1.45.0.jar /elastic-apm-agent.jar

EXPOSE 8080

ENTRYPOINT ["java", "-javaagent:/elastic-apm-agent.jar", "-jar", "app.jar"]
