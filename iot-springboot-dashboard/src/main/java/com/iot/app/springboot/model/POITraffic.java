package com.iot.app.springboot.model;

import java.io.Serializable;
import java.util.Date;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonFormat;


/**
 * Entity class for poi_traffic db table
 * 
 * @author jshah
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "poi_traffic")
public class POITraffic implements Serializable{

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="MST")
	@PartitionKey
	@Column(name="timestamp")
	private Date timeStamp;

	@Column(name="vehicleid")
	private String vehicleId;

	@Column(name="distance")
	private long distance;

	@Column(name="vehicletype")
	private String vehicleType;

}
