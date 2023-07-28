# First stage: build the project using Maven
FROM maven:3-eclipse-temurin-11 as builder

# Copy the pom.xml file into the Docker image and download dependencies
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copy the source code into the Docker image and build the project
COPY src/ ./src/
RUN mvn -B clean package

# Second stage: run the WAR file using Tomcat
FROM tomcat:jdk11

# Remove the default webapps from the Tomcat image
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the WAR file from the builder image into the webapps directory of the Tomcat image
COPY --from=builder target/my-app.war /usr/local/tomcat/webapps/ROOT.war
