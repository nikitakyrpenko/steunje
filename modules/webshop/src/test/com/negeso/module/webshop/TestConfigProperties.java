package com.negeso.module.webshop;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class TestConfigProperties {

    @Bean(name = "apiSessionFactory")
    public LocalSessionFactoryBean sessionFactory() {

        AnnotationSessionFactoryBean annotationSessionFactoryBean = new AnnotationSessionFactoryBean();
        annotationSessionFactoryBean.setDataSource(dataSource());
        annotationSessionFactoryBean.setHibernateProperties(hibernateProperties());
        annotationSessionFactoryBean.setPackagesToScan("com.negeso.module.webshop.entity");

        return annotationSessionFactoryBean;
    }

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://192.168.14.125:5432/oleg_iam1");
        dataSource.setUsername("oleg_iam1");
        dataSource.setPassword("oleg_iam1");
        return dataSource;
    }

    @Bean
    public Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();

        hibernateProperties.setProperty(
                "show_sql", "true");
        hibernateProperties.setProperty(
                "hibernate.current_session_context_class", "thread");
        hibernateProperties.setProperty(
                "hibernate.hbm2ddl.auto", "none");
        hibernateProperties.setProperty(
                "hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        return hibernateProperties;
    }
}
