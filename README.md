# IoT Traffic Monitor

Below is the architecture diagram for IoT Traffic Monitor application. Read the article at [InfoQ](https://www.infoq.com/articles/traffic-data-monitoring-iot-kafka-and-spark-streaming)

![IoT Traffic Monitor Architecture](https://github.com/baghelamit/iot-traffic-monitor/blob/master/iot-architecture.png)

Traffic Monitor application uses following tools and technologies.

- JDK - 1.8
- Maven - 3.3.9
- ZooKeeper - 3.4.8
- Kafka - 2.10-0.10.0.0
- Cassandra - 2.2.6
- Spark - 1.6.2 Pre-built for Hadoop 2.6
- Spring Boot - 1.3.5
- jQuery.js
- Bootstrap.js
- Sockjs.js
- Stomp.js
- Chart.js

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
