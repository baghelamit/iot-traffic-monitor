package com.iot.app.springboot.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static com.datastax.driver.mapping.NamingConventions.LOWER_CAMEL_CASE;
import static com.datastax.driver.mapping.NamingConventions.LOWER_SNAKE_CASE;

/**
 * DAO class for poi_traffic
 *
 * @author jshah
 *
 */

@Configuration
public class CassandraConfig {

  @Bean
  public Cluster cluster(
      @Value("${cassandra.host:127.0.0.1}") String host,
      @Value("${cassandra.cluster.name:cluster}") String clusterName,
      @Value("${cassandra.port:9042}") int port) {
    return Cluster.builder()
            .withoutJMXReporting()
            .addContactPoint(host)
        .withPort(port)
        .build();
  }

  @Bean
  public Session session(Cluster cluster) throws IOException {
    final Session session = cluster.connect("traffickeyspace");
    return session;
  }


  @Bean
  public MappingManager mappingManager(Session session) {
    final PropertyMapper propertyMapper = new DefaultPropertyMapper().setNamingStrategy(new DefaultNamingStrategy(LOWER_CAMEL_CASE, LOWER_SNAKE_CASE));
    final MappingConfiguration configuration = MappingConfiguration.builder().withPropertyMapper(propertyMapper).build();
    return new MappingManager(session, configuration);
  }
}
