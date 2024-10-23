package org.example.nbp;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.example.nbp.dto.RequestRatesBody;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NbpApplicationTests {


    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest").withInitScript("schema.sql");
    @LocalServerPort
    private static int dynamicPort;
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance().options(wireMockConfig().port(dynamicPort)).build();
    @Autowired
    WebTestClient webTestClient;

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("baseUrl", wireMockServer::baseUrl);
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://" + postgreSQLContainer.getHost() + ":" + postgreSQLContainer.getFirstMappedPort() + "/" + postgreSQLContainer.getDatabaseName());
        registry.add("spring.r2dbc.username", postgreSQLContainer::getUsername);
        registry.add("spring.r2dbc.password", postgreSQLContainer::getPassword);

    }

    @BeforeAll
    static void startPostgres() {
        postgreSQLContainer.start();

    }

    @AfterAll
    static void stopPostgres() {
        postgreSQLContainer.stop();
    }
    @Test
    void getRatesCHF_shouldReturnCreatedAndMid(){
        RequestRatesBody requestRatesBody = requestRatesBody("CHF", "Łukasz Bak");
        webTestClient.post().uri("/currencies/get-current-currency-value-command").contentType(MediaType.APPLICATION_JSON).bodyValue(requestRatesBody)
                .exchange().expectStatus().isCreated().expectBody().jsonPath("$.value").isEqualTo(4.636);
    }
    @Test
    void getRatesEUR_shouldReturnCreatedAndMid(){
        RequestRatesBody requestRatesBody = requestRatesBody("EUR", "Jan Kowalski");
        webTestClient.post().uri("/currencies/get-current-currency-value-command").contentType(MediaType.APPLICATION_JSON).bodyValue(requestRatesBody)
                .exchange().expectStatus().isCreated().expectBody().jsonPath("$.value").isEqualTo(4.3344);
    }
    @Test
    void getRatesGBP_shouldReturnCreatedAndMid(){
        RequestRatesBody requestRatesBody = requestRatesBody("GBP", "Jan Kowalski");
        webTestClient.post().uri("/currencies/get-current-currency-value-command").contentType(MediaType.APPLICATION_JSON).bodyValue(requestRatesBody)
                .exchange().expectStatus().isCreated().expectBody().jsonPath("$.value").isEqualTo(5.2168);
    }
    @Test
    void getRatesHUF_shouldReturnCreatedAndMid(){
        RequestRatesBody requestRatesBody = requestRatesBody("HUF", "Jan Kowalski");
        webTestClient.post().uri("/currencies/get-current-currency-value-command").contentType(MediaType.APPLICATION_JSON).bodyValue(requestRatesBody)
                .exchange().expectStatus().isCreated().expectBody().jsonPath("$.value").isEqualTo(0.01079);
    }
    private RequestRatesBody requestRatesBody(String currency,String name ){
        return new RequestRatesBody(currency,name);
    }
}
