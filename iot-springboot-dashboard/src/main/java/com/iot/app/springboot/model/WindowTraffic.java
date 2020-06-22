package com.iot.app.springboot.model;

import java.io.Serializable;
import java.util.Date;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Entity class for window_traffic db table
 * 
 * @author jshah
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Table(name = "window_traffic")
public class WindowTraffic implements Serializable{
	@PartitionKey
	@Column(name="routeid")
	private String routeId;

	@ClusteringColumn
	@Column(name="recorddate")
	private String recordDate;

	@ClusteringColumn(1)
	@Column(name="vehicletype")
	private String vehicleType;

	@Column(name="totalcount")
	private long totalCount;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="MST")
	@Column(name="timestamp")
	private Date timeStamp;

	@Override
	public String toString() {
		return "WindowTraffic [routeId=" + routeId + ", vehicleType=" + vehicleType + ", totalCount=" + totalCount
				+ ", timeStamp=" + timeStamp + "]";
	}
}
