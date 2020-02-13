package io.specto.hoverfly.testng;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import io.specto.hoverfly.testng.api.TestNgClassRule;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static io.specto.hoverfly.junit.core.SimulationSource.classpath;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Listeners(HoverflyListener.class)
public class NonClassRuleHoverflyRuleTest {

    @TestNgClassRule
    public static HoverflyExtension hoverflyExtension = HoverflyExtension.inSimulationMode(classpath("test-service.json"));

    private RestTemplate restTemplate = new RestTemplate();
    private Client jerseyClient = Client.create();

    @Test
    public void shouldWorkWithRestTemplate() throws URISyntaxException {
        // Given
        final RequestEntity<String> bookFlightRequest = RequestEntity.post(new URI("https://www.my-test.com/api/bookings"))
                .contentType(APPLICATION_JSON)
                .body("{\"flightId\": \"1\"}");

        // When
        final ResponseEntity<String> bookFlightResponse = restTemplate.exchange(bookFlightRequest, String.class);

        // Then
        assertThat(bookFlightResponse.getStatusCode()).isEqualTo(CREATED);
        assertThat(bookFlightResponse.getHeaders().getLocation()).isEqualTo(new URI("http://localhost/api/bookings/1"));
    }

    @Test
    public void shouldWorkWithJerseyClient() {

        // Given
        WebResource resource = jerseyClient.resource("https://www.my-test.com/api/bookings/1");

        // When
        ClientResponse response = resource.get(ClientResponse.class);

        // Then
        assertThat(response.getStatus()).isEqualTo(OK.value());
        assertThatJson(response.getEntity(String.class)).isEqualTo("{" +
                "\"bookingId\":\"1\"," +
                "\"origin\":\"London\"," +
                "\"destination\":\"Singapore\"," +
                "\"time\":\"2011-09-01T12:30\"," +
                "\"_links\":{\"self\":{\"href\":\"http://localhost/api/bookings/1\"}}" +
                "}");
    }

}
