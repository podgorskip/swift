package podgorskip.swift;

import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import podgorskip.swift.model.dto.SwiftCodeRequest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SwiftApplicationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .waitingFor(Wait.forListeningPort());

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7")
            .withExposedPorts(6379)
            .waitingFor(Wait.forLogMessage(".*Ready to accept connections.*", 1));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");

        registry.add("spring.liquibase.change-log", () -> "classpath:db/changelog/db.changelog-master.yaml");
        registry.add("spring.liquibase.url", postgres::getJdbcUrl);
        registry.add("spring.liquibase.user", postgres::getUsername);
        registry.add("spring.liquibase.password", postgres::getPassword);
    }

    @BeforeAll
    static void init() {
        postgres.start();
        redis.start();
    }

    @AfterAll
    static void cleanup() {
        postgres.stop();
        redis.stop();
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testAddSwiftCode_Headquarter() {
        SwiftCodeRequest request = SwiftCodeRequest.builder()
                        .address("OLD TOWN")
                        .countryISO2("PL")
                        .bankName("BANK PKO")
                        .countryName("POLAND")
                        .isHeadquarter(true)
                        .swiftCode("HEADABCDXXX")
                        .build();

        ResponseEntity<String> response = restTemplate.postForEntity("/swift-codes", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("Successfully created swift code");
    }

    @Test
    public void testAddSwiftCode_Branch() {
        SwiftCodeRequest request = SwiftCodeRequest.builder()
                .address("OLD TOWN, 1")
                .countryISO2("PL")
                .bankName("BANK PKO")
                .countryName("POLAND")
                .isHeadquarter(false)
                .swiftCode("SPMLPLP1KKK")
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity("/swift-codes", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("Successfully created swift code");
    }

    @Test
    public void testGetSwiftCode_HasBranch() {
        String swiftCode = "SPMLPLP1XXX";

        ResponseEntity<String> response = restTemplate.getForEntity("/swift-codes/{code}", String.class, swiftCode);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("branches");
    }

    @Test
    public void testGetSwiftCode_NotFound() {
        String swiftCode = "GGGGGGGGGGG";

        ResponseEntity<String> response = restTemplate.getForEntity("/swift-codes/{code}", String.class, swiftCode);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDeleteSwiftCode() {
        String swiftCode = "SPMLPLP1KKK";

        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                "/swift-codes/{code}",
                HttpMethod.DELETE,
                null,
                String.class,
                swiftCode
        );

        ResponseEntity<String> getResponse = restTemplate.getForEntity("/swift-codes/{code}", String.class, swiftCode);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testDeleteSwiftCode_NotFound() {
        String swiftCode = "GGGGGGGGGGG";

        ResponseEntity<String> response = restTemplate.exchange(
                "/swift-codes/{code}",
                HttpMethod.DELETE,
                null,
                String.class,
                swiftCode
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetCountrySwiftCodes() {
        String country = "PL";

        ResponseEntity<String> response = restTemplate.getForEntity("/swift-codes/country/{country}", String.class, country);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
