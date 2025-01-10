FROM ubuntu:lastest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk -Y

COPY . .

RUN apt-get install maven -y
RUN mvn clean install

EXPOSE 8080

COPY --from=build /target/*.jar app.jar


ENTRYPOINT ["java", "-jar", "app.jar"]