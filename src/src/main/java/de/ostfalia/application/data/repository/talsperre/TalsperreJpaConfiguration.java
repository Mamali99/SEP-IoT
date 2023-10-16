package de.ostfalia.application.data.repository.talsperre;

import de.ostfalia.application.data.entity.Talsperre;
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
        basePackageClasses = TalsperreRepository.class,
        entityManagerFactoryRef = "talsperreEntityManagerFactory",
        transactionManagerRef = "talsperreTransactionManager"
)
public class TalsperreJpaConfiguration {

    @Bean
    public LocalContainerEntityManagerFactoryBean talsperreEntityManagerFactory(
            @Qualifier("talisDS")DataSource dataSource, EntityManagerFactoryBuilder builder
    ){
        return builder
                .dataSource(dataSource)
                .packages(Talsperre.class)
                .build();
    }
    @Bean
    public PlatformTransactionManager talsperreTransactionManager(
            @Qualifier("talsperreEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
    }

}
