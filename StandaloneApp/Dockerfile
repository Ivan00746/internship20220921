FROM adoptopenjdk/openjdk11:jdk-11.0.11_9-alpine
WORKDIR /opt
ENV PORT 9090
EXPOSE 9090
EXPOSE 9190
COPY target/StandaloneApp-1.0.jar /opt/StandaloneApp-1.0.jar
COPY datasrc/JavaFXdocs.html /opt/StandaloneApp/datasrc/JavaFXdocs.html
ENTRYPOINT exec java $JAVA_OPTIONS -jar StandaloneApp-1.0.jar