FROM maven:3.5.4-jdk-8-alpine

# Copy everthing from . to /app inside the 'box'
COPY . /app
WORKDIR /app

RUN mkdir -p /app/storage

EXPOSE 8080

CMD ["mvn", "clean", "spring-boot:run"]

#FROM openjdk:8-alpine
#
## Required for starting application up.
#RUN apk update && apk add bash
#
#RUN mkdir -p /app
#RUN mkdir -p /app/storage
#
#ENV PROJECT_HOME /app
#
#COPY target/project0-0.0.1-SNAPSHOT.jar $PROJECT_HOME/project0-0.0.1-SNAPSHOT.jar
#
#WORKDIR $PROJECT_HOME
#
#CMD ["java", "-Dspring.data.mongodb.uri=mongodb://object-storage-mongo:27017/sos","-Djava.security.egd=file:/dev/./urandom","-jar","./project0-0.0.1-SNAPSHOT.jar"]