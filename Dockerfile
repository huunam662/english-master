### STAGE 1: RUN ###
FROM openjdk:17-oracle
WORKDIR /usr/src/app
COPY . .
RUN chmod 755 mvnw
RUN bash mvnw install 
ENTRYPOINT ["./mvnw" "spring-boot:run"]