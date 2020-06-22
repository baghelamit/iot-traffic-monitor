package com.iot.app.springboot.dao;

import java.util.List;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.iot.app.springboot.WebSocketConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import com.iot.app.springboot.model.POITraffic;

import static com.datastax.driver.core.querybuilder.QueryBuilder.select;


/**
 * DAO class for poi_traffic 
 * 
 * @author jshah
 *
 */
@Repository
public class POITrafficDataDAO {
    private static Logger logger = LoggerFactory.getLogger(POITrafficDataDAO.class);

    private Mapper<POITraffic> mapper;
    private Session session;

    private static final String TABLE = "poi_traffic";

    public POITrafficDataDAO(MappingManager mappingManager) {
        this.mapper = mappingManager.mapper(POITraffic.class);
        this.session = mappingManager.getSession();
    }

    public List<POITraffic> findAll()
    {
        ResultSet result = session.execute(select().all().from(TABLE));
        return mapper.map(result).all();
    };

}
