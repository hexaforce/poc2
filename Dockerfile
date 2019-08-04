#FROM maven:3.6.1-amazoncorretto-8 AS maven
#COPY . ./
#RUN mvn clean install

FROM amazoncorretto:8
COPY ./.aws /root/.aws
COPY ./.gcp /root/.gcp
#COPY --from=maven /target/poc-0.0.1-SNAPSHOT.jar app.jar
COPY target/poc-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]






