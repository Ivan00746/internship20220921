FROM adoptopenjdk/openjdk11:jdk-11.0.11_9-alpine
WORKDIR /opt
RUN apk update && apk upgrade && apk add --no-cache bash
EXPOSE 9091
EXPOSE 9191
COPY target/StandaloneClusterApp-1.0.jar /opt/StandaloneClusterApp-1.0.jar
COPY datasrc/JavaFXdocs.html /opt/StandaloneClusterApp/datasrc/JavaFXdocs.html
CMD java $JAVA_OPTIONS -jar StandaloneClusterApp-1.0.jar