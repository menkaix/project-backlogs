FROM gradle:jdk19 AS build

COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src

RUN gradle build --no-daemon 

RUN ls /home/gradle/src/

FROM openjdk:19

EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*SNAPSHOT.jar /app/spring-boot-application.jar


RUN ls /

CMD ["java", "-jar","/app/spring-boot-application.jar"]