package io.specto.hoverfly.testng;

import io.specto.hoverfly.testng.api.TestNgClassRule;
import io.specto.hoverfly.testng.models.SimpleBooking;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.net.URI;

import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.core.model.RequestFieldMatcher.newJsonMatcher;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.HttpBodyConverter.json;
import static io.specto.hoverfly.junit.dsl.HttpBodyConverter.jsonWithSingleQuotes;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;
import static io.specto.hoverfly.junit.dsl.matchers.HoverflyMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;

@Listeners(HoverflyListener.class)
public class HoverflyDslResponseTemplatingTest {

    @TestNgClassRule
    public static HoverflyExtension hoverflyExtension = HoverflyExtension.inSimulationMode(dsl(
            service("www.my-test.com")

                    // Path param for template
                    .get("/api/bookings/1")
                    .willReturn(success().body(jsonWithSingleQuotes(
                            "{'id':{{ Request.Path.[2] }},'origin':'London','destination':'Singapore','time':'2011-09-01T12:30','_links':{'self':{'href':'http://localhost/api/bookings/{{ Request.Path.[2] }}'}}}"
                    )))
                    // Query Param for template
                    .get("/api/bookings")
                    .queryParam("destination", "London")
                    .queryParam("page", any())
                    .willReturn(success().body(jsonWithSingleQuotes(
                            "{'id':'1', 'destination':'{{ Request.QueryParam.destination }}','time':'2011-09-01T12:30','_links':{'self':{'href':'http://localhost/api/bookings?page={{ Request.QueryParam.page }}'}}}"
                    )))
                    // Request body for template
                    .put("/api/bookings/1")
                    .body(newJsonMatcher(json(new SimpleBooking(1, "London", "Hong Kong", null)).body()))
                    .willReturn(success().body(jsonWithSingleQuotes("{'id':'1', 'destination':'{{ Request.Body 'jsonpath' '$.destination' }}'}")))

    )).printSimulationData();

    private final RestTemplate restTemplate = new RestTemplate();


    @Test
    public void shouldBeAbleToGenerateResponseFromRequestPathParam() throws Exception {
        // Given
        final RequestEntity<Void> getFlightRequest = RequestEntity.get(new URI("http://www.my-test.com/api/bookings/1")).build();

        // When
        final ResponseEntity<String> response = restTemplate.exchange(getFlightRequest, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JSONObject body = new JSONObject(response.getBody());
        assertThat(body.getInt("id")).isEqualTo(1);
        assertThat(body.getJSONObject("_links")
                .getJSONObject("self")
                .getString("href")).isEqualTo("http://localhost/api/bookings/1");
    }

    @Test
    public void shouldBeAbleToGenerateResponseFromRequestQueryParam() throws Exception {
        // Given
        URI uri = UriComponentsBuilder.fromHttpUrl("http://www.my-test.com")
                .path("/api/bookings")
                .queryParam("destination", "London")
                .queryParam("page", 2)
                .build()
                .toUri();

        // When
        final ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JSONObject body = new JSONObject(response.getBody());
        assertThat(body.getString("destination")).isEqualTo("London");
        assertThat(body.getJSONObject("_links")
                .getJSONObject("self")
                .getString("href")).isEqualTo("http://localhost/api/bookings?page=2");

    }

    @Test
    public void shouldBeAbleToGenerateResponseFromRequestBody() {
        // Given
        URI uri = UriComponentsBuilder.fromHttpUrl("http://www.my-test.com")
                .path("/api/bookings/1")
                .build()
                .toUri();
        RequestEntity<SimpleBooking> request = RequestEntity.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new SimpleBooking(1, "London", "Hong Kong", null));

        // When
        final ResponseEntity<SimpleBooking> response = restTemplate.exchange(request, SimpleBooking.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        SimpleBooking body = response.getBody();
        assertThat(body.getDestination()).isEqualTo("Hong Kong");
    }
}
