package com.cloud.proxy.weatherapigateway.config;

import com.cloud.proxy.weatherapigateway.service.GatewayRouteService;
import com.cloud.proxy.weatherapigateway.service.GatewayRouteServiceImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteTestConfig {

    @Bean
    public GatewayRouteService gatewayRouteService(ApplicationEventPublisher applicationEventPublisher){
        return new GatewayRouteServiceImpl(applicationEventPublisher);
    }
}
