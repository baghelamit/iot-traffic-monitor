package com.iot.app.springboot.model;

import java.io.Serializable;
import java.util.Date;
import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.datastax.driver.mapping.annotations.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Entity class for total_traffic db table
 * 
 * @author jshah
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "total_traffic")
public class TotalTraffic implements Serializable{
	@PartitionKey
	@Column(name = "routeid")
	private String routeId;

	@ClusteringColumn
	@Column(name = "recorddate")
	private String recordDate;

	@ClusteringColumn(1)
	@Column(name = "vehicletype")
	private String vehicleType;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="MST")
	@Column(name = "timeStamp")
	private Date timeStamp;

	@Column(name = "totalcount")
	private long totalCount;


	@Override
	public String toString() {
		return "TotalTraffic [routeId=" + routeId + ", vehicleType=" + vehicleType + ", totalCount=" + totalCount
				+ ", timeStamp=" + timeStamp + "]";
	}

}
