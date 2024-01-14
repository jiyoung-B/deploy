FROM amazoncorretto:17 as builder
WORKDIR /weather-be
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

FROM amazoncorretto:17
COPY --from=builder /weather-be/build/libs/project3-0.0.1-SNAPSHOT.jar ./app.jar
COPY --from=builder /weather-be/src/main/resources/application.properties /weather-be/application.properties
RUN ls -la
RUN cat ./application.properties
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=file:/weather-be/application.properties"]