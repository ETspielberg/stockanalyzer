package org.unidue.ub.libintel.stockanalyzer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "dataEntityManagerFactory",
        transactionManagerRef = "dataTransactionManager",
        basePackages = { "org.unidue.ub.libintel.stockanalyzer.datarepositories" }
)
public class DataDatabaseConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.data")
    public DataSource dataDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean
    dataEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("dataDataSource") DataSource dataDataSource
    ) {
        return
                builder
                        .dataSource(dataDataSource)
                        .packages("org.unidue.ub.libintel.stockanalyzer.model.data")
                        .build();
    }

    @Bean
    public PlatformTransactionManager dataTransactionManager(
            @Qualifier("dataEntityManagerFactory") EntityManagerFactory
                    barEntityManagerFactory
    ) {
        return new JpaTransactionManager(barEntityManagerFactory);
    }
}
