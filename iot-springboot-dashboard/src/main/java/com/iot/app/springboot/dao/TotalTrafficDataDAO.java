package com.iot.app.springboot.dao;

//import org.springframework.data.cassandra.repository.AllowFiltering;
//import org.springframework.data.cassandra.repository.Query;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
//import org.springframework.stereotype.Repository;
//import org.springframework.data.repository.CrudRepository;
import com.iot.app.springboot.model.TotalTraffic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;

/**
 * DAO class for total_traffic 
 * 
 * @author jshah
 *
 */

@Repository
public class TotalTrafficDataDAO {
	private static Logger logger = LoggerFactory.getLogger(TotalTrafficDataDAO.class);


	private Mapper<TotalTraffic> mapper;
	private Session session;

	private static final String TABLE = "total_traffic";

	public TotalTrafficDataDAO(MappingManager mappingManager) {
		this.mapper = mappingManager.mapper(TotalTraffic.class);
		this.session = mappingManager.getSession();
	}

	public List<TotalTraffic> findAll()
	{
		final ResultSet result = session.execute(select().all().from(TABLE));
		return mapper.map(result).all();
	};

	public List<TotalTraffic> findTrafficDataByDate(String date) {
		System.out.println("findTrafficDataByDate:" + date);
		ResultSet result = session.execute(select().all().from(TABLE).where(eq("recorddate ", date)).allowFiltering());
		return mapper.map(result).all();
	}

}
