package de.ostfalia.application.data.repository.talsperrendaten;

import de.ostfalia.application.data.entity.Talsperrendaten;
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
        basePackageClasses = TalsperrendatenRepository.class,
        entityManagerFactoryRef = "talsperrendatenEntityManagerFactory",
        transactionManagerRef = "talsperrendatenTransactionManager"
)
public class TalsperrendatenJpaConfiguration {

    @Bean
    public LocalContainerEntityManagerFactoryBean talsperrendatenEntityManagerFactory(
            @Qualifier("talisDS")DataSource dataSource, EntityManagerFactoryBuilder builder
    ){
        return builder
                .dataSource(dataSource)
                .packages(Talsperrendaten.class)
                .build();
    }
    @Bean
    public PlatformTransactionManager talsperrendatenTransactionManager(
            @Qualifier("talsperrendatenEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
    }

}
