FROM amazoncorretto:11-alpine-jdk

COPY build/libs/msFragger2LimelightXML.jar  /usr/local/bin/msFragger2LimelightXML.jar

ENTRYPOINT ["java", "-jar", "/usr/local/bin/msFragger2LimelightXML.jar"]