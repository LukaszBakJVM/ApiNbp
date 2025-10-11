package org.example.nbp.service;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.example.nbp.RatesServicesImpl;
import org.example.nbp.dto.ResponseCurrency;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NbpApplicationServiceTests {


    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest").withInitScript("schema.sql");
    @LocalServerPort
    private static int dynamicPort;
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance().options(wireMockConfig().port(dynamicPort)).build();

    @Autowired
    private RatesServicesImpl ratesServices;


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
    void getSavedRatesByCurrency_shouldReturnOk() {
        Instant instant = Instant.parse("2024-10-26T06:51:11.756300Z");

        LocalDate localDate = localDate(instant);


        String currency = "AUD";
        Flux<ResponseCurrency> byCurrency = ratesServices.findByCurrencyAndTime(currency, null);
        ResponseCurrency response = new ResponseCurrency("AUD", 2.6752, localDate);


        StepVerifier.create(byCurrency).expectNextMatches(r -> r.equals(response)).verifyComplete();


    }

    @Test
    void getSavedRatesByDate_shouldReturnOk() {
        String date = "2024-10-24";
        LocalDate parse = LocalDate.parse(date);


        Instant instant = Instant.parse("2024-10-24T06:51:11.756300Z");
        LocalDate localDate = localDate(instant);

        ResponseCurrency response = new ResponseCurrency("HKD", 0.5170, localDate);
        Flux<ResponseCurrency> byCurrency = ratesServices.findByCurrencyAndTime(null, parse);

        StepVerifier.create(byCurrency).expectNextMatches(r -> r.equals(response)).verifyComplete();


    }
    @Test
    void getSavedRatesByCurrencyAndDate_shouldReturnOk() {
        Instant instant = Instant.parse("2024-10-25T08:17:57.687052Z");
        String currency = "THB";

        LocalDate localDate = localDate(instant);

        Flux<ResponseCurrency> byCurrency = ratesServices.findByCurrencyAndTime(currency, localDate);

        ResponseCurrency response = new ResponseCurrency("THB", 0.1192, localDate);


        StepVerifier.create(byCurrency).expectNextMatches(r -> r.equals(response)).verifyComplete();


    }


    @Test
    void getAllSavedRates_shouldReturnOk() {

        //  webTestClient.get().uri("/currencies/requests").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectBody().json(Response.findAll);

    }

    private LocalDate localDate(Instant instant) {
        return instant.atZone(ZoneId.of("Europe/Warsaw")).toLocalDate();

    }
}
