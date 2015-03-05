package com.market.trade.config;

import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@PropertySource("classpath:database.properties")
public class DatabaseConfig {

    @Autowired
    private Environment environment;

    @Bean
    public PGPoolingDataSource pgPoolingDataSource() {
        PGPoolingDataSource pgPoolingDataSource = new PGPoolingDataSource();
        pgPoolingDataSource.setServerName(environment.getProperty("sql.server.name"));
        pgPoolingDataSource.setPortNumber(Integer.parseInt(environment.getProperty("sql.port.number")));
        pgPoolingDataSource.setDatabaseName(environment.getProperty("sql.database.name"));
        pgPoolingDataSource.setPassword(environment.getProperty("sql.password"));
        pgPoolingDataSource.setUser(environment.getProperty("sql.username"));
        pgPoolingDataSource.setInitialConnections(20);
        return pgPoolingDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(PGPoolingDataSource pgPoolingDataSource) {
        return new JdbcTemplate(pgPoolingDataSource);
    }
}
