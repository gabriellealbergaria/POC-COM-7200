# Etapa 1: Build da aplicação com Gradle
FROM gradle:8.5.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# Etapa 2: Imagem final com Temurin
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copia o JAR gerado
COPY --from=build /app/build/libs/*.jar app.jar

# Variáveis padrão para configuração de heap da JVM (podem ser sobrescritas no Deployment)
ENV JAVA_XMS=256m
ENV JAVA_XMX=512m

EXPOSE 8080

# Usa shell para permitir a expansão de variáveis de ambiente no entrypoint
ENTRYPOINT ["sh", "-c", "java -Xms$JAVA_XMS -Xmx$JAVA_XMX -jar app.jar"]
