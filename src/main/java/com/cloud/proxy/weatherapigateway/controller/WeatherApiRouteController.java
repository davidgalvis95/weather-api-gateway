package com.cloud.proxy.weatherapigateway.controller;

import com.cloud.proxy.weatherapigateway.model.ApiRoute;
import com.cloud.proxy.weatherapigateway.model.ApiRouteResponse;
import com.cloud.proxy.weatherapigateway.model.CreateOrUpdateApiRouteRequest;
import com.cloud.proxy.weatherapigateway.model.WeatherGatewayApiPath;
import com.cloud.proxy.weatherapigateway.service.ApiRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = WeatherGatewayApiPath.INTERNAL_API_ROUTES)
public class WeatherApiRouteController {

    private final ApiRouteService apiRouteService;

    @Autowired
    public WeatherApiRouteController(final ApiRouteService apiRouteService){
        this.apiRouteService = apiRouteService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<ApiRouteResponse>> findApiRoutes() {
        return apiRouteService.findApiRoutes()
                .map(this::convertApiRouteIntoApiRouteResponse)
                .collectList()
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApiRouteResponse> findApiRoute(@PathVariable final UUID id) {
        return apiRouteService.findApiRoute(id)
                .map(response -> convertApiRouteIntoApiRouteResponse(response))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<?> createApiRoute(
            @RequestBody @Validated CreateOrUpdateApiRouteRequest createOrUpdateApiRouteRequest) {
        return apiRouteService.createApiRoute(createOrUpdateApiRouteRequest)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PutMapping(path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<?> updateApiRoute(@PathVariable final UUID id,
                                  @RequestBody @Validated CreateOrUpdateApiRouteRequest createOrUpdateApiRouteRequest) {
        return apiRouteService.updateApiRoute(id, createOrUpdateApiRouteRequest)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @DeleteMapping(path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<?> deleteApiRoute(@PathVariable final UUID id) {
        return apiRouteService.deleteApiRoute(id)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private ApiRouteResponse convertApiRouteIntoApiRouteResponse(ApiRoute apiRoute) {
        return ApiRouteResponse.builder()
                .id(apiRoute.getId())
                .path(apiRoute.getPath())
                .method(apiRoute.getMethod())
                .uri(apiRoute.getUri())
                .build();
    }
}
