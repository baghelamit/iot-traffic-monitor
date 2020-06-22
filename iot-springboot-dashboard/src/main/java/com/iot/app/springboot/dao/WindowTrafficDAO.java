package com.iot.app.springboot.dao;

//import org.springframework.data.cassandra.repository.CassandraRepository;
//import org.springframework.data.cassandra.repository.AllowFiltering;
//import org.springframework.data.cassandra.repository.Query;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import com.iot.app.springboot.model.WindowTraffic;

import java.util.List;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;

/**
 * DAO class for window_traffic 
 * 
 * @author jshah
 *
 */
@Repository
public class WindowTrafficDAO {
	private static Logger logger = LoggerFactory.getLogger(WindowTrafficDAO.class);

	private Mapper<WindowTraffic> mapper;
	private Session session;

	private static final String TABLE = "window_traffic";

	public WindowTrafficDAO(MappingManager mappingManager) {
		//session = mappingManager.getSession();
		this.mapper = mappingManager.mapper(WindowTraffic.class);
		this.session = mappingManager.getSession();
	}
	public List<WindowTraffic> findAll()
	{
		final ResultSet result = session.execute(select().all().from(TABLE));
		return mapper.map(result).all();
	};

	public List<WindowTraffic> findTrafficDataByDate(String date) {
		final ResultSet result =
				session.execute(select().all().from(TABLE).where(eq("recordDate ", date)).allowFiltering());
		return mapper.map(result).all();
	}

}
