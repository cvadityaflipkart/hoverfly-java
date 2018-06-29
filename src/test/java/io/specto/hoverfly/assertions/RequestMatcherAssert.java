package io.specto.hoverfly.assertions;

import io.specto.hoverfly.junit.core.model.Request;
import io.specto.hoverfly.junit.core.model.RequestFieldMatcher;
import org.assertj.core.api.AbstractAssert;

import java.util.List;

import static io.specto.hoverfly.junit.core.model.RequestFieldMatcher.newExactMatcher;
import static org.assertj.core.api.Assertions.assertThat;

public class RequestMatcherAssert extends AbstractAssert<RequestMatcherAssert, Request> {
    RequestMatcherAssert(final Request actual) {
        super(actual, RequestMatcherAssert.class);
    }

    public RequestMatcherAssert hasBodyContainsOneExactMatcher(final String value) {
        isNotNull();
        assertThat(actual.getBody()).containsExactly(newExactMatcher(value));
        return this;
    }

    public RequestMatcherAssert hasDestinationContainsOneExactMatcher(final String value) {
        isNotNull();
        assertThat(actual.getBody()).containsExactly(newExactMatcher(value));
        return this;
    }

    public RequestMatcherAssert hasPathContainsOneExactMatcher(final String value) {
        isNotNull();
        assertThat(actual.getBody()).containsExactly(newExactMatcher(value));
        return this;
    }

}
