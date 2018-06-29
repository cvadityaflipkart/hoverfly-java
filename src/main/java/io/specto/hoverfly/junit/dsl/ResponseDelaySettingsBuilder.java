package io.specto.hoverfly.junit.dsl;

import io.specto.hoverfly.junit.core.model.DelaySettings;
import io.specto.hoverfly.junit.core.model.Request;

import java.util.concurrent.TimeUnit;

public class ResponseDelaySettingsBuilder extends AbstractDelaySettingsBuilder {

    private StubServiceBuilder invoker;

    ResponseDelaySettingsBuilder(int delay, TimeUnit delayTimeUnit) {
        super(delay, delayTimeUnit);
    }

    public ResponseDelaySettingsBuilder to(StubServiceBuilder invoker) {
        this.invoker = invoker;
        return this;
    }

    void forRequest(Request request) {
        if (isValid()) {
            // TODO upgrade
//            String path = request.getDestination().getMatchPattern() + request.getPath().getMatchPattern();
            String path = "";
            invoker.addDelaySetting(new DelaySettings(toPattern(path), getConvertedDelay(), null));
        }
    }

}
