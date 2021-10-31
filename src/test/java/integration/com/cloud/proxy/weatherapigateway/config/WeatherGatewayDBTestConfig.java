package com.cloud.proxy.weatherapigateway.config;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;


@Configuration
@ActiveProfiles("test")
@EnableR2dbcRepositories
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "spring.r2dbc")
public class WeatherGatewayDBTestConfig extends AbstractR2dbcConfiguration {

//    @Autowired
//    private Environment environment;

    private String url;

    private String username;

    private String password;

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {

//        System.out.println(Arrays.toString(environment.getActiveProfiles()));

        return new PostgresqlConnectionFactory(PostgresqlConnectionConfiguration.builder()
                .host((getUrl().split("/")[2]).split(":")[0])
                .port(Integer.parseInt((getUrl().split("/")[2]).split(":")[1]))
                .username(getUsername())
                .password(getPassword())
                .database(getUrl().split("/")[3])
                .build());
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
