package com.cloud.proxy.weatherapigateway.config;

import org.testcontainers.containers.PostgreSQLContainer;

import static java.lang.String.format;

public class CustomTestDBContainer extends PostgreSQLContainer<CustomTestDBContainer> {
    private static final String IMAGE_VERSION = "postgres:11.1";
    private static final String DATABASE_NAME = "weathergatewaytest";
    private static final String DATABASE_USER = "weathertestuser";
    private static final String DATABASE_PASSWORD = "weathertestpwd";

    private static CustomTestDBContainer container;

    private CustomTestDBContainer() {

        super(IMAGE_VERSION);
    }

    public static CustomTestDBContainer getInstance() {
        if (container == null) {
            container = new CustomTestDBContainer()
                    .withDatabaseName(DATABASE_NAME)
                    .withUsername(DATABASE_USER)
                    .withPassword(DATABASE_PASSWORD);
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", format("r2dbc:pool:postgresql://%s:%s/%s",
                container.getHost(),
                container.getFirstMappedPort(),
                DATABASE_NAME));
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
        super.stop();
    }
}
