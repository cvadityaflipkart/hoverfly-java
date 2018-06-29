package io.specto.hoverfly.junit.core.model;

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestFieldMatcher<T> {

    private final MatcherType matcher;
    private final T value;

    @JsonCreator
    public RequestFieldMatcher(@JsonProperty("matcher") MatcherType matcher,
                               @JsonProperty("value") T value) {
        this.matcher = matcher;
        this.value = value;
    }

    public MatcherType getMatcher() {
        return matcher;
    }

    public T getValue() {
        return value;
    }

    public static RequestFieldMatcher newExactMatcher(String value) {
        return new RequestFieldMatcher<>(MatcherType.EXACT, value);
    }

    public static RequestFieldMatcher newGlobMatcher(String value) {
        return new RequestFieldMatcher<>(MatcherType.GLOB, value);
    }

    public static RequestFieldMatcher newRegexMatcher(String value) {
        return new RequestFieldMatcher<>(MatcherType.REGEX, value);
    }

    public static RequestFieldMatcher newXmlMatcher(String value) {
        return new RequestFieldMatcher<>(MatcherType.XML, value);
    }

    public static RequestFieldMatcher newXpathMatcher(String value) {
        return new RequestFieldMatcher<>(MatcherType.XPATH, value);
    }

    public static RequestFieldMatcher newJsonMatcher(String value) {
        return new RequestFieldMatcher<>(MatcherType.JSON, value);
    }

    public static RequestFieldMatcher newJsonPathMatch(String value) {
        return new RequestFieldMatcher<>(MatcherType.JSONPATH, value);
    }

    public enum MatcherType {
        EXACT,
        GLOB,
        REGEX,
        XML,
        XPATH,
        JSON,
        JSONPATH;


        @JsonValue
        public String getValue() {
            return name().toLowerCase();
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
        return ToStringBuilder.reflectionToString(this);
    }
}
