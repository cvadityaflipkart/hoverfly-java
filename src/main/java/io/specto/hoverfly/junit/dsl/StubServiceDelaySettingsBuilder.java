package io.specto.hoverfly.junit.dsl;

import io.specto.hoverfly.junit.core.model.DelaySettings;

import java.util.concurrent.TimeUnit;

public class StubServiceDelaySettingsBuilder extends AbstractDelaySettingsBuilder {

    private final StubServiceBuilder invoker;

    StubServiceDelaySettingsBuilder(int delay, TimeUnit delayTimeUnit, StubServiceBuilder invoker) {
        super(delay, delayTimeUnit);
        this.invoker = invoker;
    }

    public StubServiceBuilder forAll() {
        if (isValid()) {
            invoker.addDelaySetting(new DelaySettings(toPattern(invoker.destination), getConvertedDelay(), null));
        }
        return invoker;
    }

    public StubServiceBuilder forMethod(String method) {
        if (isValid()) {
            invoker.addDelaySetting(new DelaySettings(toPattern(invoker.destination), getConvertedDelay(), method));
        }
        return invoker;
    }

}
