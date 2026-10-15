# syntax=docker/dockerfile:1

FROM docker.io/library/eclipse-temurin:17-jre-jammy AS runtime
WORKDIR /app

# Das Image erwartet, dass die Anwendung bereits lokal gebaut wurde:
#   mvn -DskipTests package dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target/dependency
# Dadurch braucht der Docker-Build kein Maven, kein Remote-Repository und keine Zertifikatsauflösung.
# Liquibase kann die Changelog-Dateien weiterhin als normale Dateien aus /app/classes/db lesen.
COPY target/classes /app/classes
COPY target/dependency /app/libs

EXPOSE 7070

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -cp /app/classes:/app/libs/* ch.suva.bi7.webshop.service.Bi7WebshopService"]


