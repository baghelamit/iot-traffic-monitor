package com.iot.app.springboot.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.java.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import org.apache.log4j.Logger;

/**
 * Spring bean configuration for Cassandra db.
 * 
 * @author abaghel
 *
 */
@Configuration
@PropertySource(value = {"classpath:iot-springboot.properties"})
@EnableCassandraRepositories(basePackages = {"com.iot.app.springboot.dao"})
public class CassandraConfig extends AbstractCassandraConfiguration {
    private static final Logger logger = Logger.getLogger(CassandraConfig.class);

    @Autowired
    private Environment environment;
    
    @Bean
    public CassandraClusterFactoryBean cluster() {
        CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
	String cassandraHost = environment.getProperty("com.iot.app.cassandra.host");
        if (System.getProperty("com.iot.app.cassandra.host") != null) {
            cassandraHost = System.getProperty("com.iot.app.cassandra.host");
        }
        String cassandraPort = environment.getProperty("com.iot.app.cassandra.port");
        if (System.getProperty("com.iot.app.cassandra.port") != null) {
            cassandraPort = System.getProperty("com.iot.app.cassandra.port");
        }
	logger.info("Using cassandra host=" + cassandraHost + " port=" + cassandraPort);
        cluster.setContactPoints(cassandraHost);
        cluster.setPort(Integer.parseInt(cassandraPort));
        return cluster;
    }
  
    @Bean
    public CassandraMappingContext cassandraMapping(){
         return new BasicCassandraMappingContext();
    }
    
	@Override
	@Bean
	protected String getKeyspaceName() {
		return environment.getProperty("com.iot.app.cassandra.keyspace");
	}
}
