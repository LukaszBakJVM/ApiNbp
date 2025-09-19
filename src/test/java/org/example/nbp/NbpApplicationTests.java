package org.example.nbp;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient

class NbpApplicationTests {


    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest").withInitScript("schema.sql");
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
    void getSavedRatesByCurrency_shouldReturnOk() {
        String currency = "AUD";
        //   loadDataFromSqlFile();
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/currencies/find-data").queryParam("code", currency).build()).accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectBody().json(Response.findByCurrency);


    }
    @Test
    void getSavedRatesByDate_shouldReturnOk() {
        String date = "2024-10-24";
        loadDataFromSqlFile();
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/currencies/find-data").queryParam("date", date).build()).accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectBody().json(Response.findByDate);


    }








    @Test
    void getAllSavedRates_shouldReturnOk() {
        loadDataFromSqlFile();
        webTestClient.get().uri("/currencies/requests").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk().expectBody().json(Response.findAll);

    }



    private void loadDataFromSqlFile() {
        String sqlScript;
        try {
            sqlScript = new String(Files.readAllBytes(Paths.get("src/test/resources/data.sql")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        Flux.fromArray(sqlScript.split(";")).map(String::trim).filter(sql -> !sql.isEmpty()).flatMap(sql -> databaseClient.sql(sql).then()).subscribe();


    }


}
