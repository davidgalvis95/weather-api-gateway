package com.cloud.proxy.weatherapigateway.repository;

import com.cloud.proxy.weatherapigateway.model.ApiRoute;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface ApiRouteRepository extends ReactiveCrudRepository<ApiRoute, UUID> {

}