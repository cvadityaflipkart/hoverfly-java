package io.specto.hoverfly.testng;

import io.specto.hoverfly.testng.api.TestNgClassRule;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.net.InetSocketAddress;
import java.net.Socket;

import static io.specto.hoverfly.junit.core.HoverflyConfig.localConfigs;
import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;
import static org.assertj.core.api.Assertions.assertThat;

@Listeners(HoverflyListener.class)
public class DiffModeTest {

    private static final int ADMIN_PROXY_PORT = 54321;

    @TestNgClassRule
    private static HoverflyExtension hoverflyExtension = HoverflyExtension.inDiffMode(dsl(
            service("http://localhost:" + ADMIN_PROXY_PORT)
                    .get("/api/v2/state")
                    .willReturn(success().body("expected message")),
            service("http://localhost:" + ADMIN_PROXY_PORT)
                    .get("/api/health")
                    .willReturn(success().body("{\"message\":\"Hoverfly is healthy\"}"))
    ), localConfigs().proxyLocalHost().adminPort(ADMIN_PROXY_PORT)).printSimulationData();

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    public void shouldRecordDiffAndDiffAssertionFail() {
        // when
        ResponseEntity<Void> response =
                restTemplate.getForEntity(String.format("http://localhost:%s/api/v2/state", ADMIN_PROXY_PORT), Void.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verifyExceptionThrownByDiffAssertion(true);
    }

    @Test
    public void shouldRecordDiffAndDiffAssertionRuleFail() {
        verifyExceptionThrownByDiffAssertionRule(true, "assertStateApi");
    }

    @Test
    public void shouldRecordNoDiffWhenResponsesAreSameAndDiffAssertionShouldNotFail() {
        // when
        ResponseEntity<Void> response =
                restTemplate.getForEntity(String.format("http://localhost:%s/api/health", ADMIN_PROXY_PORT), Void.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        hoverflyExtension.assertThatNoDiffIsReported();
    }

    @Test
    public void shouldRecordNoDiffWhenResponsesAreSameAndDiffAssertionRuleShouldNotFail() {
        verifyExceptionThrownByDiffAssertionRule(false, "assertHealthApi");
    }

    @Test
    public void diffAssertionShouldResetAllRecordedDiffs() {
        // given
        restTemplate.getForEntity(String.format("http://localhost:%s/api/v2/state", ADMIN_PROXY_PORT), Void.class);

        // when
        verifyExceptionThrownByDiffAssertion(true);

        // then
        hoverflyExtension.assertThatNoDiffIsReported();
    }

    @Test
    public void diffAssertionRuleShouldResetAllRecordedDiffs() {
        verifyExceptionThrownByDiffAssertionRule(true, "assertStateApi");
        hoverflyExtension.assertThatNoDiffIsReported();
    }

    @Test
    public void ShouldResetAllRecordedDiffs() {
        // given
        restTemplate.getForEntity(String.format("http://localhost:%s/api/v2/state", ADMIN_PROXY_PORT), Void.class);

        // when
        hoverflyExtension.resetDiffs();

        // then
        hoverflyExtension.assertThatNoDiffIsReported();
    }

    @Test
    public void diffAssertionShouldNotResetRecordedDiffs() {
        // given
        restTemplate.getForEntity(String.format("http://localhost:%s/api/v2/state", ADMIN_PROXY_PORT), Void.class);

        // when
        verifyExceptionThrownByDiffAssertion(false);

        // then
        verifyExceptionThrownByDiffAssertion(true);
    }

    private void verifyExceptionThrownByDiffAssertion(boolean shouldReset) {
        try {
            hoverflyExtension.assertThatNoDiffIsReported(shouldReset);
        } catch (Throwable t) {
            verifyExceptionAssertionErrorWithDiff(t);
            return;
        }
        Assertions.fail("Expecting code to raise a AssertionError containing a recorded diff");
    }

    private void verifyExceptionThrownByDiffAssertionRule(boolean shouldBeThrown, String methodName) {

        Request assertStateApi = Request.method(NoDiffAssertionTest.class, methodName);
        Result result = new JUnitCore().run(assertStateApi);

        if (shouldBeThrown) {
            assertThat(result.getFailures()).hasSize(1);
            verifyExceptionAssertionErrorWithDiff(result.getFailures().get(0).getException());
        } else {
            assertThat(result.getFailures()).isEmpty();
        }
    }

    private void verifyExceptionAssertionErrorWithDiff(Throwable t) {
        assertThat(t)
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("method='GET'")
                .hasMessageContaining("host='localhost:54321'")
                .hasMessageContaining("path='/api/v2/state'")
                .hasMessageContaining("query=''")
                .hasMessageContaining("have been recorded 1 diff(s)")
                .hasMessageContaining("1. diff")
                .hasMessageContaining("(1.)");
    }

    public static class NoDiffAssertionTest {

        public NoDiffAssertion noDiffAssertion = new NoDiffAssertion(hoverflyExtension);

        private final RestTemplate restTemplate = new RestTemplate();

        @BeforeClass
        public static void skipIfNoHoverfly() {
            Throwable exception = null;
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("localhost", ADMIN_PROXY_PORT), 10);
            } catch (Throwable t) {
                exception = t;
            }
            Assume.assumeNoException(exception);
        }

        @org.junit.Test
        public void assertStateApi() {
            ResponseEntity<Void> response =
                    restTemplate.getForEntity(String.format("http://localhost:%s/api/v2/state", ADMIN_PROXY_PORT),
                            Void.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @org.junit.Test
        public void assertHealthApi() {
            ResponseEntity<Void> response =
                    restTemplate.getForEntity(String.format("http://localhost:%s/api/health", ADMIN_PROXY_PORT), Void.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Before
        public void beforeTest() {
            noDiffAssertion.cleanDiffs();
        }

        @After
        public void afterTest() {
            noDiffAssertion.performAssertion();
        }
    }
}
