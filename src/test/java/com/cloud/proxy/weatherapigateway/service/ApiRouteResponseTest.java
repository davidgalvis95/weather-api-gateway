package com.cloud.proxy.weatherapigateway.service;


import com.cloud.proxy.weatherapigateway.config.RouteTestConfig;
import com.cloud.proxy.weatherapigateway.model.ApiRoute;
import com.cloud.proxy.weatherapigateway.model.CreateOrUpdateApiRouteRequest;
import com.cloud.proxy.weatherapigateway.repository.ApiRouteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RouteTestConfig.class)
public class ApiRouteResponseTest {

    public static final UUID ROUTE_ID = UUID.fromString("ba02fb9c-d65e-4750-93d2-47cdc92afc06");
    private static final String PATH = "/somepath";
    private static final String METHOD = "GET";
    private static final String URI = "http://someurl";

    @MockBean
    private ApiRouteRepository apiRouteRepository;

    @Autowired
    private GatewayRouteService gatewayRouteService;

    private ApiRouteService apiRouteService;

    @BeforeEach
    void setUp(){
        apiRouteService = new ApiRouteServiceImpl(apiRouteRepository, gatewayRouteService);
    }


    @Test
    void testCreateApiRoute(){
        //given
        final CreateOrUpdateApiRouteRequest createOrUpdateApiRouteRequest = CreateOrUpdateApiRouteRequest.builder()
                .path(PATH)
                .method(METHOD)
                .uri(URI)
                .build();

        final ApiRoute apiRoute  = ApiRoute.builder()
                .id(UUID.randomUUID())
                .uri(URI)
                .method(METHOD)
                .path(PATH)
                .build();

        when(apiRouteRepository.save(any(ApiRoute.class))).thenReturn(Mono.just(apiRoute));
        when(apiRouteRepository.findAll()).thenReturn(Flux.just(apiRoute));

        //when
        Mono<ApiRoute> response = apiRouteService.createApiRoute(createOrUpdateApiRouteRequest);
        //then
        verify(apiRouteRepository).save(any(ApiRoute.class));

        response.subscribe(responseObject -> {
            assertEquals(apiRoute, responseObject);
        });

        StepVerifier.create(response)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testUpdateApiRoute(){
        //given
        final String pathUpdated = PATH + "Updated";

        final CreateOrUpdateApiRouteRequest createOrUpdateApiRouteRequest = CreateOrUpdateApiRouteRequest.builder()
                .path(pathUpdated)
                .method(METHOD)
                .uri(URI)
                .build();

        final ApiRoute apiRoute  = ApiRoute.builder()
                .id(UUID.randomUUID())
                .uri(URI)
                .method(METHOD)
                .path(PATH)
                .build();


        final ApiRoute apiRouteUpdated = ((ApiRouteServiceImpl)apiRouteService).setNewApiRoute(apiRoute, createOrUpdateApiRouteRequest);

        when(apiRouteRepository.findById(ROUTE_ID)).thenReturn(Mono.just(apiRoute));
        when(apiRouteRepository.save(apiRouteUpdated)).thenReturn(Mono.just(apiRouteUpdated));

        //when
        final Mono<ApiRoute> response = apiRouteService.updateApiRoute(ROUTE_ID,createOrUpdateApiRouteRequest);

        //then
        response.subscribe(responseObject -> {
            assertEquals(apiRouteUpdated, responseObject);
        });

        StepVerifier.create(response)
                .expectNextCount(1)
                .verifyComplete();

        verify(apiRouteRepository, times(1)).findById(ROUTE_ID);
        verify(apiRouteRepository, times(2)).save(apiRouteUpdated);
    }

    @Test
    void testDeleteApiRoute(){
        //given
        final ApiRoute apiRoute  = ApiRoute.builder()
                .id(ROUTE_ID)
                .uri(URI)
                .method(METHOD)
                .path(PATH)
                .build();

        when(apiRouteRepository.findById(ROUTE_ID)).thenReturn(Mono.just(apiRoute));
        when(apiRouteRepository.deleteById(ROUTE_ID)).thenReturn(Mono.empty());

        //when
        final Mono<Void> response = apiRouteService.deleteApiRoute(ROUTE_ID);

        verify(apiRouteRepository, times(1)).findById(ROUTE_ID);

        //then
        StepVerifier.create(response)
                .expectNextCount(0)
                .verifyComplete();

        verify(apiRouteRepository, times(1)).deleteById(ROUTE_ID);
    }

    @Test
    void testFindApiRoutes(){
        //given
        final ApiRoute apiRoute  = ApiRoute.builder()
                .id(ROUTE_ID)
                .uri(URI)
                .method(METHOD)
                .path(PATH)
                .build();

        when(apiRouteRepository.findAll()).thenReturn(Flux.just(apiRoute));

        //when
        final Flux<ApiRoute> response = apiRouteService.findApiRoutes();

        verify(apiRouteRepository, times(1)).findAll();

        //then
        response.subscribe(responseObject -> {
            assertEquals(apiRoute, responseObject);
        });

        StepVerifier.create(response)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findApiRouteById(){
        //given
        final ApiRoute apiRoute  = ApiRoute.builder()
                .id(ROUTE_ID)
                .uri(URI)
                .method(METHOD)
                .path(PATH)
                .build();

        when(apiRouteRepository.findById(ROUTE_ID)).thenReturn(Mono.just(apiRoute));

        //when
        final Mono<ApiRoute> response = apiRouteService.findApiRoute(ROUTE_ID);

        verify(apiRouteRepository, times(1)).findById(ROUTE_ID);

        //then
        response.subscribe(responseObject -> {
            assertEquals(apiRoute, responseObject);
        });

        StepVerifier.create(response)
                .expectNextCount(1)
                .verifyComplete();
    }
}

