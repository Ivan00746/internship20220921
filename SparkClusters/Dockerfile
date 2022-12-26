FROM adoptopenjdk/openjdk11:jdk-11.0.11_9-alpine
WORKDIR /opt
ENV PORT 8080
EXPOSE 8080
EXPOSE 4040
COPY target/SparkLocalDefaultTest-1.0.jar /opt/SparkLocalDefaultTest-1.0.jar
COPY datasrc/JavaFXdocs.html /opt/datasrc/JavaFXdocs.html
ENTRYPOINT exec java $JAVA_OPTIONS -jar SparkLocalDefaultTest-1.0.jar