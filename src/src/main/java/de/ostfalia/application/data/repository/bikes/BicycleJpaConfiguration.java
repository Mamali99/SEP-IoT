package de.ostfalia.application.data.repository.bikes;

import de.ostfalia.application.data.entity.Bicycle;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackageClasses = BicycleRepository.class,
        entityManagerFactoryRef = "bicycleEntityManagerFactory",
        transactionManagerRef = "bicycleTransactionManager"
)
public class BicycleJpaConfiguration {

    @Bean
    public LocalContainerEntityManagerFactoryBean bicycleEntityManagerFactory(EntityManagerFactoryBuilder builder,
            @Qualifier("bikeDS")DataSource dataSource
            ){
        return builder
                .dataSource(dataSource)
                .packages(Bicycle.class)
                .build();
    }
    @Bean
    public PlatformTransactionManager bycicleTransactionManager(
            @Qualifier("bicycleEntityManagerFactory") LocalContainerEntityManagerFactoryBean bicycleEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(bicycleEntityManagerFactory.getObject()));
    }



}
