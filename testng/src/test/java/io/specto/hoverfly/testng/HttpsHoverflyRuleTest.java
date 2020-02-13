package io.specto.hoverfly.testng;

import io.specto.hoverfly.testng.api.TestNgClassRule;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static io.specto.hoverfly.junit.core.SimulationSource.classpath;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;


@Listeners(HoverflyListener.class)
public class HttpsHoverflyRuleTest {

    @TestNgClassRule
    public static HoverflyExtension hoverflyExtension = HoverflyExtension.inSimulationMode();

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void shouldBeAbleToGetABookingUsingHttps() {

        //Given
        hoverflyExtension.simulate(classpath("test-service.json"));

        // When
        final ResponseEntity<String> getBookingResponse = restTemplate.getForEntity("https://www.my-test.com/api/bookings/1", String.class);

        // Then
        assertThat(getBookingResponse.getStatusCode()).isEqualTo(OK);
        assertThatJson(getBookingResponse.getBody()).isEqualTo("{" +
                "\"bookingId\":\"1\"," +
                "\"origin\":\"London\"," +
                "\"destination\":\"Singapore\"," +
                "\"time\":\"2011-09-01T12:30\"," +
                "\"_links\":{\"self\":{\"href\":\"http://localhost/api/bookings/1\"}}" +
                "}");
    }

    @Test
    public void shouldWorkWithRestTemplateWhenHttpsResponseDoesNotContainBodyOrHeaderInTheSimulation() {
        //Given
        hoverflyExtension.simulate(classpath("simulations/v5-simulation-without-response-body.json"));

        // When
        final ResponseEntity<String> firstResponse = restTemplate.getForEntity("https://www.my-test.com/api/bookings/1", String.class);
        final ResponseEntity<String> secondResponse = restTemplate.getForEntity("https://www.my-test.com/api/bookings/1", String.class);

        // Then
        assertThat(firstResponse.getStatusCode()).isEqualTo(OK);
        assertThat(firstResponse.getBody()).isNull();
        assertThat(firstResponse.getHeaders()).containsKey("Transfer-Encoding");
        assertThat(secondResponse.getStatusCode()).isEqualTo(OK);
        assertThat(secondResponse.getBody()).isNull();
        assertThat(secondResponse.getHeaders()).containsKey("Transfer-Encoding");
    }
}
