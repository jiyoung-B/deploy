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
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
