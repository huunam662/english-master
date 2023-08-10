### STAGE 1: RUN ###
FROM openjdk:17-oracle
WORKDIR /usr/src/app
COPY . .
RUN chmod +x mvnw
RUN bash mvnw install 
ENTRYPOINT ["sh",  "./mvnw", "spring-boot:run"]
