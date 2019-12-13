FROM openjdk:8
RUN mkdir /app
WORKDIR /app
COPY src /app/src
COPY gradle /app/gradle
COPY build.gradle /app/build.gradle
COPY gradlew /app/gradlew
COPY settings.gradle /app/settings.gradle
RUN ./gradlew bootjar
ENTRYPOINT ["./gradlew","bootrun"]