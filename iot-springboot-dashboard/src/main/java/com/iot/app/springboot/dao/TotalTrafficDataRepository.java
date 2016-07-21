package com.iot.app.springboot.dao;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import com.iot.app.springboot.dao.entity.TotalTrafficData;

/**
 * DAO class for total_traffic 
 * 
 * @author abaghel
 *
 */
@Repository
public interface TotalTrafficDataRepository extends CassandraRepository<TotalTrafficData>{

	 @Query("SELECT * FROM traffickeyspace.total_traffic WHERE recorddate = ?0 ALLOW FILTERING")
	 Iterable<TotalTrafficData> findTrafficDataByDate(String date);	 
}
