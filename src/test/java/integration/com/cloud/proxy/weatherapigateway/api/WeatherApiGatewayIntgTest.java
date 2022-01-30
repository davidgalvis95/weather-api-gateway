package com.cloud.proxy.weatherapigateway.api;

import com.cloud.proxy.weatherapigateway.config.CustomTestDBContainer;
import com.cloud.proxy.weatherapigateway.model.ApiRoute;
import com.cloud.proxy.weatherapigateway.model.CreateOrUpdateApiRouteRequest;
import com.cloud.proxy.weatherapigateway.repository.ApiRouteRepository;
import com.cloud.proxy.weatherapigateway.service.ApiRouteServiceImpl;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.cloud.proxy.weatherapigateway.model.WeatherGatewayApiPath.INTERNAL_API_ROUTES;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ActiveProfiles("test")
@AutoConfigureWebTestClient(timeout = "PT10M")
@Testcontainers
@TestPropertySource(locations = {"classpath:application-test.yml"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WeatherApiGatewayIntgTest {

    private static WireMockServer wireMockServer;

    Logger logger = Logger.getLogger(WeatherApiGatewayIntgTest.class.getName());

    public static final String FAKE_SERVICE_PATH = "/fake/response";

    public static final String FAKE_SERVICE_RESPONSE_PAYLOAD = "{\n\"fakePayload\":{\n\"fakeProp1\":1,\n\"fakeProp2\":2\n}\n}";

    public static final String ADDITION_TO_PATH = "/somethingExtra";

    public static final String FAKE_SERVICE_UPDATED_RESPONSE_PAYLOAD = "{\n\"fakeUpdatedPayload\":{\n\"fakeUpdatedProp1\":1,\n\"fakeUpdatedProp2\":2\n}\n}";


    @Value("${fakeService.uri}")
    private String urii;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ApiRouteRepository apiRouteRepository;

    @ClassRule
    public static PostgreSQLContainer<CustomTestDBContainer> container = CustomTestDBContainer.getInstance();


    @BeforeAll
    static void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        System.setProperty("FAKE_SERVICE_URL", wireMockServer.baseUrl());
        container.start();
        generateStubs();
    }

    @Test
    void testCreateNewFakeRouteAndCallToFakeService() {

        //Create a new ApiRoute
        final CreateOrUpdateApiRouteRequest createRequest = CreateOrUpdateApiRouteRequest.builder()
                .path(FAKE_SERVICE_PATH)
                .method(HttpMethod.GET.name())
                .uri(wireMockServer.baseUrl())
                .build();

        final FluxExchangeResult<ApiRoute> reactiveResponse = webTestClient.post()
                .uri(INTERNAL_API_ROUTES)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .body(Mono.just(createRequest), CreateOrUpdateApiRouteRequest.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(ApiRoute.class);

        final ApiRoute createdRoute = reactiveResponse.getResponseBody().share().blockFirst();

//        //Query the repository and get the ApiRoute by id and compare against the created one
        assertNotNull(createdRoute);
        assertNotNull(Optional.of(createdRoute).map(ApiRoute::getId).orElse(null));

        final ApiRoute currentRoute = apiRouteRepository.findById(createdRoute.getId()).block();

        assertNotNull(currentRoute);
        assertEquals(createdRoute.getMethod(), currentRoute.getMethod());
        assertEquals(createdRoute.getPath(), currentRoute.getPath());
        assertEquals(createdRoute.getUri(), currentRoute.getUri());

        //Hit the gateway to get the fake service's response of the created payload
        webTestClient.get()
                .uri(createRequest.getUri() + FAKE_SERVICE_PATH)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.fakePayload.fakeProp1").isEqualTo(1)
                .jsonPath("$.fakePayload.fakeProp2").isEqualTo(2);
    }

    @Test
    void testCreateNewFakeRouteUpdateItCallToFakeServiceWhenCreatedAndWhenUpdated() {

        //Create a new ApiRoute
        final CreateOrUpdateApiRouteRequest createRequest = CreateOrUpdateApiRouteRequest.builder()
                .path(FAKE_SERVICE_PATH)
                .method(HttpMethod.GET.name())
                .uri(wireMockServer.baseUrl())
                .build();

        final FluxExchangeResult<ApiRoute> reactiveResponse = webTestClient.post()
                .uri(INTERNAL_API_ROUTES)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .body(Mono.just(createRequest), CreateOrUpdateApiRouteRequest.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(ApiRoute.class);

        final ApiRoute createdRoute = reactiveResponse.getResponseBody().share().blockFirst();

//        //Query the repository and get the ApiRoute by id and compare against the created one
        assertNotNull(createdRoute);
        assertNotNull(Optional.of(createdRoute).map(ApiRoute::getId).orElse(null));

        final ApiRoute currentRoute = apiRouteRepository.findById(createdRoute.getId()).block();

        assertNotNull(currentRoute);
        assertEquals(createdRoute.getMethod(), currentRoute.getMethod());
        assertEquals(createdRoute.getPath(), currentRoute.getPath());
        assertEquals(createdRoute.getUri(), currentRoute.getUri());


        //Hit the gateway to get the fake service's response of the created payload
        webTestClient.get()
                .uri(createRequest.getUri() + FAKE_SERVICE_PATH)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.fakePayload.fakeProp1").isEqualTo(1)
                .jsonPath("$.fakePayload.fakeProp2").isEqualTo(2);

        final CreateOrUpdateApiRouteRequest updateRequest = CreateOrUpdateApiRouteRequest.builder()
                .path(FAKE_SERVICE_PATH + ADDITION_TO_PATH)
                .method(HttpMethod.GET.name())
                .uri(wireMockServer.baseUrl())
                .build();

        webTestClient.put()
                .uri(INTERNAL_API_ROUTES + "/" + createdRoute.getId())
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .body(Mono.just(updateRequest), CreateOrUpdateApiRouteRequest.class)
                .exchange()
                .expectStatus()
                .isNoContent()
                .returnResult(ApiRoute.class);

        final ApiRoute updatedRoute = apiRouteRepository.findById(createdRoute.getId()).block();

        assertNotNull(updatedRoute);
        assertEquals(updateRequest.getMethod(), updatedRoute.getMethod());
        assertEquals(updateRequest.getPath(), updatedRoute.getPath());
        assertEquals(updateRequest.getUri(), updatedRoute.getUri());

        //Hit the gateway to get the fake service's response of the created payload
        webTestClient.get()
                .uri(createRequest.getUri() + FAKE_SERVICE_PATH + ADDITION_TO_PATH)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.fakeUpdatedPayload.fakeUpdatedProp1").isEqualTo(1)
                .jsonPath("$.fakeUpdatedPayload.fakeUpdatedProp2").isEqualTo(2);
    }

    @Test
    void testCreateNewFakeRouteUpdateItAndDeleteIt() {

        //Create a new ApiRoute
        final CreateOrUpdateApiRouteRequest createRequest = CreateOrUpdateApiRouteRequest.builder()
                .path(FAKE_SERVICE_PATH)
                .method(HttpMethod.GET.name())
                .uri(wireMockServer.baseUrl())
                .build();

        final FluxExchangeResult<ApiRoute> reactiveResponse = webTestClient.post()
                .uri(INTERNAL_API_ROUTES)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .body(Mono.just(createRequest), CreateOrUpdateApiRouteRequest.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .returnResult(ApiRoute.class);

        final ApiRoute createdRoute = reactiveResponse.getResponseBody().share().blockFirst();

//        //Query the repository and get the ApiRoute by id and compare against the created one
        assertNotNull(createdRoute);
        assertNotNull(Optional.of(createdRoute).map(ApiRoute::getId).orElse(null));

        final ApiRoute currentRoute = apiRouteRepository.findById(createdRoute.getId()).block();

        assertNotNull(currentRoute);
        assertEquals(createdRoute.getMethod(), currentRoute.getMethod());
        assertEquals(createdRoute.getPath(), currentRoute.getPath());
        assertEquals(createdRoute.getUri(), currentRoute.getUri());


        //Hit the gateway to get the fake service's response of the created payload
        webTestClient.get()
                .uri(createRequest.getUri() + FAKE_SERVICE_PATH)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.fakePayload.fakeProp1").isEqualTo(1)
                .jsonPath("$.fakePayload.fakeProp2").isEqualTo(2);

        final CreateOrUpdateApiRouteRequest updateRequest = CreateOrUpdateApiRouteRequest.builder()
                .path(FAKE_SERVICE_PATH + ADDITION_TO_PATH)
                .method(HttpMethod.GET.name())
                .uri(wireMockServer.baseUrl())
                .build();

        webTestClient.put()
                .uri(INTERNAL_API_ROUTES + "/" + createdRoute.getId())
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .body(Mono.just(updateRequest), CreateOrUpdateApiRouteRequest.class)
                .exchange()
                .expectStatus()
                .isNoContent()
                .returnResult(ApiRoute.class);

        final ApiRoute updatedRoute = apiRouteRepository.findById(createdRoute.getId()).block();

        assertNotNull(updatedRoute);
        assertEquals(updateRequest.getMethod(), updatedRoute.getMethod());
        assertEquals(updateRequest.getPath(), updatedRoute.getPath());
        assertEquals(updateRequest.getUri(), updatedRoute.getUri());

        //Hit the gateway to get the fake service's response of the created payload
        webTestClient.get()
                .uri(createRequest.getUri() + FAKE_SERVICE_PATH + ADDITION_TO_PATH)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.fakeUpdatedPayload.fakeUpdatedProp1").isEqualTo(1)
                .jsonPath("$.fakeUpdatedPayload.fakeUpdatedProp2").isEqualTo(2);

        webTestClient.delete()
                .uri(INTERNAL_API_ROUTES + "/" + createdRoute.getId())
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isNoContent()
                .returnResult(ApiRoute.class);

        final ApiRoute noRoute = apiRouteRepository.findById(createdRoute.getId()).block();
        assertNull(noRoute);

        //Hit the gateway to get the fake service's response of the created payload
        webTestClient.get()
                .uri(createRequest.getUri() + FAKE_SERVICE_PATH + ADDITION_TO_PATH)
                .exchange()
                .expectStatus()
                .isNotFound();
    }


    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
        container.stop();
    }

    private static void generateStubs() {
        wireMockServer.stubFor(get(urlEqualTo(FAKE_SERVICE_PATH))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withBody(FAKE_SERVICE_RESPONSE_PAYLOAD)
                        .withHeader(ACCEPT, APPLICATION_JSON_VALUE)));

        wireMockServer.stubFor(get(urlEqualTo(FAKE_SERVICE_PATH + ADDITION_TO_PATH))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withBody(FAKE_SERVICE_UPDATED_RESPONSE_PAYLOAD)
                        .withHeader(ACCEPT, APPLICATION_JSON_VALUE)));
//        stubFor(get(urlEqualTo("/delay/3"))
//                .willReturn(aResponse()
//                        .withBody("no fallback")
//                        .withFixedDelay(3000)));
    }

    private static ApiRoute setNewApiRoute(ApiRoute apiRoute,
                                           CreateOrUpdateApiRouteRequest createOrUpdateApiRouteRequest) {
        apiRoute.setPath(createOrUpdateApiRouteRequest.getPath());
        apiRoute.setMethod(createOrUpdateApiRouteRequest.getMethod());
        apiRoute.setUri(createOrUpdateApiRouteRequest.getUri());
        return apiRoute;
    }
}
