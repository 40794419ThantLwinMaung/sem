FROM openjdk:latest
COPY ./target/sem.jar /tmp
WORKDIR /tmp
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "sem.jar", "db:3306", "30000"]