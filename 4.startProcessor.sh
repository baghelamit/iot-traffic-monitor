cd /home/ds_user/iot-traffic-monitor/iot-spark-processor
nohup dse spark-submit --packages org.apache.spark:spark-streaming-kafka_2.11:1.6.3 --class "com.iot.app.spark.processor.IoTDataProcessor" target/iot-spark-processor-1.0.0.jar &
tail -f /home/ds_user/iot-traffic-monitor/iot-spark-processor/nohup.out