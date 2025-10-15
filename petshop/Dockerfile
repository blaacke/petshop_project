FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package
RUN ls -lah target && \
    JAR_FILE=$(ls target | grep '\.jar$' | head -n1) && \
    echo "JAR => ${JAR_FILE}" && \
    cp "target/${JAR_FILE}" /app/app.jar

FROM eclipse-temurin:21-jre
ENV TZ=America/Sao_Paulo \
    JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75 -Dfile.encoding=UTF-8"

WORKDIR /opt/app
COPY --from=build /app/app.jar ./app.jar

EXPOSE 8080

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar app.jar" ]
