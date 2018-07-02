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
package io.specto.hoverfly.junit.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Request {

    private final List<RequestFieldMatcher> path;

    private final List<RequestFieldMatcher> method;

    private final List<RequestFieldMatcher> destination;

    private final List<RequestFieldMatcher> scheme;

    private final Map<String, List<RequestFieldMatcher>> query;

    private final List<RequestFieldMatcher> deprecatedQuery;

    private final List<RequestFieldMatcher> body;

    private final Map<String, List<RequestFieldMatcher>> headers;

    private final Map<String, String> requiresState;

    @JsonCreator
    public Request(@JsonProperty("path") List<RequestFieldMatcher> path,
                   @JsonProperty("method") List<RequestFieldMatcher> method,
                   @JsonProperty("destination") List<RequestFieldMatcher> destination,
                   @JsonProperty("scheme") List<RequestFieldMatcher> scheme,
                   @JsonProperty("query") Map<String, List<RequestFieldMatcher>> query,
                   @JsonProperty("deprecatedQuery") List<RequestFieldMatcher> deprecatedQuery,
                   @JsonProperty("body") List<RequestFieldMatcher> body,
                   @JsonProperty("headers") Map<String, List<RequestFieldMatcher>> headers,
                   @JsonProperty("requiresState") Map<String, String> requiresState) {
        this.path = path;
        this.method = method;
        this.destination = destination;
        this.scheme = scheme;
        this.query = query;
        this.deprecatedQuery = deprecatedQuery;
        this.body = body;
        this.headers = headers;
        this.requiresState = requiresState;
    }

    public List<RequestFieldMatcher> getPath() {
        return path;
    }

    public List<RequestFieldMatcher> getMethod() {
        return method;
    }

    public List<RequestFieldMatcher> getDestination() {
        return destination;
    }

    public List<RequestFieldMatcher> getScheme() {
        return scheme;
    }

    public Map<String, List<RequestFieldMatcher>> getQuery() {
        return query;
    }

    public List<RequestFieldMatcher> getDeprecatedQuery() {
        return deprecatedQuery;
    }

    public List<RequestFieldMatcher> getBody() {
        return body;
    }

    public Map<String, List<RequestFieldMatcher>> getHeaders() {
        return headers;
    }

    public Map<String, String> getRequiresState() {
        return requiresState;
    }

    public static class Builder {

        private List<RequestFieldMatcher> path;
        private List<RequestFieldMatcher> method;
        private List<RequestFieldMatcher> destination;
        private List<RequestFieldMatcher> scheme;
        private Map<String, List<RequestFieldMatcher>> query;
        private List<RequestFieldMatcher> body;
        private List<RequestFieldMatcher> deprecatedQuery;
        private Map<String, List<RequestFieldMatcher>> headers;
        private Map<String, String> requiresState;

        public Request.Builder path(List<RequestFieldMatcher> path) {
            this.path = path;
            return this;
        }

        public Request.Builder method(List<RequestFieldMatcher> method) {
            this.method = method;
            return this;
        }

        public Request.Builder destination(List<RequestFieldMatcher> destination) {
            this.destination = destination;
            return this;
        }

        public Request.Builder scheme(List<RequestFieldMatcher> scheme) {
            this.scheme = scheme;
            return this;
        }

        public Request.Builder query(Map<String, List<RequestFieldMatcher>> query) {
            this.query = query;
            return this;
        }

        public Request.Builder deprecatedQuery(List<RequestFieldMatcher> deprecatedQuery) {
            this.deprecatedQuery = deprecatedQuery;
            return this;
        }

        public Request.Builder body(List<RequestFieldMatcher> body) {
            this.body = body;
            return this;
        }

        public Request.Builder headers(Map<String, List<RequestFieldMatcher>> headers) {
            this.headers = headers;
            return this;
        }

        public Request.Builder requiresState(Map<String, String> requiresState) {
            this.requiresState = requiresState;
            return this;
        }

        public Request build() {
            return new Request(path, method, destination, scheme, query, deprecatedQuery, body, headers, requiresState);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return ToStringBuilder.reflectionToString(this);
        }
    }


}