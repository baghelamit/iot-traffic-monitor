# Docker image for https://github.com/jatin7/iot-traffic-monitor
FROM openjdk:8-jre-alpine
MAINTAINER Jatin Shah
ENV container=datastax-iot

# App install directory
ENV IOT_HOME=/home/ds_user
WORKDIR $IOT_HOME

# Copy all the application jars.
COPY ./iot-kafka-producer/target/iot-kafka-producer-1.0.0.jar $IOT_HOME/iot-kafka-producer.jar
COPY ./iot-springboot-dashboard/target/iot-springboot-dashboard-1.0.0.jar $IOT_HOME/iot-springboot-dashboard.jar
COPY ./iot-spark-processor/target/iot-spark-processor-1.0.0.jar $IOT_HOME/iot-spark-processor.jar

# Expose necessary ports.
EXPOSE 8989

#
# To build:
#   cd iot-traffic-monitor
#   docker build . -t datastax-iot
#   docker tag <id> jatin7/datastax-iot:<latest>
#   docker push jatin7/datastax-iot:latest
#
# To run:
#   docker run -p 8080:8080 --name datastax-iot datastax-iot
#
# Stop:
#   docker stop datastax-iot
#   docker rm datastax-iot
#
# Notes:
#   Needs to be able to talk to port 9042(DataStax DB) and port 9092(Confluent Kafka)
