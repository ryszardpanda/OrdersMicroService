FROM openjdk:21-jdk

LABEL maintainer="Rychu"

COPY target/OrdersMicroService-0.0.1-SNAPSHOT.jar OrdersMicroService-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "OrdersMicroService-0.0.1-SNAPSHOT.jar"]