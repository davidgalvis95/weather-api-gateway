package com.cloud.proxy.weatherapigateway.service;

import com.cloud.proxy.weatherapigateway.model.ApiRoute;
import com.cloud.proxy.weatherapigateway.model.CreateOrUpdateApiRouteRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ApiRouteService {

    Flux<ApiRoute> findApiRoutes();

    Mono<ApiRoute> findApiRoute(UUID id);

    Mono<ApiRoute> createApiRoute(CreateOrUpdateApiRouteRequest createOrUpdateApiRouteRequest);

    Mono<ApiRoute> updateApiRoute(UUID id, CreateOrUpdateApiRouteRequest createOrUpdateApiRouteRequest);

    Mono<Void> deleteApiRoute(UUID id);
}

