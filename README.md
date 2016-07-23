# IoT Traffic Monitor

Below is the architecture diagram for IoT Traffic Monitor application.

iot-traffic-monitor/iot-architecture.png

IoT Traffic Monitor is a Maven Aggregator project. It includes following three projects.

- IoT Kafka Producer
- IoT Spark Processor
- IoT Spring Boot Dashboard

For building these projects it requires following tools. Please refer README.md files of individual projects for more details.

- JDK - 1.8
- Maven - 3.3.9

Use below command to build all projects.

```sh
mvn package
```
