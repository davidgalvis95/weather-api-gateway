//package com.cloud.proxy.weatherapigateway.config;
//TODO implement this config
//import liquibase.integration.spring.SpringLiquibase;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class LiquibaseConfig {
//
//    @Bean
//    public DataSource dataSource()  {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//
////        dataSource.setDriverClassName(env.getProperty("spring.datasource.drivername"));
////        dataSource.setUrl(env.getProperty("spring.datasource.url"));
////        dataSource.setUsername(env.getProperty("spring.datasource.username"));
////        dataSource.setPassword(env.getProperty("spring.datasource.password"));
//
//        return dataSource;
//    }
//
//    @Bean
//    @Profile("test")
//    public SpringLiquibase liquibaseTest(){
//        final SpringLiquibase liquibase = new SpringLiquibase();
//
//        liquibase.setDataSource(dataSource());
//        liquibase.setChangeLog("classpath:db.changelog-master.yml");
//
//        return liquibase;
//    }
//}
