package com.cloud.proxy.weatherapigateway.config;

import com.cloud.proxy.weatherapigateway.locator.ApiPathRouteLocatorImpl;
import com.cloud.proxy.weatherapigateway.service.ApiRouteService;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WeatherGatewayConfig {

    @Bean
    public RouteLocator routeLocator(final ApiRouteService apiRouteService,
                                     final RouteLocatorBuilder routeLocatorBuilder) {

        return new ApiPathRouteLocatorImpl(apiRouteService, routeLocatorBuilder);
    }
}
