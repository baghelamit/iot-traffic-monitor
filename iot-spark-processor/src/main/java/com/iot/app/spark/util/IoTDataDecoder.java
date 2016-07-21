package com.iot.app.spark.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.app.spark.vo.IoTData;

import kafka.serializer.Decoder;
import kafka.utils.VerifiableProperties;

/**
 * Class to deserialize JSON string to IoTData java object
 * 
 * @author abaghel
 *
 */
public class IoTDataDecoder implements Decoder<IoTData> {
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public IoTDataDecoder(VerifiableProperties verifiableProperties) {

    }
	public IoTData fromBytes(byte[] bytes) {
		try {
			return objectMapper.readValue(bytes, IoTData.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
