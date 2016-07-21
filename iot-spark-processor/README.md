# IoT Spark Processor
IoT Spark Processor is a Maven application for processing IoT Data streams using Apache Spark. Processed data is persisted in to Cassandra Database. This project requires following tools and technologies.

- JDK - 1.8
- Maven - 3.3.9
- ZooKeeper - 3.4.8
- Kafka - 2.10-0.10.0.0
- Cassandra - 2.2.6
- Spark - 1.6.2 Pre-built for Hadoop 2.6

Please refer "IoTData.cql" file to create Keyspace and Tables in Cassandra Database, which are required by this application.

You can build and run this application using below commands. Please check resources/iot-spark.properties for configuration details.

```sh
mvn package
spark-submit --class "com.iot.app.spark.processor.IoTDataProcessor” iot-spark-processor-1.0.0.jar
```