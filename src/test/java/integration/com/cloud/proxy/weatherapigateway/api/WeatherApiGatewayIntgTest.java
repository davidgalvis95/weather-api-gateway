package com.cloud.proxy.weatherapigateway.api;

import com.cloud.proxy.weatherapigateway.config.CustomTestDBContainer;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@Testcontainers
@TestPropertySource(locations = {"classpath:application-test.yml"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WeatherApiGatewayIntgTest {

    @Autowired
    private WebTestClient webTestClient;

    @ClassRule
    public static PostgreSQLContainer<CustomTestDBContainer> container = CustomTestDBContainer.getInstance();

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void setUp(){
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        container.start();
    }

    @Test
    void testCallToLocationService(){
        System.out.println(wireMockServer.baseUrl());
        assertTrue(wireMockServer.isRunning());
    }


    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
        container.stop();
    }
}
