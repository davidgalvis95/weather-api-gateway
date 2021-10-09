package com.cloud.proxy.weatherapigateway.service;

import com.cloud.proxy.weatherapigateway.model.ApiRoute;
import com.cloud.proxy.weatherapigateway.model.CreateOrUpdateApiRouteRequest;
import com.cloud.proxy.weatherapigateway.repository.ApiRouteRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@NoArgsConstructor
public class ApiRouteServiceImpl implements ApiRouteService {

    private ApiRouteRepository apiRouteRepository;

    private GatewayRouteService gatewayRouteService;

    @Autowired
    public ApiRouteServiceImpl(final ApiRouteRepository apiRouteRepository,
                               final GatewayRouteService gatewayRouteService){
        this.apiRouteRepository = apiRouteRepository;
        this.gatewayRouteService = gatewayRouteService;
    }

    @Override
    public Flux<ApiRoute> findApiRoutes() {
        return apiRouteRepository.findAll();
    }

    @Override
    public Mono<ApiRoute> findApiRoute(UUID id) {
        return findAndValidateApiRoute(id);
    }
    @Override
    public Mono<Void> createApiRoute(CreateOrUpdateApiRouteRequest createOrUpdateApiRouteRequest) {
        ApiRoute apiRoute = setNewApiRoute(new ApiRoute(), createOrUpdateApiRouteRequest);
        return apiRouteRepository.save(apiRoute)
                .doOnSuccess(obj -> gatewayRouteService.refreshRoutes())
                .then();
    }
    @Override
    public Mono<Void> updateApiRoute(UUID id,
                                     CreateOrUpdateApiRouteRequest createOrUpdateApiRouteRequest) {
        return findAndValidateApiRoute(id)
                .map(apiRoute -> setNewApiRoute(apiRoute, createOrUpdateApiRouteRequest))
                .flatMap(apiRouteRepository::save)
                .doOnSuccess(obj -> gatewayRouteService.refreshRoutes())
                .then();
    }
    @Override
    public Mono<Void> deleteApiRoute(UUID id) {
        return findAndValidateApiRoute(id)
                .flatMap(apiRoute -> apiRouteRepository.deleteById(apiRoute.getId()))
                .doOnSuccess(obj -> gatewayRouteService.refreshRoutes());
    }

    private Mono<ApiRoute> findAndValidateApiRoute(UUID id) {
        return apiRouteRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new RuntimeException(String.format("Api route with id %s not found", id))));
    }

    private ApiRoute setNewApiRoute(ApiRoute apiRoute,
                                    CreateOrUpdateApiRouteRequest createOrUpdateApiRouteRequest) {
        apiRoute.setPath(createOrUpdateApiRouteRequest.getPath());
        apiRoute.setMethod(createOrUpdateApiRouteRequest.getMethod());
        apiRoute.setUri(createOrUpdateApiRouteRequest.getUri());
        return apiRoute;
    }
}
