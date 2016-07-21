package com.iot.app.spark.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function3;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.State;
import org.apache.spark.streaming.StateSpec;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaMapWithStateDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import com.google.common.base.Optional;
import com.iot.app.spark.util.IoTDataDecoder;
import com.iot.app.spark.util.PropertyFileReader;
import com.iot.app.spark.vo.IoTData;
import com.iot.app.spark.vo.POIData;

import kafka.serializer.StringDecoder;
import scala.Tuple2;
import scala.Tuple3;

/**
 * This class consumes Kafka IoT messages and creates stream for processing the IoT data.
 * 
 * @author abaghel
 *
 */
public class IoTDataProcessor {
	
	 private static final Logger logger = Logger.getLogger(IoTDataProcessor.class);
	
	 public static void main(String[] args) throws Exception {
		 //read Spark and Cassandra properties and create SparkConf
		 Properties prop = PropertyFileReader.readPropertyFile();		
		 SparkConf conf = new SparkConf()
				 .setAppName(prop.getProperty("com.iot.app.spark.app.name"))
				 .setMaster(prop.getProperty("com.iot.app.spark.master"))
				 .set("spark.cassandra.connection.host", prop.getProperty("com.iot.app.cassandra.host"))
				 .set("spark.cassandra.connection.port", prop.getProperty("com.iot.app.cassandra.port"))
				 .set("spark.cassandra.connection.keep_alive_ms", prop.getProperty("com.iot.app.cassandra.keep_alive"));		 
		 //batch interval of 5 seconds for incoming stream		 
		 JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));	
		 //add check point directory
		 jssc.checkpoint(prop.getProperty("com.iot.app.spark.checkpoint.dir"));
		 
		 //read and set Kafka properties
		 Map<String, String> kafkaParams = new HashMap<String, String>();
		 kafkaParams.put("zookeeper.connect", prop.getProperty("com.iot.app.kafka.zookeeper"));
		 kafkaParams.put("metadata.broker.list", prop.getProperty("com.iot.app.kafka.brokerlist"));
		 String topic = prop.getProperty("com.iot.app.kafka.topic");
		 Set<String> topicsSet = new HashSet<String>();
		 topicsSet.add(topic);
		 //create direct kafka stream
		 JavaPairInputDStream<String, IoTData> directKafkaStream = KafkaUtils.createDirectStream(
			        jssc,
			        String.class,
			        IoTData.class,
			        StringDecoder.class,
			        IoTDataDecoder.class,
			        kafkaParams,
			        topicsSet
			    );
		 logger.info("Starting Stream Processing");
		 
		 //We need non filtered stream for poi traffic data calculation
		 JavaDStream<IoTData> nonFilteredIotDataStream = directKafkaStream.map(tuple -> tuple._2());
		 
		 //We need filtered stream for total and traffic data calculation
		 JavaPairDStream<String,IoTData> iotDataPairStream = nonFilteredIotDataStream.mapToPair(iot -> new Tuple2<String,IoTData>(iot.getVehicleId(),iot)).reduceByKey((a, b) -> a );
		
		 // Check vehicle Id is already processed
		 JavaMapWithStateDStream<String, IoTData, Boolean, Tuple2<IoTData,Boolean>> iotDStreamWithStatePairs = iotDataPairStream
							.mapWithState(StateSpec.function(processedVehicleFunc).timeout(Durations.seconds(3600)));//maintain state for one hour

		 // Filter processed vehicle ids and keep un-processed
		 JavaDStream<Tuple2<IoTData,Boolean>> filteredIotDStreams = iotDStreamWithStatePairs.map(tuple2 -> tuple2)
							.filter(tuple -> tuple._2.equals(Boolean.FALSE));

		 // Get stream of IoTdata
		 JavaDStream<IoTData> filteredIotDataStream = filteredIotDStreams.map(tuple -> tuple._1);
		 
		 //cache stream as it is used in total and window based computation
		 filteredIotDataStream.cache();
		 	 
		 //process data
		 IoTTrafficDataProcessor iotTrafficProcessor = new IoTTrafficDataProcessor();
		 iotTrafficProcessor.processTotalTrafficData(filteredIotDataStream);
		 iotTrafficProcessor.processWindowTrafficData(filteredIotDataStream);

		 //poi data
		 POIData poiData = new POIData();
		 poiData.setLatitude(33.877495);
		 poiData.setLongitude(-95.50238);
		 poiData.setRadius(30);//30 km
		 
		 //broadcast variables. We will monitor vehicles on Route 37 which are of type Truck
		 Broadcast<Tuple3<POIData, String, String>> broadcastPOIValues = jssc.sparkContext().broadcast(new Tuple3<>(poiData,"Route-37","Truck"));
		 //call method  to process stream
		 iotTrafficProcessor.processPOIData(nonFilteredIotDataStream,broadcastPOIValues);
		 
		 //start context
		 jssc.start();            
		 jssc.awaitTermination();  
  }
	 //Funtion to check processed vehicles.
	private static final Function3<String, Optional<IoTData>, State<Boolean>, Tuple2<IoTData,Boolean>> processedVehicleFunc = (String, iot, state) -> {
			Tuple2<IoTData,Boolean> vehicle = new Tuple2<>(iot.get(),false);
			if(state.exists()){
				vehicle = new Tuple2<>(iot.get(),true);
			}else{
				state.update(Boolean.TRUE);
			}			
			return vehicle;
		};
          
}
