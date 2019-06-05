cd /home/ds_user/iot-traffic-monitor/iot-kafka-producer
nohup java -jar target/iot-kafka-producer-1.0.0.jar &
tail -f /home/ds_user/iot-traffic-monitor/iot-kafka-producer/nohup.out
