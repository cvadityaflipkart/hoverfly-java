package io.specto.hoverfly.testng;

import io.specto.hoverfly.testng.api.TestNGClassRule;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static io.specto.hoverfly.junit.core.HoverflyConfig.localConfigs;
import static io.specto.hoverfly.junit.core.SimulationSource.classpath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

@Listeners(HoverflyExecutor.class)
public class HoverflyRulePortConfigurationTest {

    private static final int EXPECTED_ADMIN_PORT = 8889;
    private static final int EXPECTED_PROXY_PORT = 8890;

    @TestNGClassRule
    public static HoverflyTestNG hoverflyTestNG = HoverflyTestNG.inSimulationMode(classpath("test-service.json"),
        localConfigs().proxyPort(EXPECTED_PROXY_PORT).adminPort(EXPECTED_ADMIN_PORT));

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void shouldStillVirtualizeServiceAfterConfiguringPorts() {
        // When
        final ResponseEntity<String> getBookingResponse = restTemplate.getForEntity("http://www.my-test.com/api/bookings/1", String.class);

        // Then
        assertThat(getBookingResponse.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void shouldChangeAdminPortToConfiguredPort() {
        final ResponseEntity<String> health = restTemplate.getForEntity(String.format("http://localhost:%s/api/health", EXPECTED_ADMIN_PORT), String.class);
        assertThat(health.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void shouldBeAbleToGetPort() {
        // Then
        assertThat(hoverflyTestNG.getProxyPort()).isEqualTo(EXPECTED_PROXY_PORT);
    }


}
