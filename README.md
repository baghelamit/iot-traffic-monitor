# IoT Traffic Monitor

Below is the architecture diagram for IoT Traffic Monitor application. Read the article at [InfoQ](https://www.infoq.com/articles/traffic-data-monitoring-iot-kafka-and-spark-streaming)

![IoT Traffic Monitor Architecture](https://github.com/baghelamit/iot-traffic-monitor/blob/master/iot-architecture.png)

Traffic Monitor application uses following tools and technologies.

- JDK - 1.8
- Maven - 3.3.9
- ZooKeeper - 3.4.13
- Kafka - 2.1.1
- Cassandra - 3.0.0
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


# Setup Application

1. git https://github.com/jatin7/iot-traffic-monitor.git
2. Build the required binaries.
```sh
cd iot-traffic-monitor
mvn package
```
# Running the application
1. Start the data producer.
```sh
cd iot-kafka-producer
nohup java -jar target/iot-kafka-producer-1.0.0.jar &
```
2. Start Spark the data processing application 
```sh
cd  iot-spark-processor
nohup java -jar target/iot-spark-processor-1.0.0.jar &
```
3. Start the UI application.
```sh
cd iot-springboot-dashboard
nohup ava -jar target/iot-springboot-dashboard-1.0.0.jar &
```

4. Now open the dashboard UI in a web browser. The application will refresh itself periodically.
```sh
http://localhost:8080
```


 

