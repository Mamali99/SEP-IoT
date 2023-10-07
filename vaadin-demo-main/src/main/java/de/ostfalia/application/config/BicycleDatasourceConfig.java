package de.ostfalia.application.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class BicycleDatasourceConfig {


    @Bean
    @ConfigurationProperties("spring.datasource.bikes")
    public DataSourceProperties bikeDataSourceProperties() {
        return new DataSourceProperties();
    }
    @Bean
    public DataSource bikeDS(){
        return bikeDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public JdbcTemplate bikeJdbcTemplate(@Qualifier("bikeDS") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
