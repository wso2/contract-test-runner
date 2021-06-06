# we will use openjdk 8 with alpine as it is a very small linux distro
FROM openjdk:11-ea-16-jre-slim

# copy the packaged jar file into our docker image
COPY ./target/integrationtests-1.0-SNAPSHOT.jar ./
COPY ./target/lib/. ./lib

# set the startup command to execute the jar
CMD ["java", "-jar", "integrationtests-1.0-SNAPSHOT.jar"]