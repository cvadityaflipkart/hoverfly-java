package io.specto.hoverfly.testng;

import io.specto.hoverfly.junit.core.HoverflyMode;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CaptureOrSimulationModeTest {

    @Test
    public void shouldInstantiateHoverflyInCaptureModeInCaseOfNoRecord() {

        final HoverflyExtension hoverflyRule = HoverflyExtension.inCaptureOrSimulationMode("mynewservicerecord.json");

        assertThat(hoverflyRule.getHoverflyMode()).isEqualTo(HoverflyMode.CAPTURE);

    }

    @Test
    public void shouldInstantiateHoverflyInSimulationModeInCaseOfPreviousRecord() {

        final HoverflyExtension hoverflyRule = HoverflyExtension.inCaptureOrSimulationMode("test-service-below-hoverfly-dir.json");

        assertThat(hoverflyRule.getHoverflyMode()).isEqualTo(HoverflyMode.SIMULATE);
    }

}
