FROM openjdk:8
COPY src /usr/src/myapp/src
COPY gradle /usr/src/myapp/gradle
COPY build.gradle settings.gradle gradlew /usr/src/myapp/
WORKDIR /usr/src/myapp
RUN ./gradlew bootjar
ENTRYPOINT ["./gradlew", "bootrun"]