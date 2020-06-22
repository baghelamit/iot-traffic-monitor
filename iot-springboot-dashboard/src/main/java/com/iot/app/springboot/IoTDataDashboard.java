package com.iot.app.springboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring boot application class for Dashboard.
 * 
 * @author jshah
 *
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.iot.app.springboot", "com.iot.app.springboot.dao", "com.iot.app.springboot.model"})
public class IoTDataDashboard {
	private static Logger logger = LoggerFactory.getLogger(IoTDataDashboard.class);

	public static void main(String[] args) {
	        SpringApplication.run(IoTDataDashboard.class, args);
	    }
}