FROM eclipse-temurin:21

RUN mkdir /home/app
COPY build/libs/sem-work-1.0.jar /home/app

ENTRYPOINT ["java", "-jar", "/home/app/sem-work-1.0.jar"]