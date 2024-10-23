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
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.file.Files;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NbpApplicationTests {


    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");
    @LocalServerPort
    private static int dynamicPort;
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance().options(wireMockConfig().port(dynamicPort)).build();
    @Autowired
    WebTestClient webTestClient;
    @Autowired
    RatesInfoRepository repository;

    @Autowired
    private DatabaseClient databaseClient;

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
    void getRatesCHF_shouldReturnCreatedAndMid() {
        RequestRatesBody requestRatesBody = requestRatesBody("CHF", "Łukasz Bak");
        webTestClient.post().uri("/currencies/get-current-currency-value-command").contentType(MediaType.APPLICATION_JSON).bodyValue(requestRatesBody).exchange().expectStatus().isCreated().expectBody().jsonPath("$.value").isEqualTo(4.636);

        Mono<RatesInfo> savedCurrencyRate = repository.findByCurrency(requestRatesBody.currency());

        StepVerifier.create(savedCurrencyRate).expectNextMatches(rateInfo -> rateInfo.getCourse() == (4.636) && rateInfo.getPersonalData().equals("Łukasz Bak") && rateInfo.getCurrency().equals("CHF")).verifyComplete();

    }

    @Test
    void getRatesEUR_shouldReturnCreatedAndMid() {
        RequestRatesBody requestRatesBody = requestRatesBody("EUR", "Jan Kowalski");
        webTestClient.post().uri("/currencies/get-current-currency-value-command").contentType(MediaType.APPLICATION_JSON).bodyValue(requestRatesBody).exchange().expectStatus().isCreated().expectBody().jsonPath("$.value").isEqualTo(4.3344);

        Mono<RatesInfo> savedCurrencyRate = repository.findByCurrency(requestRatesBody.currency());

        StepVerifier.create(savedCurrencyRate).expectNextMatches(rateInfo -> rateInfo.getCourse() == (4.3344) && rateInfo.getPersonalData().equals("Jan Kowalski") && rateInfo.getCurrency().equals("EUR")).verifyComplete();

    }

    @Test
    void getRatesGBP_shouldReturnCreatedAndMid() {
        RequestRatesBody requestRatesBody = requestRatesBody("GBP", "Jan Kowalski");
        webTestClient.post().uri("/currencies/get-current-currency-value-command").contentType(MediaType.APPLICATION_JSON).bodyValue(requestRatesBody).exchange().expectStatus().isCreated().expectBody().jsonPath("$.value").isEqualTo(5.2168);

        Mono<RatesInfo> savedCurrencyRate = repository.findByCurrency(requestRatesBody.currency());

        StepVerifier.create(savedCurrencyRate).expectNextMatches(rateInfo -> rateInfo.getCourse() == (5.2168) && rateInfo.getPersonalData().equals("Jan Kowalski") && rateInfo.getCurrency().equals("GBP")).verifyComplete();

    }

    @Test
    void getRatesHUF_shouldReturnCreatedAndMid() {
        RequestRatesBody requestRatesBody = requestRatesBody("HUF", "Jan Kowalski");
        webTestClient.post().uri("/currencies/get-current-currency-value-command").contentType(MediaType.APPLICATION_JSON).bodyValue(requestRatesBody).exchange().expectStatus().isCreated().expectBody().jsonPath("$.value").isEqualTo(0.01079);

        Mono<RatesInfo> savedCurrencyRate = repository.findByCurrency(requestRatesBody.currency());

        StepVerifier.create(savedCurrencyRate).expectNextMatches(rateInfo -> rateInfo.getCourse() == (0.01079) && rateInfo.getPersonalData().equals("Jan Kowalski") && rateInfo.getCurrency().equals("HUF")).verifyComplete();

    }

    @Test
    void getRatesEURO_shouldReturnNotFound() {
        String jsonMessage = "{\"status\": 404,\"message\": \"Currency code EURO not found\"}";
        RequestRatesBody requestRatesBody = requestRatesBody("EURO", "Jan Kowalski");
        webTestClient.post().uri("/currencies/get-current-currency-value-command").contentType(MediaType.APPLICATION_JSON).bodyValue(requestRatesBody).exchange().expectStatus().isNotFound().expectBody().json(jsonMessage);
    }

    @Test
    void getRatesUSD1_shouldReturnNotFound() {
        String jsonMessage = "{\"status\": 404,\"message\": \"Currency code USD1 not found\"}";
        RequestRatesBody requestRatesBody = requestRatesBody("USD1", "Jan Kowalski");
        webTestClient.post().uri("/currencies/get-current-currency-value-command").contentType(MediaType.APPLICATION_JSON).bodyValue(requestRatesBody).exchange().expectStatus().isNotFound().expectBody().json(jsonMessage);
    }


    private RequestRatesBody requestRatesBody(String currency, String name) {
        return new RequestRatesBody(currency, name);
    }

    private void loadDataFromSqlFile() throws Exception {
        String sqlScript = new String(Files.readAllBytes(Paths.get("src/test/resources/data.sql")));


        Flux.fromArray(sqlScript.split(";")).flatMap(sql -> databaseClient.sql(sql.trim()).then()).subscribe();
    }


}
