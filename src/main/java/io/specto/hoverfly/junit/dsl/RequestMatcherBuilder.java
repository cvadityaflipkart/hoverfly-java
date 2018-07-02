/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * <p>
 * Copyright 2016-2016 SpectoLabs Ltd.
 */
package io.specto.hoverfly.junit.dsl;

import io.specto.hoverfly.junit.core.model.Request;
import io.specto.hoverfly.junit.core.model.RequestFieldMatcher;
import io.specto.hoverfly.junit.core.model.RequestResponsePair;

import java.util.*;
import java.util.stream.Collectors;

import static io.specto.hoverfly.junit.core.model.RequestFieldMatcher.newExactMatcher;
import static io.specto.hoverfly.junit.dsl.matchers.HoverflyMatchers.any;
import static java.util.Collections.singletonList;

/**
 * A builder for {@link Request}
 */
public class RequestMatcherBuilder {

    private StubServiceBuilder invoker;
    private final List<RequestFieldMatcher> method;
    private final List<RequestFieldMatcher> scheme;
    private final List<RequestFieldMatcher> destination;
    private final List<RequestFieldMatcher> path;
    private final Map<String, List<RequestFieldMatcher>> headers = new HashMap<>();
    private final Map<String, String> requiresState = new HashMap<>();
    private Map<String, List<RequestFieldMatcher>> query = new HashMap<>();
    private List<RequestFieldMatcher> body = singletonList(newExactMatcher("")); // default to match on empty body


    RequestMatcherBuilder(final StubServiceBuilder invoker,
                          final StubServiceBuilder.HttpMethod method,
                          final List<RequestFieldMatcher> scheme,
                          final List<RequestFieldMatcher> destination,
                          final List<RequestFieldMatcher> path) {
        this.invoker = invoker;
        this.method = method.getRequestFieldMatcher();
        this.scheme = scheme;
        this.destination = destination;
        this.path = path;
    }

    /**
     * Sets the request body
     * @param body the request body to match on exactly
     * @return the {@link RequestMatcherBuilder} for further customizations
     */
    public RequestMatcherBuilder body(final String body) {
        this.body = singletonList(newExactMatcher(body));
        return this;
    }

    /**
     * Sets the request body using {@link HttpBodyConverter} to match on exactly
     * @param httpBodyConverter custom http body converter
     * @return the {@link RequestMatcherBuilder} for further customizations
     */
    public RequestMatcherBuilder body(HttpBodyConverter httpBodyConverter) {
        this.body = singletonList(newExactMatcher(httpBodyConverter.body()));
        return this;
    }

    public RequestMatcherBuilder body(RequestFieldMatcher matcher) {
        this.body = singletonList(matcher);
        return this;
    }

    public RequestMatcherBuilder anyBody() {
        this.body = null;
        return this;
    }

    /**
     * Sets one request header
     * @param key the header key to match on
     * @param value the header value to match on
     * @return the {@link RequestMatcherBuilder} for further customizations
     */
    public RequestMatcherBuilder header(final String key, final String value) {
        headers.put(key, singletonList(newExactMatcher(value)));
        return this;
    }

    /**
     * Sets a required state
     * @param key state key
     * @param value state value
     * @return the {@link RequestMatcherBuilder} for further customizations
     */
    public RequestMatcherBuilder withState(final String key, final String value) {
        requiresState.put(key, value);
        return this;
    }

    /**
     * Sets the request query
     * @param key the query params key to match on
     * @param values the query params values to match on
     * @return the {@link RequestMatcherBuilder} for further customizations
     */
    public RequestMatcherBuilder queryParam(final String key, final Object... values) {
        if (values.length == 0 ) {
            query.put(key, singletonList(any()));
        } else {
            // TODO until we implement an array matcher, hoverfly currently match on array values that are joined by semicolon
            query.put(key, singletonList(newExactMatcher(Arrays.stream(values)
                    .map(Object::toString)
                    .collect(Collectors.joining(";")))));
        }
        return this;
    }

    public RequestMatcherBuilder queryParam(final String key, final RequestFieldMatcher value) {
        query.put(key, singletonList(value));
        return this;
    }

    public RequestMatcherBuilder anyQueryParams() {
        query = null;
        return this;
    }

    /**
     * Sets the expected response
     * @param responseBuilder the builder for response
     * @return the {@link StubServiceBuilder} for chaining the next {@link RequestMatcherBuilder}
     * @see ResponseBuilder
     */
    public StubServiceBuilder willReturn(final ResponseBuilder responseBuilder) {
        Request request = this.build();
        return invoker
                .addRequestResponsePair(new RequestResponsePair(request, responseBuilder.build()))
                .addDelaySetting(request, responseBuilder);
    }

    public Request build() {

        // TODO upgrade, as it has builder, the constructor should probably private
        return new Request(path, method, destination, scheme, query, body, headers, requiresState);
    }

}
