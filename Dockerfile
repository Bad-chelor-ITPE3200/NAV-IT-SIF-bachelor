FROM jelastic/maven:3.9.5-openjdk-21 as BUILD
COPY . .
RUN mvn compile
RUN mvn package -Dmaven.test.skip

FROM bellsoft/liberica-openjdk-alpine:21
LABEL authors="JAGO industries"
#CMD ["mvn spring-boot:build-image"]
#COPY /.mvn/maven-wrapper.jar maven-wrapper.jar
#COPY mvnw.cmd mvnw.cmd


#Can also be commended out for a real db
COPY  src/main/resources/__files ./resources/__files
COPY  --from=BUILD target/*.jar sif-vju-backend.jar

ENTRYPOINT ["java", "-jar","/sif-vju-backend.jar"]