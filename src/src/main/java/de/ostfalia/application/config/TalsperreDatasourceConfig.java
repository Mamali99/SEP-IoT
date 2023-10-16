package de.ostfalia.application.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "talsperreEntityManagerFactory",
        transactionManagerRef = "talsperreTransactionManager"
        )
public class TalsperreDatasourceConfig {

//    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.talsperre")
    //@Primary
    public DataSourceProperties talsperrenDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource talisDS(){
        return talsperrenDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public JdbcTemplate talsperreJdbcTemplate(@Qualifier("talisDS") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


}
