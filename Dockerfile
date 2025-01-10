FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk maven -y

COPY . .

WORKDIR /todolist/todolist

RUN mvn clean install

FROM openjdk:17-jdk-slim

EXPOSE 8080

COPY --from=build /todolist/todolist/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]