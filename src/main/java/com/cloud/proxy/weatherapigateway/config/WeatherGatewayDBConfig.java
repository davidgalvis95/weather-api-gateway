package com.cloud.proxy.weatherapigateway.config;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Data
@Configuration
@EnableR2dbcRepositories
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "spring.r2dbc")
public class WeatherGatewayDBConfig extends AbstractR2dbcConfiguration {

    private static final int PORT = 5432;

    private String url;

    private String username;

    private String password;

    @Override
    @Bean
    //TODO create this bean to run in other instances out of localhost
    public ConnectionFactory connectionFactory() {

        return new PostgresqlConnectionFactory(PostgresqlConnectionConfiguration.builder()
                .host("localhost")
                .port(PORT)
                .username(getUsername())
                .password(getPassword())
                .database(getUrl().split("/")[3])
                .build());
    }
}
