### STAGE 1: RUN ###
FROM openjdk:17-oracle
WORKDIR /usr/src/app
COPY . .
RUN bash mvnw install 
ENTRYPOINT ["bash", "mvnw" "spring-boot:run"]